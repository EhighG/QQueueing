package com.qqueueing.main.waiting;

import com.qqueueing.main.support.QueueApiTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 입장 속도(분당 입장 인원). 시계는 테스트마다 TestClockConfig.START에서 시작하고, admit()마다 1초씩 간다.
 * 아래 기대값은 시작 초와 상관없이 성립한다. 분당 80명의 3초 몫(80 × 3 / 60 = 4), 분당 30명의 2초 몫(30 × 2 / 60 = 1)처럼
 * 나눠떨어지는 구간만 단언하기 때문이다.
 */
@DisplayName("입장 속도 HTTP API")
class AdmissionRateApiTest extends QueueApiTestSupport {

    @Test
    @DisplayName("분당 80이면 3초에 4명, 60초에 80명이 입장하고, 정각에 맞추지 않은 60초 구간에도 80명이 입장한다")
    void rate80AdmitsFourPerThreeSecondsAndEightyPerMinute() throws Exception {
        RegisteredQueue queue = registerQueueWithRate(80);
        enqueue(queue, 100);

        admit(3);
        assertThat(waitingInfo(queue)).isEqualTo(new WaitingInfo(4, 96));

        admit(57); // 처음부터 60초
        assertThat(waitingInfo(queue)).isEqualTo(new WaitingInfo(80, 20));

        admit(3); // 4번째 초부터 63번째 초까지 60초 동안에도 84 - 4 = 80명
        assertThat(waitingInfo(queue)).isEqualTo(new WaitingInfo(84, 16));
    }

    @Test
    @DisplayName("분당 30이면 2초에 1명이 입장한다")
    void rate30AdmitsOnePerTwoSeconds() throws Exception {
        RegisteredQueue queue = registerQueueWithRate(30);
        List<Enqueued> waiters = enqueue(queue, 40);

        admit(2);
        assertThat(waitingInfo(queue)).isEqualTo(new WaitingInfo(1, 39));
        assertThat(order(queue, waiters.get(0).waiterId()).status()).isEqualTo("ENTERED");
        assertThat(order(queue, waiters.get(1).waiterId())).isEqualTo(new OrderStatus("WAITING", 1, 39, null));

        admit(2);
        assertThat(waitingInfo(queue)).isEqualTo(new WaitingInfo(2, 38));

        admit(56); // 처음부터 60초
        assertThat(waitingInfo(queue)).isEqualTo(new WaitingInfo(30, 10));
    }

    @Test
    @DisplayName("대기자가 몫보다 적었던 초의 남은 몫과, 입장 처리가 건너뛴 초의 몫은 다음 초로 넘어가지 않는다")
    void unusedShareIsNotCarriedOver() throws Exception {
        RegisteredQueue queue = registerQueueWithRate(180); // 1초에 3명

        admit(); // 줄이 비어 있던 초의 몫 3명은 버린다
        enqueue(queue, 1);
        admit(); // 몫 3명 가운데 1명만 입장하고 남은 2명 몫은 버린다
        assertThat(waitingInfo(queue)).isEqualTo(new WaitingInfo(1, 0));

        enqueue(queue, 10);
        admit(); // 앞 초에서 남은 몫 없이 이번 초의 몫 3명만 입장한다
        assertThat(waitingInfo(queue)).isEqualTo(new WaitingInfo(4, 7));

        clock.advance(Duration.ofSeconds(5)); // 5초 동안 입장 처리가 돌지 않았다
        admit(); // 건너뛴 5초의 몫은 넘어오지 않는다
        assertThat(waitingInfo(queue)).isEqualTo(new WaitingInfo(7, 4));
    }

