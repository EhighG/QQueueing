package com.qqueueing.main.waiting;

import com.fasterxml.jackson.databind.JsonNode;
import com.qqueueing.main.support.QueueApiTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("대기열 HTTP API")
class WaitingQueueApiTest extends QueueApiTestSupport {

    private static final String UUID_PATTERN = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    @Test
    @DisplayName("줄 서기: 순번은 앞 인원 + 1이 되고 대기 인원이 늘어난다")
    void enqueueGivesOrderAndGrowsQueue() throws Exception {
        RegisteredQueue queue = registerQueue();

        Enqueued first = enqueue(queue);
        assertThat(first.myOrder()).isEqualTo(1);
        assertThat(order(queue, first.waiterId())).isEqualTo(new OrderStatus("WAITING", 1, 1, null));

        Enqueued second = enqueue(queue);
        Enqueued third = enqueue(queue);
        assertThat(second.myOrder()).isEqualTo(2);
        assertThat(third.myOrder()).isEqualTo(3);
        assertThat(order(queue, first.waiterId())).isEqualTo(new OrderStatus("WAITING", 1, 3, null));
        assertThat(order(queue, third.waiterId())).isEqualTo(new OrderStatus("WAITING", 3, 3, null));
        assertThat(waitingInfo(queue)).isEqualTo(new WaitingInfo(0, 3));

        // 대기자 ID는 서버가 발급한 무작위 값이다
        assertThat(List.of(first.waiterId(), second.waiterId(), third.waiterId()))
                .allMatch(id -> id.matches(UUID_PATTERN))
                .doesNotHaveDuplicates();
        assertThat(first.queueId()).isEqualTo(queue.id());
    }

    @Test
    @DisplayName("이탈: 앞사람이 이탈하면 뒷사람 순번이 바로 줄어든다(100번째에서 앞의 50명이 이탈하면 50번째)")
    void leavingAheadShortensOrder() throws Exception {
        RegisteredQueue queue = registerQueue();
        List<Enqueued> waiters = enqueue(queue, 100);
        String last = waiters.get(99).waiterId();
        assertThat(order(queue, last)).isEqualTo(new OrderStatus("WAITING", 100, 100, null));

        for (Enqueued waiter : waiters.subList(0, 50)) {
            leave(queue, waiter.waiterId());
        }

        assertThat(order(queue, last)).isEqualTo(new OrderStatus("WAITING", 50, 50, null));
        // 이탈한 대기자는 대기자 없음이 된다
        assertThat(order(queue, waiters.get(0).waiterId())).isEqualTo(new OrderStatus("NOT_FOUND", 0, 50, null));
    }

    @Test
    @DisplayName("입장: 입장 처리 뒤 뒷사람 순번이 줄고, 입장한 대기자는 통과 토큰을 한 번만 받으며 그 토큰은 한 번만 쓰인다")
    void admissionShortensOrderAndIssuesSingleUseToken() throws Exception {
        RegisteredQueue queue = registerQueue();
        List<Enqueued> waiters = enqueue(queue, 103);

        admit(); // 초당 100명

        assertThat(order(queue, waiters.get(100).waiterId())).isEqualTo(new OrderStatus("WAITING", 1, 3, null));
        assertThat(order(queue, waiters.get(102).waiterId())).isEqualTo(new OrderStatus("WAITING", 3, 3, null));
        assertThat(waitingInfo(queue)).isEqualTo(new WaitingInfo(100, 3));

        OrderStatus entered = order(queue, waiters.get(0).waiterId());
        assertThat(entered.status()).isEqualTo("ENTERED");
        assertThat(entered.token()).isNotBlank();
        // 통과 토큰은 한 번만 발급된다
        assertThat(order(queue, waiters.get(0).waiterId())).isEqualTo(new OrderStatus("NOT_FOUND", 0, 3, null));

        assertThat(pass(entered.token())).isEqualTo(TARGET_PAGE);
        // 같은 토큰은 다시 쓸 수 없다
        assertThat(pass(entered.token())).contains("invalid token").doesNotContain(TARGET_PAGE);
    }

    @Test
    @DisplayName("입장 뒤 이탈: 입장한 뒤에 이탈을 요청해도 통과 토큰은 유효하다")
    void leavingAfterAdmissionKeepsToken() throws Exception {
        RegisteredQueue queue = registerQueue();
        Enqueued beforeToken = enqueue(queue);
        Enqueued afterToken = enqueue(queue);
        admit();

        // 통과 토큰을 받기 전에 이탈을 요청한다
        leave(queue, beforeToken.waiterId());
        OrderStatus first = order(queue, beforeToken.waiterId());
        assertThat(first.status()).isEqualTo("ENTERED");
        assertThat(pass(first.token())).isEqualTo(TARGET_PAGE);

        // 통과 토큰을 받은 뒤에 이탈을 요청한다
        OrderStatus second = order(queue, afterToken.waiterId());
        assertThat(second.status()).isEqualTo("ENTERED");
        leave(queue, afterToken.waiterId());
        assertThat(pass(second.token())).isEqualTo(TARGET_PAGE);
    }

