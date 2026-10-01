package com.qqueueing.main.waiting.service;

import com.qqueueing.main.waiting.model.WaitingOrderResponse;
import com.qqueueing.main.waiting.model.WaitingStatus;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * 대기열 상태를 Redis에 두고 다루는 유일한 곳이다. 대기열은 등록 정보 id(queueId)로 구분한다.
 *
 * <pre>
 * qq:queue:{queueId}:line      Sorted Set  줄. 원소는 대기자 ID, 점수는 대기 번호
 * qq:queue:{queueId}:seq       String      대기 번호 카운터
 * qq:queue:{queueId}:entered   Hash        입장 기록. 필드는 대기자 ID, 필드마다 만료 시간
 * qq:queue:{queueId}:tokens    Hash        이 대기열이 발급한 통과 토큰. 필드는 토큰, 필드마다 만료 시간
 * qq:queue:{queueId}:admitted  String      누적 입장 인원
 * </pre>
 *
 * 여러 명령을 묶는 연산은 Lua 스크립트로 원자적으로 처리한다(ADR-0001).
 * 필드별 만료 시간(HEXPIRE)은 Redis 7.4 이상에서 동작한다.
 */
@Component
public class QueueStore {

    /** 입장 기록과 통과 토큰의 안전용 만료 시간. 받아 가지 않은 항목이 영구히 쌓이는 것만 막는다. */
    static final Duration SAFETY_TTL = Duration.ofHours(24);

    private static final String KEY_PREFIX = "qq:queue:";
    private static final char TOKEN_SEPARATOR = '.';

    /** 줄 서기: 대기 번호를 받아 줄에 넣고 0부터 센 순위를 돌려준다. */
    private static final DefaultRedisScript<Long> ENQUEUE_SCRIPT = new DefaultRedisScript<>("""
            local waitNo = redis.call('INCR', KEYS[2])
            redis.call('ZADD', KEYS[1], waitNo, ARGV[1])
            return redis.call('ZRANK', KEYS[1], ARGV[1])
            """, Long.class);

    /**
     * 순번 조회: 줄에 있으면 순번과 대기 인원을, 입장 기록에 있으면 기록을 지우고 통과 토큰을 한 번만 발급한다.
     * 반환: {상태, 순번, 대기 인원[, 통과 토큰]}
     */
    @SuppressWarnings("rawtypes")
    private static final DefaultRedisScript<List> STATUS_SCRIPT = new DefaultRedisScript<>("""
            local size = redis.call('ZCARD', KEYS[1])
            local rank = redis.call('ZRANK', KEYS[1], ARGV[1])
            if rank then
              return {'WAITING', rank + 1, size}
            end
            if redis.call('HDEL', KEYS[2], ARGV[1]) == 1 then
              redis.call('HSET', KEYS[3], ARGV[2], '1')
              redis.call('HEXPIRE', KEYS[3], ARGV[3], 'FIELDS', 1, ARGV[2])
              return {'ENTERED', 0, size, ARGV[2]}
            end
            return {'NOT_FOUND', 0, size}
            """, List.class);

    /** 입장 처리: 줄 앞에서 최대 ARGV[1]명을 꺼내 입장 기록에 넣고 누적 입장 인원을 늘린다. 꺼낸 인원을 돌려준다. */
    private static final DefaultRedisScript<Long> ADMIT_SCRIPT = new DefaultRedisScript<>("""
            local popped = redis.call('ZPOPMIN', KEYS[1], ARGV[1])
            local admitted = 0
            for i = 1, #popped, 2 do
              redis.call('HSET', KEYS[2], popped[i], '1')
              redis.call('HEXPIRE', KEYS[2], ARGV[2], 'FIELDS', 1, popped[i])
              admitted = admitted + 1
            end
            if admitted > 0 then
              redis.call('INCRBY', KEYS[3], admitted)
            end
            return admitted
            """, Long.class);

    /** 통과 토큰 발급: 토큰을 이 대기열의 토큰 목록에 넣고 만료 시간을 건다. */
    private static final DefaultRedisScript<Long> ISSUE_TOKEN_SCRIPT = new DefaultRedisScript<>("""
            redis.call('HSET', KEYS[1], ARGV[1], '1')
            redis.call('HEXPIRE', KEYS[1], ARGV[2], 'FIELDS', 1, ARGV[1])
            return 1
            """, Long.class);

    private final StringRedisTemplate redis;

    public QueueStore(StringRedisTemplate redis) {
        this.redis = redis;
    }

