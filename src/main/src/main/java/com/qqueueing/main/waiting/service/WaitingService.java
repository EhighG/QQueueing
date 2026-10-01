package com.qqueueing.main.waiting.service;


import com.qqueueing.main.registration.model.GetWaitingInfoResDto;
import com.qqueueing.main.registration.model.Registration;
import com.qqueueing.main.registration.repository.RegistrationRepository;
import com.qqueueing.main.waiting.model.EnqueueResponse;
import com.qqueueing.main.waiting.model.WaitingOrderResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;


@Slf4j
@Service
public class WaitingService {

    @Value("${servers.parsing}")
    private String serverUrl;

    private final QueueRegistry queueRegistry;
    private final QueueStore queueStore;
    private final TargetApiConnector targetApiConnector;
    private final RegistrationRepository registrationRepository;

    private final String SERVER_ORIGIN;
    private final String QUEUE_PAGE_API = "/waiting/queue-page";
    private final String TARGET_PAGE_URI = "/waiting/page-req";
    private final String QUEUE_PAGE_FRONT;
    private final String REPLACE_URL;
    // for test
    @Setter
    private String endpoint = "/waiting";

    public WaitingService(QueueRegistry queueRegistry, QueueStore queueStore,
                          TargetApiConnector targetApiConnector, RegistrationRepository registrationRepository,
                          @Value("${servers.front}") String queuePageFront,
                          @Value("${servers.main}") String serverOrigin,
                          @Value("${servers.replace-url}") String replaceUrl) {
        this.queueRegistry = queueRegistry;
        this.queueStore = queueStore;
        this.targetApiConnector = targetApiConnector;
        this.registrationRepository = registrationRepository;
        this.QUEUE_PAGE_FRONT = "http://" + queuePageFront;
        this.SERVER_ORIGIN = serverOrigin;
        this.REPLACE_URL = replaceUrl;
    }

    /**
     * 관리자 화면의 대기 정보: 누적 입장 인원과 대기 인원
     */
    public GetWaitingInfoResDto getWaitingInfo(String queueId) {
        return new GetWaitingInfoResDto(queueStore.admittedCount(queueId), queueStore.waitingCount(queueId));
    }

    /**
     * 등록 정보를 저장(등록·수정·활성 상태 변경)한 뒤 호출한다.
     */
    public void onRegistrationSaved(Registration saved) {
        queueRegistry.put(saved);
    }

    /**
     * 등록 정보를 지운 뒤 호출한다. 그 대기열의 Redis 상태를 모두 지운다.
     */
    public void onRegistrationDeleted(String queueId) {
        queueRegistry.remove(queueId);
        queueStore.deleteAll(queueId);
    }

    /**
     * 비활성에서 활성으로 바뀔 때만 줄과 대기 번호를 비운다. 이미 활성이면 아무것도 바꾸지 않는다.
     */
    public void activate(String queueId) {
        Registration registration = findRegistration(queueId);
        if (Boolean.TRUE.equals(registration.getIsActive())) {
            return;
        }
        queueStore.resetLine(registration.getId());
        registration.setIsActive(true);
        queueRegistry.put(registrationRepository.save(registration));
    }

    /**
     * 비활성으로 바꾼다. 줄에 남은 대기자는 그대로 둔다(입장 처리는 활성 대기열만 한다).
     */
    public void deactivate(String queueId) {
        Registration registration = findRegistration(queueId);
        if (!Boolean.TRUE.equals(registration.getIsActive())) {
            return;
        }
        registration.setIsActive(false);
        queueRegistry.put(registrationRepository.save(registration));
    }

    /**
     * 활성 상태를 바꿀 등록 정보를 MongoDB에서 새로 읽는다. 캐시(QueueRegistry)의 객체는 바꾸지 않는다.
     */
    private Registration findRegistration(String queueId) {
        return registrationRepository.findById(queueId)
                .orElseThrow(() -> new IllegalArgumentException("wrong queueId"));
    }

    /**
     * 대기열 적용된 요청 시, 처음 거치는 메소드
     * @param targetUrl
     * @return redirect url; queue-page req api url || target-page req api url
     */
    public URI enter(String targetUrl) {
        Registration registration = queueRegistry.findByTargetUrl(targetUrl)
                .orElseThrow(() -> new IllegalArgumentException("wrong targetUrl"));

        if (Boolean.TRUE.equals(registration.getIsActive())) { // 대기 필요
            return URI.create(SERVER_ORIGIN + QUEUE_PAGE_API + "?Target-URL=" + targetUrl);
        }
        // 대기 불필요: 통과 토큰을 바로 발급한다
        String token = queueStore.issueToken(registration.getId());
        return UriComponentsBuilder.fromUriString(SERVER_ORIGIN + TARGET_PAGE_URI)
                .queryParam("token", token)
                .build().toUri();
    }

    public String getQueuePage(String targetUrl) {
        boolean active = queueRegistry.findByTargetUrl(targetUrl)
                .map(r -> Boolean.TRUE.equals(r.getIsActive()))
                .orElse(false);
        if (!active) {
            throw new RuntimeException("wrong targetUrl");
        }

        String html = targetApiConnector.forwardToWaitingPage(QUEUE_PAGE_FRONT, targetUrl).getBody();
        return parseHtmlPage(QUEUE_PAGE_FRONT, html);
    }

