package com.qqueueing.main.waiting.service;


import com.qqueueing.main.registration.model.Registration;
import com.qqueueing.main.registration.repository.RegistrationRepository;
import com.qqueueing.main.waiting.model.BatchResDto;
import com.qqueueing.main.waiting.model.GetMyOrderResDto;
import com.qqueueing.main.waiting.model.WaitingStatusDto;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;


@Slf4j
@Transactional
@Service
public class WaitingService {

    private final ConsumerConnector consumerConnector;
    private final TargetApiConnector targetApiConnector;
    private final EnterProducer enterProducer;
    private final RegistrationRepository registrationRepository;

    private Set<Integer> activePartitions = new HashSet<>();
    private Map<Integer, WaitingStatusDto> queues = new HashMap<>();
    private Map<String, Integer> partitionNoMapper = new HashMap<>();
    private Map<String, String> targetUrlMapper = new HashMap<>();
    private final KafkaTopicManager kafkaTopicManager;
    private final String SERVER_ORIGIN;
    private final String QUEUE_PAGE_API = "/waiting/queue-page";
    private final String TARGET_PAGE_URI = "/waiting/page-req";
    private final String QUEUE_PAGE_FRONT;
    private final int TOKEN_LEN = 20;
    private final String TOPIC_NAME;
    private final String REPLACE_URL;
    // for test
    @Setter
    private String endpoint = "/waiting";

    public WaitingService(ConsumerConnector consumerConnector, TargetApiConnector targetApiConnector,
                          EnterProducer enterProducer, RegistrationRepository registrationRepository, KafkaTopicManager kafkaTopicManager,
                          @Value("${servers.front}") String queuePageFront,
                          @Value("${servers.main}") String serverOrigin,
                          @Value("${kafka.topic-names.enter}") String topicName,
                          @Value("${servers.replace-url}") String replaceUrl) {
        this.consumerConnector = consumerConnector;
        this.targetApiConnector = targetApiConnector;
        this.enterProducer = enterProducer; // init every partitions
        this.registrationRepository = registrationRepository;
        this.kafkaTopicManager = kafkaTopicManager;
        this.QUEUE_PAGE_FRONT = "http://" + queuePageFront;
        this.SERVER_ORIGIN = serverOrigin;
        this.TOPIC_NAME = topicName;
        this.REPLACE_URL = replaceUrl;
    }

    private void checkTopic() {
        log.info("Start -- checkTopicr");
        Set<String> topics = kafkaTopicManager.getTopics();
        // for Test -- init every time main server restart
        // disable to remove kafka topic without restart zookeeper-server, kafka-broker now
//        if (topics.contains(TOPIC_NAME)) {
//            kafkaTopicManager.deleteTopic(TOPIC_NAME);
//        }
        if (!topics.contains(TOPIC_NAME)) {
            log.info("topic {} not present, create...", TOPIC_NAME);
            kafkaTopicManager.createTopic(TOPIC_NAME);
        }
        log.info("End -- checkTopic");
    }

    /**
     * kafka producing test
     */
    public Object send(Integer partitionNo, long key, String message) {
        return enterProducer.send(message, key, partitionNo);
    }

    /**
     * 대기열 서버가 비정상 종료 후 다시 켜질 때, 대기열을 다시 활성화
     */
    @PostConstruct
    public void initQueues() {
        // init kafka topic
        checkTopic(); // 카프카 토픽 (있으면 지우고) 생성
        enterProducer.initialize(); // 토픽 내 0~19번 파티션까지 초기화

        List<Registration> queues = registrationRepository.findAll();
        queues.forEach(r -> {
                    partitionNoMapper.put(r.getTargetUrl(), r.getPartitionNo());
                    if (r.getIsActive()) activate(r);
                });
    }

    public void addUrlPartitionMapping(String targetUrl) {
        Registration registration = registrationRepository.findByTargetUrl(targetUrl);
        if (registration != null) {
            partitionNoMapper.put(registration.getTargetUrl(), registration.getPartitionNo());
        }
    }

