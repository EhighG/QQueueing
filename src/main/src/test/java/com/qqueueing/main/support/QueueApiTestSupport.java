package com.qqueueing.main.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qqueueing.main.registration.model.Registration;
import com.qqueueing.main.registration.repository.RegistrationRepository;
import com.qqueueing.main.registration.service.ScriptExecService;
import com.qqueueing.main.waiting.service.AdmissionService;
import com.qqueueing.main.waiting.service.OrphanQueueCleaner;
import com.qqueueing.main.waiting.service.QueueRegistry;
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
import org.springframework.test.web.servlet.ResultActions;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 대기열 HTTP API 테스트의 공통 틀.
 * main을 프로세스 안에서 띄우고(MockMvc), MongoDB와 Redis는 Testcontainers로 띄운다.
 * 가짜로 바꾸는 것은 바깥 세계와 닿는 두 곳뿐이다: 대상 사이트 포워딩(TargetApiConnector)과 에이전트 FIFO 쓰기(ScriptExecService).
 * 1초마다 도는 입장 처리는 끄고, 테스트가 admit()으로 직접 부른다. 현재 시각은 MutableClock으로 정한다.
 * main이 기동할 때 하는 정리도 테스트가 runStartupCleanup()으로 다시 부를 수 있다.
 * 대기열은 등록 정보 id(대기열 id)로 부른다. 입장 속도를 정하지 않고 등록한 대기열은 기본 입장 속도(분당 6000명, 1초에 100명)를 쓴다.
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

    @Autowired
    private OrphanQueueCleaner orphanQueueCleaner;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private QueueRegistry queueRegistry;

    @MockBean
    protected TargetApiConnector targetApiConnector;

    @MockBean
    protected ScriptExecService scriptExecService;

    @BeforeEach
    void resetQueues() throws Exception {
        clock.setInstant(TestClockConfig.START);
        when(targetApiConnector.forward(anyString())).thenReturn(ResponseEntity.ok(TARGET_PAGE));
        // 등록 정보는 API로 지워 main의 캐시와 함께 비우고, 남은 Redis 데이터는 통째로 비운다.
        for (JsonNode registration : registrations()) {
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
        return registerQueue(newTargetUrl());
    }

    /** 겹치지 않는 대상 URL. */
    protected static String newTargetUrl() {
        return "https://localhost/test/" + UUID.randomUUID();
    }

    /** 대상 URL을 정해 등록한다(삭제 후 같은 대상 URL로 다시 등록할 때). */
    protected RegisteredQueue registerQueue(String targetUrl) throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("targetUrl", targetUrl, "serviceName", "test"));
        JsonNode result = json(mockMvc.perform(post("/queue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn()).get("result");
        return new RegisteredQueue(result.get("id").asText(), targetUrl);
    }

    /** 등록 정보 목록(GET /queue의 result 배열). */
    protected JsonNode registrations() throws Exception {
        return json(mockMvc.perform(get("/queue")).andExpect(status().isOk()).andReturn()).get("result");
    }

    /** 등록 정보 상세(GET /queue/{id}의 result). */
    protected JsonNode registration(RegisteredQueue queue) throws Exception {
        return json(mockMvc.perform(get("/queue/{id}", queue.id())).andExpect(status().isOk()).andReturn())
                .get("result");
    }

    /** 입장 속도(분당 입장 인원)를 정해 등록한다. */
    protected RegisteredQueue registerQueueWithRate(int processingPerMinute) throws Exception {
        String targetUrl = newTargetUrl();
        return registered(postQueueWithRate(targetUrl, String.valueOf(processingPerMinute)), targetUrl);
    }

    /**
     * 등록 요청(POST /queue)을 보낸다. processingPerMinute 자리에는 JSON 값을 글자 그대로 넣는다(예: "80", "null", "1.5", "\"80\"").
     * 응답을 확인하지 않고 돌려준다.
     */
    protected ResultActions postQueueWithRate(String targetUrl, String processingPerMinuteJson) throws Exception {
        String body = "{\"targetUrl\":\"" + targetUrl + "\",\"serviceName\":\"test\",\"processingPerMinute\":"
                + processingPerMinuteJson + "}";
        return mockMvc.perform(post("/queue").contentType(MediaType.APPLICATION_JSON).content(body));
    }

    /** 등록 응답이 201인지 확인하고 등록한 대기열을 돌려준다. */
    protected RegisteredQueue registered(ResultActions response, String targetUrl) throws Exception {
        JsonNode result = json(response.andExpect(status().isCreated()).andReturn()).get("result");
        return new RegisteredQueue(result.get("id").asText(), targetUrl);
    }

    /** 수정 요청(PATCH /queue/{id})을 JSON 본문 그대로 보낸다. 응답을 확인하지 않고 돌려준다. */
    protected ResultActions patchQueue(RegisteredQueue queue, String jsonBody) throws Exception {
        return mockMvc.perform(patch("/queue/{id}", queue.id()).contentType(MediaType.APPLICATION_JSON).content(jsonBody));
    }

    /** processingPerMinute만 담은 수정 요청을 보낸다. 값은 JSON 글자 그대로 넣는다. */
    protected ResultActions patchQueueWithRate(RegisteredQueue queue, String processingPerMinuteJson) throws Exception {
        return patchQueue(queue, "{\"processingPerMinute\":" + processingPerMinuteJson + "}");
    }

    /** 입장 속도를 고친다. 수정이 성공(200)해야 한다. */
    protected void updateRate(RegisteredQueue queue, int processingPerMinute) throws Exception {
        patchQueueWithRate(queue, String.valueOf(processingPerMinute)).andExpect(status().isOk());
    }

    /** 등록 정보 상세(GET /queue/{id})의 processingPerMinute. 값이 없으면 null. */
    protected Integer processingPerMinute(RegisteredQueue queue) throws Exception {
        JsonNode value = registration(queue).get("processingPerMinute");
        return value == null || value.isNull() ? null : value.asInt();
    }

    /** 거부 응답이 HTTP 400인지 확인하고 본문의 message를 돌려준다. */
    protected String rejectedMessage(ResultActions response) throws Exception {
        return json(response.andExpect(status().isBadRequest()).andReturn()).get("message").asText();
    }

    protected void deleteQueue(RegisteredQueue queue) throws Exception {
        mockMvc.perform(delete("/queue/{id}", queue.id())).andExpect(status().isOk());
    }

    protected void activate(RegisteredQueue queue) throws Exception {
        mockMvc.perform(post("/waiting/{queueId}/activate", queue.id())).andExpect(status().isOk());
    }

    protected void deactivate(RegisteredQueue queue) throws Exception {
        mockMvc.perform(post("/waiting/{queueId}/deactivate", queue.id())).andExpect(status().isOk());
    }

    protected WaitingInfo waitingInfo(RegisteredQueue queue) throws Exception {
        JsonNode result = json(mockMvc.perform(get("/queue/{id}/info", queue.id()))
                .andExpect(status().isOk())
                .andReturn()).get("result");
        return new WaitingInfo(result.get("enterCnt").asLong(), result.get("totalQueueSize").asLong());
    }

    // ---- 대기 페이지 API ----

    /** 줄 서기 응답(POST /waiting의 result)을 그대로 돌려준다. */
    protected JsonNode enqueueResult(RegisteredQueue queue) throws Exception {
        return json(mockMvc.perform(post("/waiting").header("Target-URL", queue.targetUrl()))
                .andExpect(status().isOk())
                .andReturn()).get("result");
    }

    protected Enqueued enqueue(RegisteredQueue queue) throws Exception {
        JsonNode result = enqueueResult(queue);
        return new Enqueued(result.get("queueId").asText(), result.get("waiterId").asText(),
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
        String body = objectMapper.writeValueAsString(Map.of("queueId", queue.id(), "waiterId", waiterId));
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
                        .param("queueId", queue.id())
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

    /** admit()을 seconds번 부른다. 1초씩 시계를 진행하며 입장 처리를 돌린다. */
    protected void admit(int seconds) {
        for (int i = 0; i < seconds; i++) {
            admit();
        }
    }

    // ---- 기동 정리 ----

    /** main이 기동할 때 하는 정리(등록 정보가 없는 대기열의 Redis 상태 삭제)를 한 번 돌린다. */
    protected void runStartupCleanup() {
        orphanQueueCleaner.cleanUp();
    }

    /**
     * MongoDB에서 등록 정보만 지운다. docker compose down으로 MongoDB만 비워진 상황을 흉내 낸다.
     * main의 캐시와 Redis 상태는 그대로 둔다. 지운 등록 정보를 돌려준다.
     */
    protected Registration dropRegistrationDocument(RegisteredQueue queue) {
        Registration registration = registrationRepository.findById(queue.id()).orElseThrow();
        registrationRepository.deleteById(queue.id());
        return registration;
    }

    /** dropRegistrationDocument로 지운 등록 정보를 같은 id로 되살린다. 정리 결과를 HTTP API로 보기 위해서만 쓴다. */
    protected void restoreRegistrationDocument(Registration registration) {
        registrationRepository.save(registration);
    }

    /**
     * 입장 속도 검증이 생기기 전에 등록된 대기열을 흉내 낸다. 입장 속도를 processingPerMinute(null이나 0)로 MongoDB에 바로 저장하고,
     * main이 기동할 때처럼 등록 정보 캐시를 다시 읽는다.
     */
    protected RegisteredQueue registerLegacyQueue(Integer processingPerMinute) {
        String targetUrl = newTargetUrl();
        Registration saved = registrationRepository.save(
                new Registration(null, targetUrl, 0, processingPerMinute, "legacy", null, true));
        queueRegistry.load();
        return new RegisteredQueue(saved.getId(), targetUrl);
    }

    private JsonNode json(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    /** 등록한 대기열. id는 등록 정보 id이고, 대기열 API는 이 값으로 대기열을 부른다. */
    public record RegisteredQueue(String id, String targetUrl) {
    }

    public record Enqueued(String queueId, String waiterId, long myOrder) {
    }

    public record OrderStatus(String status, long myOrder, long totalQueueSize, String token) {
    }

    public record WaitingInfo(long enterCnt, long totalQueueSize) {
    }
}