    /** 줄 서기. 줄을 선 직후의 순번(앞 인원 + 1)을 돌려준다. */
    public long enqueue(String queueId, String waiterId) {
        Long rank = redis.execute(ENQUEUE_SCRIPT, List.of(lineKey(queueId), seqKey(queueId)), waiterId);
        return rank + 1;
    }

    /** 순번 조회. 입장한 대기자에게는 이 호출에서 통과 토큰을 한 번만 발급한다. */
    public WaitingOrderResponse status(String queueId, String waiterId) {
        List<?> result = redis.execute(STATUS_SCRIPT,
                List.of(lineKey(queueId), enteredKey(queueId), tokensKey(queueId)),
                waiterId, newToken(queueId), ttlSeconds());
        WaitingStatus status = WaitingStatus.valueOf(String.valueOf(result.get(0)));
        long myOrder = Long.parseLong(String.valueOf(result.get(1)));
        long totalQueueSize = Long.parseLong(String.valueOf(result.get(2)));
        String token = result.size() > 3 ? String.valueOf(result.get(3)) : null;
        return new WaitingOrderResponse(status, myOrder, totalQueueSize, token);
    }

    /** 이탈. 줄에 있는 대기자만 뺀다. 입장 기록과 통과 토큰은 건드리지 않는다. */
    public void leave(String queueId, String waiterId) {
        redis.opsForZSet().remove(lineKey(queueId), waiterId);
    }

    /** 입장 처리. 줄 앞에서 최대 count명을 꺼내 입장 기록에 넣고, 실제로 꺼낸 인원을 돌려준다. */
    public long admit(String queueId, long count) {
        if (count <= 0) {
            return 0;
        }
        Long admitted = redis.execute(ADMIT_SCRIPT,
                List.of(lineKey(queueId), enteredKey(queueId), admittedKey(queueId)),
                String.valueOf(count), ttlSeconds());
        return admitted == null ? 0 : admitted;
    }

    /** 줄을 서지 않고 바로 쓸 통과 토큰을 발급한다(비활성 대기열의 대상 URL 접속). */
    public String issueToken(String queueId) {
        String token = newToken(queueId);
        redis.execute(ISSUE_TOKEN_SCRIPT, List.of(tokensKey(queueId)), token, ttlSeconds());
        return token;
    }

    /** 통과 토큰을 쓴다. 유효하면 지우고 true를 돌려준다. 같은 토큰은 두 번 쓸 수 없다. */
    public boolean consumeToken(String queueId, String token) {
        Long removed = redis.opsForHash().delete(tokensKey(queueId), token);
        return removed != null && removed == 1;
    }

    /** 대기 인원(지금 줄에 있는 대기자 수). */
    public long waitingCount(String queueId) {
        Long size = redis.opsForZSet().zCard(lineKey(queueId));
        return size == null ? 0 : size;
    }

    /** 누적 입장 인원. */
    public long admittedCount(String queueId) {
        String value = redis.opsForValue().get(admittedKey(queueId));
        return value == null ? 0 : Long.parseLong(value);
    }

    /** 대기열을 다시 활성화할 때 줄과 대기 번호를 비운다. */
    public void resetLine(String queueId) {
        redis.delete(List.of(lineKey(queueId), seqKey(queueId)));
    }

    /** 대기열을 삭제할 때 그 대기열의 Redis 상태를 모두 지운다. */
    public void deleteAll(String queueId) {
        redis.delete(List.of(lineKey(queueId), seqKey(queueId), enteredKey(queueId),
                tokensKey(queueId), admittedKey(queueId)));
    }

    /** 통과 토큰에서 발급한 대기열의 id를 꺼낸다. 형식이 맞지 않으면 null. */
    public static String queueIdOf(String token) {
        if (token == null) {
            return null;
        }
        int separator = token.indexOf(TOKEN_SEPARATOR);
        return separator > 0 ? token.substring(0, separator) : null;
    }

    private static String newToken(String queueId) {
        return queueId + TOKEN_SEPARATOR + UUID.randomUUID().toString().replace("-", "");
    }

    private static String ttlSeconds() {
        return String.valueOf(SAFETY_TTL.toSeconds());
    }

    private static String lineKey(String queueId) {
        return KEY_PREFIX + queueId + ":line";
    }

    private static String seqKey(String queueId) {
        return KEY_PREFIX + queueId + ":seq";
    }

    private static String enteredKey(String queueId) {
        return KEY_PREFIX + queueId + ":entered";
    }

    private static String tokensKey(String queueId) {
        return KEY_PREFIX + queueId + ":tokens";
    }

    private static String admittedKey(String queueId) {
        return KEY_PREFIX + queueId + ":admitted";
    }
}