    /**
     * 대기열 실행 중 상태 변경 시, 호출 필요
     */
    public void activate(Registration registration) {
        int partitionNo = registration.getPartitionNo();
        activePartitions.add(partitionNo);
        queues.put(partitionNo, new WaitingStatusDto(partitionNo, registration.getTargetUrl(), 0, 0));
        // update mongodb data
        registration.setIsActive(true);
        registrationRepository.save(registration);
//        // re-init partition
        consumerConnector.clearPartition(partitionNo);
        log.info("activation end");
        // re-init auto increment client order
        enterProducer.activate(partitionNo);
    }

    public void activate(int partitionNo) {
        Registration registration = registrationRepository.findByPartitionNo(partitionNo);
        activate(registration);
    }

    /**
     * 대기열 실행 중 상태 변경 시, 호출 필요
     * @param partitionNo
     */
    public void deactivate(int partitionNo) {
        if (!activePartitions.contains(partitionNo)) {
            return;
        }
        Registration registration = registrationRepository.findByPartitionNo(partitionNo);

        activePartitions.remove(partitionNo);
        queues.remove(partitionNo);

        // update mongodb data
        registration.setIsActive(false);
        registrationRepository.save(registration);
    }

    /**
     * 대기열 적용된 요청 시, 처음 거치는 메소드
     * @param targetUrl
     * @return redirect url; queue-page req api url || target-page req api url
     */
    public URI enter(String targetUrl) {
        log.info("targetUrl = {}", targetUrl);
        log.info("activePartitions = {}", activePartitions);
        Integer partitionNo = partitionNoMapper.get(targetUrl);
        if (partitionNo == null) {
            log.error("wrong target url; proxy path applied to wrong target");
        }
        log.info("partitionNo = {}", partitionNo);
        UriComponentsBuilder uriBuilder;
        if (activePartitions.contains(partitionNo)) { // 대기 필요
            log.info("대기 필요 - 대기 페이지로 redirect 응답 반환");
//            uriBuilder = UriComponentsBuilder.fromUriString(SERVER_ORIGIN + QUEUE_PAGE_API)
//                    .queryParam("Target-URL", targetUrl);
//            return uriBuilder.build().toUri();
            return URI.create(SERVER_ORIGIN + QUEUE_PAGE_API + "?Target-URL=" + targetUrl);
        } else { // 대기 불필요
            log.info("대기 불필요 - 타겟 페이지로 redirect 응답 반환");
            String tempToken = createTempToken(targetUrl); // 토큰 생성
            uriBuilder = UriComponentsBuilder.fromUriString(SERVER_ORIGIN + TARGET_PAGE_URI)
                    .queryParam("token", tempToken);
            return uriBuilder.build().toUri();
        }
    }

    public String getQueuePage(String targetUrl, HttpServletRequest request) {
        String html = targetApiConnector.forwardToWaitingPage(QUEUE_PAGE_FRONT, targetUrl, request).getBody();

        return parseHtmlPage(QUEUE_PAGE_FRONT, html);
    }

    /**
     * 실제 대기열 진입 메소드. 항상 대기가 필요한 경우에만 들어옴
     * @param targetUrl
     * @return
     */
    public Object enqueue(String targetUrl, HttpServletRequest request) {
        log.info("targetUrl = {}", targetUrl);
        Integer partitionNo = partitionNoMapper.get(targetUrl);

//         카프카에 요청자 ip 저장 후, 대기 정보 반환
        return enterProducer.send(extractClientIp(request), (long) partitionNo, partitionNo);
    }


    private String createTempToken(String targetUrl) {
        String tempToken = RandomStringUtils.randomAlphanumeric(TOKEN_LEN);
        while (targetUrlMapper.get(tempToken) != null) {
            tempToken = RandomStringUtils.randomAlphanumeric(TOKEN_LEN);
        }
        targetUrlMapper.put(tempToken, targetUrl);
        return tempToken;
    }