    @Test
    @DisplayName("입력하지 않으면 6000(1초에 100명)이 쓰이고, 0 이하는 등록과 수정에서 모두 거부된다")
    void defaultRateIsUsedAndNonPositiveRateIsRejected() throws Exception {
        // 입력하지 않으면(필드를 빼거나 null로 보내면) 6000이 저장된다
        RegisteredQueue omitted = registerQueue();
        String nullTargetUrl = newTargetUrl();
        RegisteredQueue nullRate = registered(postQueueWithRate(nullTargetUrl, "null"), nullTargetUrl);
        assertThat(processingPerMinute(omitted)).isEqualTo(6000);
        assertThat(processingPerMinute(nullRate)).isEqualTo(6000);

        // 6000은 1초에 100명이다
        enqueue(omitted, 150);
        admit();
        assertThat(waitingInfo(omitted)).isEqualTo(new WaitingInfo(100, 50));

        // 0 이하는 등록에서 거부되고, 등록 정보가 생기지 않는다
        for (String rejected : List.of("0", "-1")) {
            assertThat(rejectedMessage(postQueueWithRate(newTargetUrl(), rejected))).contains("분당 입장 인원");
        }
        assertThat(registrations().size()).isEqualTo(2);

        // 0 이하는 수정에서도 거부되고, 입장 속도는 그대로다
        for (String rejected : List.of("0", "-1")) {
            assertThat(rejectedMessage(patchQueueWithRate(omitted, rejected))).contains("분당 입장 인원");
        }
        assertThat(processingPerMinute(omitted)).isEqualTo(6000);

        // 수정에서 입장 속도를 보내지 않으면 지금 값을 그대로 둔다
        patchQueue(omitted, "{\"serviceName\":\"renamed\"}").andExpect(status().isOk());
        assertThat(registration(omitted).get("serviceName").asText()).isEqualTo("renamed");
        assertThat(processingPerMinute(omitted)).isEqualTo(6000);
    }

    @Test
    @DisplayName("고친 입장 속도가 다음 입장 처리부터 반영된다")
    void updatedRateAppliesFromNextAdmission() throws Exception {
        RegisteredQueue queue = registerQueue(); // 기본 입장 속도: 1초에 100명
        enqueue(queue, 110);
        admit();
        assertThat(waitingInfo(queue)).isEqualTo(new WaitingInfo(100, 10));

        updateRate(queue, 120); // 1초에 2명
        assertThat(processingPerMinute(queue)).isEqualTo(120);
        admit();
        assertThat(waitingInfo(queue)).isEqualTo(new WaitingInfo(102, 8));

        updateRate(queue, 180); // 1초에 3명
        admit();
        assertThat(waitingInfo(queue)).isEqualTo(new WaitingInfo(105, 5));
    }

    @Test
    @DisplayName("정수가 아니거나 int 범위를 넘는 입장 속도는 등록과 수정에서 모두 거부되고, 1과 int 최댓값은 받는다")
    void nonIntegerRateIsRejectedAndBoundaryRatesAreAccepted() throws Exception {
        RegisteredQueue queue = registerQueueWithRate(80);

        for (String rejected : List.of("1.5", "\"80\"", "2147483648", "\"abc\"")) {
            assertThat(rejectedMessage(postQueueWithRate(newTargetUrl(), rejected))).contains("분당 입장 인원");
            assertThat(rejectedMessage(patchQueueWithRate(queue, rejected))).contains("분당 입장 인원");
        }
        assertThat(registrations().size()).isEqualTo(1);
        assertThat(processingPerMinute(queue)).isEqualTo(80);

        // 1은 받는다
        updateRate(queue, 1);
        assertThat(processingPerMinute(queue)).isEqualTo(1);

        // int 최댓값도 받고, 입장 인원 계산이 넘치지 않아 줄에 있는 대기자가 모두 입장한다
        updateRate(queue, Integer.MAX_VALUE);
        assertThat(processingPerMinute(queue)).isEqualTo(Integer.MAX_VALUE);
        for (int second = 1; second <= 3; second++) {
            enqueue(queue, 5);
            admit();
            assertThat(waitingInfo(queue)).isEqualTo(new WaitingInfo(5L * second, 0));
        }
    }

    @Test
    @DisplayName("이전 버전 등록 정보: 입장 속도가 없거나 0으로 저장된 대기열은 기본값(분당 6000명)으로 입장시키고, 입장 속도를 보내지 않는 수정도 받는다")
    void legacyRegistrationWithoutRateUsesDefault() throws Exception {
        RegisteredQueue missing = registerLegacyQueue(null);
        RegisteredQueue zero = registerLegacyQueue(0);
        enqueue(missing, 150);
        enqueue(zero, 150);

        admit();
        assertThat(waitingInfo(missing)).isEqualTo(new WaitingInfo(100, 50));
        assertThat(waitingInfo(zero)).isEqualTo(new WaitingInfo(100, 50));

        // 입장 속도를 보내지 않는 수정은 받는다
        patchQueue(zero, "{\"serviceName\":\"renamed\"}").andExpect(status().isOk());
        assertThat(registration(zero).get("serviceName").asText()).isEqualTo("renamed");

        // 입장 속도를 고치면 그 값을 쓴다
        updateRate(missing, 60); // 1초에 1명
        admit();
        assertThat(waitingInfo(missing)).isEqualTo(new WaitingInfo(101, 49));
        assertThat(waitingInfo(zero)).isEqualTo(new WaitingInfo(150, 0));
    }
}
