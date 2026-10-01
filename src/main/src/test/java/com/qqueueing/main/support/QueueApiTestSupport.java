package com.qqueueing.main.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qqueueing.main.registration.service.ScriptExecService;
import com.qqueueing.main.waiting.service.AdmissionService;
import com.qqueueing.main.waiting.service.TargetApiConnector;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 대기열 HTTP API 테스트의 공통 틀.
 * main을 프로세스 안에서 띄우고(MockMvc), MongoDB와 Redis는 Testcontainers로 띄운다.
 * 가짜로 바꾸는 것은 바깥 세계와 닿는 두 곳뿐이다: 대상 사이트 포워딩(TargetApiConnector)과 에이전트 FIFO 쓰기(ScriptExecService).
 * 1초마다 도는 입장 처리는 끄고, 테스트가 admit()으로 직접 부른다. 현재 시각은 MutableClock으로 정한다.
 */
@SpringBootTest(properties = "admission.scheduler.enabled=false")
@AutoConfigureMockMvc
@Import({TestcontainersConfig.class, TestClockConfig.class})
public abstract class QueueApiTestSupport {

    /** 가짜 대상 사이트가 돌려주는 원래 페이지 */
    protected static final String TARGET_PAGE = "<html>target page</html>";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MutableClock clock;

    @Autowired
    private AdmissionService admissionService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @MockBean
    protected TargetApiConnector targetApiConnector;

    @MockBean
    protected ScriptExecService scriptExecService;

    @BeforeEach
    void resetQueues() throws Exception {
        clock.setInstant(TestClockConfig.START);
        when(targetApiConnector.forward(anyString())).thenReturn(ResponseEntity.ok(TARGET_PAGE));
        // 등록 정보는 API로 지워 main의 캐시와 함께 비우고, 남은 Redis 데이터는 통째로 비운다.
        JsonNode registrations = json(mockMvc.perform(get("/queue")).andExpect(status().isOk()).andReturn());
        for (JsonNode registration : registrations.get("result")) {
            mockMvc.perform(delete("/queue/{id}", registration.get("id").asText()))
                    .andExpect(status().isOk());
        }
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });
    }

    // ---- 관리자 API ----

    protected RegisteredQueue registerQueue() throws Exception {
        String targetUrl = "https://localhost/test/" + UUID.randomUUID();
        String body = objectMapper.writeValueAsString(Map.of("targetUrl", targetUrl, "serviceName", "test"));
        JsonNode result = json(mockMvc.perform(post("/queue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn()).get("result");
        return new RegisteredQueue(result.get("id").asText(), result.get("partitionNo").asInt(), targetUrl);
    }

    protected void activate(RegisteredQueue queue) throws Exception {
        mockMvc.perform(post("/waiting/{partitionNo}/activate", queue.partitionNo())).andExpect(status().isOk());
    }

    protected void deactivate(RegisteredQueue queue) throws Exception {
        mockMvc.perform(post("/waiting/{partitionNo}/deactivate", queue.partitionNo())).andExpect(status().isOk());
    }

    protected WaitingInfo waitingInfo(RegisteredQueue queue) throws Exception {
        JsonNode result = json(mockMvc.perform(get("/queue/{id}/info", queue.id()))
                .andExpect(status().isOk())
                .andReturn()).get("result");
        return new WaitingInfo(result.get("enterCnt").asLong(), result.get("totalQueueSize").asLong());
    }

    // ---- 대기 페이지 API ----

    protected Enqueued enqueue(RegisteredQueue queue) throws Exception {
        JsonNode result = json(mockMvc.perform(post("/waiting").header("Target-URL", queue.targetUrl()))
                .andExpect(status().isOk())
                .andReturn()).get("result");
        return new Enqueued(result.get("partitionNo").asInt(), result.get("waiterId").asText(),
                result.get("myOrder").asLong());
    }

    protected List<Enqueued> enqueue(RegisteredQueue queue, int count) throws Exception {
        List<Enqueued> waiters = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            waiters.add(enqueue(queue));
        }
        return waiters;
    }

    protected OrderStatus order(RegisteredQueue queue, String waiterId) throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("partitionNo", queue.partitionNo(), "waiterId", waiterId));
        JsonNode result = json(mockMvc.perform(post("/waiting/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn());
        String token = result.hasNonNull("token") ? result.get("token").asText() : null;
        return new OrderStatus(result.get("status").asText(), result.get("myOrder").asLong(),
                result.get("totalQueueSize").asLong(), token);
    }

    protected void leave(RegisteredQueue queue, String waiterId) throws Exception {
        mockMvc.perform(post("/waiting/out")
                        .param("partitionNo", String.valueOf(queue.partitionNo()))
                        .param("waiterId", waiterId))
                .andExpect(status().isOk());
    }

    /** 대상 URL 접속(nginx가 main의 /waiting/enter로 넘기는 요청). 리다이렉트 주소를 돌려준다. */
    protected String enter(RegisteredQueue queue) throws Exception {
        return mockMvc.perform(get("/waiting/enter").header("Target-URL", queue.targetUrl()))
                .andExpect(status().isFound())
                .andReturn().getResponse().getHeader("Location");
    }

    /** 통과 토큰으로 원래 페이지를 연다. 응답 본문을 돌려준다(성공하면 TARGET_PAGE). */
    protected String pass(String token) throws Exception {
        return mockMvc.perform(get("/waiting/page-req").param("token", token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    protected static String tokenOf(String location) {
        return UriComponentsBuilder.fromUriString(location).build().getQueryParams().getFirst("token");
    }

    // ---- 입장 처리 ----

    /** 시계를 1초 진행하고 입장 처리를 한 번 돌린다. */
    protected void admit() {
        clock.advance(Duration.ofSeconds(1));
        admissionService.admitAll();
    }

    private JsonNode json(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    public record RegisteredQueue(String id, int partitionNo, String targetUrl) {
    }

    public record Enqueued(int partitionNo, String waiterId, long myOrder) {
    }

    public record OrderStatus(String status, long myOrder, long totalQueueSize, String token) {
    }

    public record WaitingInfo(long enterCnt, long totalQueueSize) {
    }
}