    private String extractClientIp(HttpServletRequest request) {
        // 요청이 프록시 등을 거쳤을 때, 원래 ip주소를 담는 헤더
        String ip = request.getHeader("X-Forwarded-For");
        log.info("client ip(X-Forwarded-For) = {}", ip);
        if (ip != null) return ip;

        String remoteAddr = request.getRemoteAddr();
        log.info("request.remoteAddr = {}", remoteAddr);
        return remoteAddr;
    }

    public void out(int partitionNo, Long order) {
        // outList는 '내 앞에 나간 사람 수' 를 알기 위해 쓰이므로, 정렬된 채로 유지
        try {
            List<Long> outList = queues.get(partitionNo).getOutList();
            int insertIdx = -(Collections.binarySearch(outList, order) + 1);
            outList.add(insertIdx, order);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GetMyOrderResDto getMyOrder(int partitionNo, Long oldOrder, String ip, HttpServletRequest request) {
        WaitingStatusDto waitingStatus = queues.get(partitionNo);
        Set<String> doneSet = waitingStatus.getDoneSet();
        List<Long> outList = waitingStatus.getOutList();
        int lastOffset = waitingStatus.getLastOffset();
        int outCntInFront = - (Collections.binarySearch(outList, oldOrder) + 1);
        Long myOrder = oldOrder - outCntInFront - lastOffset; // newOrder
        GetMyOrderResDto result = new GetMyOrderResDto(myOrder, waitingStatus.getTotalQueueSize());
        if (doneSet.contains(ip)) { // waiting done
            log.info("ip addr {} requested, and return tempToken");
            doneSet.remove(ip);
            result.setToken(createTempToken(waitingStatus.getTargetUrl()));
        }
        return result;
    }

    /**
     * 타겟 프론트 페이지 포워딩 메소드
     */
    public String forward(String token, HttpServletRequest request) {
        String targetUrl = targetUrlMapper.get(token);
        log.info("forward target Url = {}", targetUrl);
        if (targetUrl == null) {
            throw new IllegalArgumentException("invalid token");
        }
        targetUrlMapper.remove(token);
        targetUrl = REPLACE_URL + extractEndpoint(targetUrl);
        log.info("targetUrl = {}", targetUrl);

        String html = targetApiConnector.forward(targetUrl, request).getBody();
        log.info("forwording result = \n\n{}", html);
        return html;
    }

    private String parseHtmlPage(String targetUrl, String html) {
//        String[] urlSplitList = targetUrl.split("qqueueing-frontend:3000");
//        String endPoint = urlSplitList[1];
        // parse target url(external request -> internal)
        log.info("html : " + html);

        String newHtml = html.replace("/_next", endpoint + "/_next");
        log.info("newHtml : " + newHtml);
        return newHtml;
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

    @Async
    @Scheduled(cron = "0/5 * * * * *") // 매 분 0초부터, 5초마다
    public void getNext() {
        try {
            if (activePartitions.isEmpty()) {
                return;
            }
            Map<Integer, BatchResDto> response = consumerConnector.getNext(activePartitions); // 대기 완료된 ip 목록을 가져온다.
            System.out.println("response = " + response);
            Set<Integer> partitionNos = response.keySet();

            for (Integer partitionNo : partitionNos) {
                WaitingStatusDto waitingStatus = queues.get(partitionNo);

                BatchResDto batchRes = response.get(partitionNo);
                waitingStatus.getDoneSet()
                        .addAll(batchRes.getCurDoneList());
                waitingStatus.setLastOffset(batchRes.getLastOffset());
                waitingStatus.setTotalQueueSize(batchRes.getTotalQueueSize());
            }
            cleanUpOutList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 대기 끝난 사람들 삭제
     */
    private void cleanUpOutList() {
        for (int partitionNo : queues.keySet()) {
            WaitingStatusDto waigtingStatus = queues.get(partitionNo);
            int lastOffset = waigtingStatus.getLastOffset();
            waigtingStatus.setOutList(
                    waigtingStatus.getOutList().stream()
                            .filter(i -> i > lastOffset)
                            .toList()
            );
        }
    }
}
