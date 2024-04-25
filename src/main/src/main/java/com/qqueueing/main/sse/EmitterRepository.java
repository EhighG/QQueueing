//package com.practice.apiserver.sse;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Repository;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@RequiredArgsConstructor
//@Repository
//public class EmitterRepository {
//
//    /**
//     * Map(String requesterIp, emitter)
//     */
//    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
//
//    public void save(String key, SseEmitter emitter) {
//        emitters.put(key, emitter);
//    }
//
//    public void delete(String key) {
//        emitters.remove(key);
//    }
//
//    public SseEmitter get(String key) {
//        return emitters.get(key);
//    }
//}