    @Test
    @DisplayName("식별: 모르는 대기자 ID나 예전 테스트용 값으로는 통과 토큰을 받을 수 없다")
    void unknownOrLegacyValuesGetNoToken() throws Exception {
        RegisteredQueue queue = registerQueue();
        Enqueued waiter = enqueue(queue);
        admit(); // 입장 기록이 있는 대기열에서 확인한다

        for (String unknown : List.of(UUID.randomUUID().toString(), "ipStringValueForTest", "127.0.0.11")) {
            assertThat(order(queue, unknown)).isEqualTo(new OrderStatus("NOT_FOUND", 0, 0, null));
        }
        assertThat(pass("testTokenStringValue")).contains("invalid token").doesNotContain(TARGET_PAGE);

        // 실제로 입장한 대기자의 입장 기록은 그대로 남아 있다
        assertThat(order(queue, waiter.waiterId()).status()).isEqualTo("ENTERED");
    }

    @Test
    @DisplayName("활성화: 이미 활성인 대기열을 활성화해도 줄은 유지된다")
    void activatingActiveQueueKeepsLine() throws Exception {
        RegisteredQueue queue = registerQueue();
        Enqueued first = enqueue(queue);
        Enqueued second = enqueue(queue);

        activate(queue);

        assertThat(order(queue, first.waiterId())).isEqualTo(new OrderStatus("WAITING", 1, 2, null));
        assertThat(order(queue, second.waiterId())).isEqualTo(new OrderStatus("WAITING", 2, 2, null));
    }

    @Test
    @DisplayName("활성화: 비활성에서 활성으로 바뀌면 빈 줄에서 시작한다")
    void reactivationStartsWithEmptyLine() throws Exception {
        RegisteredQueue queue = registerQueue();
        Enqueued before = enqueue(queue);
        enqueue(queue);

        deactivate(queue);
        activate(queue);

        assertThat(order(queue, before.waiterId())).isEqualTo(new OrderStatus("NOT_FOUND", 0, 0, null));
        Enqueued after = enqueue(queue);
        assertThat(after.myOrder()).isEqualTo(1);
        assertThat(order(queue, after.waiterId())).isEqualTo(new OrderStatus("WAITING", 1, 1, null));
    }

    @Test
    @DisplayName("대상 URL 접속: 활성이면 대기 페이지로, 비활성이면 한 번만 쓰는 통과 토큰과 함께 원래 페이지로 보낸다")
    void enterRedirectsByActiveState() throws Exception {
        RegisteredQueue queue = registerQueue();
        assertThat(enter(queue)).contains("/waiting/queue-page?Target-URL=" + queue.targetUrl());

        deactivate(queue);
        String location = enter(queue);
        assertThat(location).contains("/waiting/page-req?token=");
        String token = tokenOf(location);
        assertThat(pass(token)).isEqualTo(TARGET_PAGE);
        assertThat(pass(token)).contains("invalid token").doesNotContain(TARGET_PAGE);
    }

    @Test
    @DisplayName("대기열 식별자: 등록 정보와 줄 서기 응답은 대기열을 등록 정보 id로 부르고 파티션 번호·토픽 이름을 담지 않는다")
    void queueIsCalledByRegistrationId() throws Exception {
        RegisteredQueue queue = registerQueue();

        JsonNode detail = registration(queue);
        JsonNode listed = registrations().get(0);
        for (JsonNode node : List.of(detail, listed)) {
            assertThat(node.get("id").asText()).isEqualTo(queue.id());
            assertThat(node.has("partitionNo")).isFalse();
            assertThat(node.has("topicName")).isFalse();
        }

        JsonNode enqueued = enqueueResult(queue);
        assertThat(enqueued.get("queueId").asText()).isEqualTo(queue.id());
        assertThat(enqueued.has("partitionNo")).isFalse();
        String waiterId = enqueued.get("waiterId").asText();

        // 등록되지 않은 대기열 id로 물으면 대기자 없음이다
        RegisteredQueue unknown = new RegisteredQueue("000000000000000000000000", queue.targetUrl());
        assertThat(order(unknown, waiterId)).isEqualTo(new OrderStatus("NOT_FOUND", 0, 0, null));
        // 다른 대기열 id로 보낸 이탈은 이 대기열의 줄을 바꾸지 않는다
        leave(unknown, waiterId);
        assertThat(order(queue, waiterId)).isEqualTo(new OrderStatus("WAITING", 1, 1, null));
        // 대기열 id로 이탈한다
        leave(queue, waiterId);
        assertThat(order(queue, waiterId)).isEqualTo(new OrderStatus("NOT_FOUND", 0, 0, null));
    }

    @Test
    @DisplayName("대기열 수: 21개 이상 등록할 수 있고, 21번째 이후 대기열도 대기열 id로 줄 서기·입장·비활성화가 동작한다")
    void registersMoreThanTwentyQueues() throws Exception {
        List<RegisteredQueue> queues = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            queues.add(registerQueue());
        }

        assertThat(registrations().size()).isEqualTo(25);
        assertThat(queues.stream().map(RegisteredQueue::id).toList()).doesNotHaveDuplicates();

        RegisteredQueue last = queues.get(24);
        Enqueued waiter = enqueue(last);
        assertThat(waiter.queueId()).isEqualTo(last.id());
        assertThat(order(last, waiter.waiterId())).isEqualTo(new OrderStatus("WAITING", 1, 1, null));

        admit();
        assertThat(order(last, waiter.waiterId()).status()).isEqualTo("ENTERED");

        deactivate(last);
        assertThat(enter(last)).contains("/waiting/page-req?token=");
        // 다른 대기열은 그대로 활성이다
        assertThat(enter(queues.get(0))).contains("/waiting/queue-page?Target-URL=");
    }
}