    /**
     * 줄 서기. 추측할 수 없는 대기자 ID를 발급해 줄에 넣는다.
     */
    public EnqueueResponse enqueue(String targetUrl) {
        Registration registration = queueRegistry.findByTargetUrl(targetUrl)
                .orElseThrow(() -> new IllegalArgumentException("wrong targetUrl"));
        String waiterId = UUID.randomUUID().toString();
        long myOrder = queueStore.enqueue(registration.getId(), waiterId);
        return new EnqueueResponse(registration.getId(), waiterId, myOrder);
    }

    /**
     * 순번 조회. 대기열이나 대기자를 찾지 못하면 NOT_FOUND(대기자 없음)를 돌려준다.
     */
    public WaitingOrderResponse getMyOrder(String queueId, String waiterId) {
        Registration registration = queueRegistry.findById(queueId).orElse(null);
        if (registration == null) {
            return WaitingOrderResponse.notFound(0);
        }
        if (waiterId == null || waiterId.isBlank()) {
            return WaitingOrderResponse.notFound(queueStore.waitingCount(registration.getId()));
        }
        return queueStore.status(registration.getId(), waiterId);
    }

    /**
     * 이탈. 줄에 있는 대기자만 뺀다. 입장 기록과 통과 토큰은 그대로 둔다.
     */
    public void out(String queueId, String waiterId) {
        if (waiterId == null || waiterId.isBlank()) {
            return;
        }
        queueRegistry.findById(queueId)
                .ifPresent(registration -> queueStore.leave(registration.getId(), waiterId));
    }

    /**
     * 타겟 프론트 페이지 포워딩 메소드. 통과 토큰은 쓰는 즉시 지운다.
     */
    public String forward(String token) {
        String queueId = QueueStore.queueIdOf(token);
        Registration registration = queueId == null ? null : queueRegistry.findById(queueId).orElse(null);
        if (registration == null || !queueStore.consumeToken(queueId, token)) {
            throw new IllegalArgumentException("invalid token");
        }

        // make internal request url
        String targetUrl = REPLACE_URL + extractEndpoint(registration.getTargetUrl());
        return targetApiConnector.forward(targetUrl).getBody();
    }

    private String parseHtmlPage(String targetUrl, String html) {

        html = html.replace("/_next", endpoint + "/_next");
        html = html.replace("favicon", "waiting/favicon");

        return html;
    }

    private String extractEndpoint(String targetUrl) {
        int startPos = findIndex(targetUrl, '/', 3);
        return targetUrl.substring(startPos);
    }

    private int findIndex(String s, char c, int cnt) {
        int end = s.length();
        for (int i = 0; i < end; i++) {
            if (s.charAt(i) == c && --cnt == 0) {
                return i;
            }
        }
        return -1;
    }

    public ResponseEntity<?> parsing(String address, String scheme) {

        String serverPort = ":3000";

        HttpHeaders headers = new HttpHeaders();

        String url = address.split("/waiting")[0];

        if(address.contains("image")) {

            String[] imageAddressSplit1 = address.split(url);
            String imageAddressSplit1result = imageAddressSplit1[1];

            String[] imageAddressSplit2 = imageAddressSplit1result.split("/_next");
            String imageAddressSplit2result = imageAddressSplit2[0];

            address = address.replace(imageAddressSplit2result, serverPort);

            String[] imageAddressSplit3 = address.split("url=");

            String[] imageAddressSplit4 = imageAddressSplit3[1].split("%2F_next");
            String imageAddress = imageAddressSplit4[0];

            address = address.replace(imageAddress, "");

            String imageUrl = serverUrl + serverPort + "/_next" + address.split("/_next")[1];

            try {
                byte[] imageBytes = getImageBytes(imageUrl);

                headers.setContentType(MediaType.IMAGE_PNG);

                return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
//                return ResponseEntity.ok().body(imageBytes);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // favicon 일 경우
        if(address.contains("favicon")) {

            String serverURL = serverUrl + "/favicon.ico";
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(serverURL, String.class);

            String result = response.getBody().replace("favicon", "waiting/favicon");

            headers.setContentType(new MediaType("image", "x-icon", StandardCharsets.UTF_8));
            return ResponseEntity.ok().headers(headers).body(result);
        }

        String[] addressSplit = address.split("_next");
        String targetUrl = "/_next" + addressSplit[1];

        String[] endPointSplit = address.split(url);
        String[] endPointSplit2 = endPointSplit[1].split("/_next");
        String endPoint = endPointSplit2[0];

        String serverURL = serverUrl + serverPort + targetUrl;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(serverURL, String.class);

        String result = response.getBody().replace("/_next", endPoint + "/_next");

        // css 일 경우
        if(address.contains("css")) {

            headers.setContentType(new MediaType("text", "css", StandardCharsets.UTF_8));
            return ResponseEntity.ok().headers(headers).body(result);
        }

        headers.setContentType(new MediaType("text", "html", StandardCharsets.UTF_8));
        return ResponseEntity.ok().headers(headers).body(result);

    }

    public static byte[] getImageBytes(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try {
            InputStream inputStream = connection.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        } finally {
            connection.disconnect();
        }
    }
}
