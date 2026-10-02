package com.qqueueing.main.waiting;

import com.fasterxml.jackson.databind.JsonNode;
import com.qqueueing.main.registration.model.Registration;
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
    @DisplayName("비활성화: 줄에 남은 대기자가 모두 다음 순번 조회에서 통과 토큰을 받는다")
    void deactivationAdmitsAllRemainingWaiters() throws Exception {
        RegisteredQueue queue = registerQueue();
        // 기본 입장 속도로 1초에 입장하는 인원(100)보다 많이 세운다
        List<Enqueued> waiters = enqueue(queue, 150);

        deactivate(queue);

        // 한 번에 모두 입장해 줄이 비고, 입장 인원에 더해진다
        assertThat(waitingInfo(queue)).isEqualTo(new WaitingInfo(150, 0));
        List<String> tokens = new ArrayList<>();
        for (Enqueued waiter : waiters) {
            OrderStatus status = order(queue, waiter.waiterId());
            assertThat(status.token()).isNotBlank();
            assertThat(status).isEqualTo(new OrderStatus("ENTERED", 0, 0, status.token()));
            tokens.add(status.token());
        }
        assertThat(tokens).doesNotHaveDuplicates();
        assertThat(pass(tokens.get(0))).isEqualTo(TARGET_PAGE);
        assertThat(pass(tokens.get(149))).isEqualTo(TARGET_PAGE);
    }

    @Test
    @DisplayName("비활성화: 비활성화된 뒤에 줄을 선 대기자도 다음 순번 조회에서 바로 통과 토큰을 받는다")
    void waiterQueuedAfterDeactivationEntersOnNextOrder() throws Exception {
        RegisteredQueue queue = registerQueue();
        deactivate(queue);

        // 대기 페이지를 연 채 비활성화되면 대기 페이지의 줄 서기는 비활성 대기열로 들어온다
        Enqueued late = enqueue(queue);
        OrderStatus status = order(queue, late.waiterId());

        assertThat(status.token()).isNotBlank();
        assertThat(status).isEqualTo(new OrderStatus("ENTERED", 0, 0, status.token()));
        assertThat(pass(status.token())).isEqualTo(TARGET_PAGE);
        // 통과 토큰은 한 번만 발급되고, 입장 인원에 더해진다
        assertThat(order(queue, late.waiterId())).isEqualTo(new OrderStatus("NOT_FOUND", 0, 0, null));
        assertThat(waitingInfo(queue)).isEqualTo(new WaitingInfo(1, 0));
    }

    @Test
    @DisplayName("재활성화: 비활성화한 뒤 남은 대기자가 토큰을 받아 가기 전에 다시 활성화하면 이전 대기자는 대기자 없음이 되고, 비활성 기간에 발급된 통과 토큰으로는 통과할 수 없으며, 입장 인원은 0이다")
    void reactivationDiscardsEntriesAndTokensFromInactivePeriod() throws Exception {
        RegisteredQueue queue = registerQueue();
        List<Enqueued> waiters = enqueue(queue, 3);
        deactivate(queue);

        // 비활성 기간에 발급된 통과 토큰 두 가지: 남은 대기자가 순번 조회로 받은 토큰, 대상 URL 접속으로 바로 받은 토큰
        OrderStatus received = order(queue, waiters.get(0).waiterId());
        assertThat(received.status()).isEqualTo("ENTERED");
        String enterToken = tokenOf(enter(queue));
        assertThat(waitingInfo(queue)).isEqualTo(new WaitingInfo(3, 0));

        activate(queue);

        // 토큰을 받아 가지 않은 대기자(1, 2번째)를 포함해 이전 대기자는 모두 대기자 없음이다
        for (Enqueued waiter : waiters) {
            assertThat(order(queue, waiter.waiterId())).isEqualTo(new OrderStatus("NOT_FOUND", 0, 0, null));
        }
        assertThat(pass(received.token())).contains("invalid token").doesNotContain(TARGET_PAGE);
        assertThat(pass(enterToken)).contains("invalid token").doesNotContain(TARGET_PAGE);
        assertThat(waitingInfo(queue)).isEqualTo(new WaitingInfo(0, 0));
        // 다시 활성화한 뒤에 온 사람은 줄을 선다
        assertThat(enter(queue)).contains("/waiting/queue-page?Target-URL=" + queue.targetUrl());
    }

    @Test
    @DisplayName("삭제 후 재등록: 같은 대상 URL로 다시 등록하면 빈 줄에서 시작하고, 이전 대기자 ID는 대기자 없음, 이전 통과 토큰으로는 통과할 수 없다")
    void reRegisteringAfterDeletionStartsWithEmptyLine() throws Exception {
        RegisteredQueue previous = registerQueue();
        List<Enqueued> entered = enqueue(previous, 2);
        admit();
        OrderStatus received = order(previous, entered.get(0).waiterId()); // 통과 토큰을 받고 쓰지 않는다
        assertThat(received.status()).isEqualTo("ENTERED");
        Enqueued waiting = enqueue(previous); // 줄에 남는다
        assertThat(waitingInfo(previous)).isEqualTo(new WaitingInfo(2, 1));

        deleteQueue(previous);
        RegisteredQueue renewed = registerQueue(previous.targetUrl());

        assertThat(renewed.id()).isNotEqualTo(previous.id());
        assertThat(waitingInfo(renewed)).isEqualTo(new WaitingInfo(0, 0));
        for (String waiterId : List.of(entered.get(0).waiterId(), entered.get(1).waiterId(), waiting.waiterId())) {
            assertThat(order(previous, waiterId)).isEqualTo(new OrderStatus("NOT_FOUND", 0, 0, null));
            assertThat(order(renewed, waiterId)).isEqualTo(new OrderStatus("NOT_FOUND", 0, 0, null));
        }
        assertThat(pass(received.token())).contains("invalid token").doesNotContain(TARGET_PAGE);

        Enqueued first = enqueue(renewed);
        assertThat(first.queueId()).isEqualTo(renewed.id());
        assertThat(first.myOrder()).isEqualTo(1);
        assertThat(order(renewed, first.waiterId())).isEqualTo(new OrderStatus("WAITING", 1, 1, null));
    }

    @Test
    @DisplayName("기동 정리: MongoDB 등록 정보에 없는 대기열의 Redis 상태만 지우고, 등록된 대기열의 상태는 그대로 둔다")
    void startupCleanupRemovesOnlyUnregisteredQueueState() throws Exception {
        RegisteredQueue orphan = registerQueue();
        RegisteredQueue kept = registerQueue();
        // 두 대기열 모두 입장 기록, 쓰지 않은 통과 토큰, 줄, 누적 입장 인원을 남긴다
        List<Enqueued> orphanEntered = enqueue(orphan, 2);
        List<Enqueued> keptEntered = enqueue(kept, 2);
        admit();
        OrderStatus orphanReceived = order(orphan, orphanEntered.get(0).waiterId());
        OrderStatus keptReceived = order(kept, keptEntered.get(0).waiterId());
        Enqueued orphanWaiting = enqueue(orphan);
        Enqueued keptWaiting = enqueue(kept);

        // docker compose down처럼 MongoDB의 등록 정보만 사라진 뒤 main이 기동한다
        Registration document = dropRegistrationDocument(orphan);
        runStartupCleanup();
        // 정리 결과를 HTTP API로 보기 위해 같은 id로 등록 정보만 되살린다
        restoreRegistrationDocument(document);

        assertThat(order(orphan, orphanWaiting.waiterId())).isEqualTo(new OrderStatus("NOT_FOUND", 0, 0, null));
        assertThat(order(orphan, orphanEntered.get(1).waiterId())).isEqualTo(new OrderStatus("NOT_FOUND", 0, 0, null));
        assertThat(pass(orphanReceived.token())).contains("invalid token").doesNotContain(TARGET_PAGE);
        assertThat(waitingInfo(orphan)).isEqualTo(new WaitingInfo(0, 0));

        // 등록된 대기열의 줄, 입장 기록, 통과 토큰, 누적 입장 인원은 그대로다
        assertThat(order(kept, keptWaiting.waiterId())).isEqualTo(new OrderStatus("WAITING", 1, 1, null));
        assertThat(order(kept, keptEntered.get(1).waiterId()).status()).isEqualTo("ENTERED");
        assertThat(pass(keptReceived.token())).isEqualTo(TARGET_PAGE);
        assertThat(waitingInfo(kept)).isEqualTo(new WaitingInfo(2, 1));
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
