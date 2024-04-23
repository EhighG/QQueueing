//package com.practice.apiserver.sse;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//import java.io.IOException;
//
//@Slf4j
//@RequiredArgsConstructor
//@Service
//public class SSEService {
//
//    private final EmitterRepository emitterRepository;
//
//    private static final Long DEFAULT_TIMEOUT = 300_000L;
//    private static final long RECONNECTION_TIMEOUT = 3000L;
//
//    public SseEmitter subscribe(String ip) {
//        SseEmitter emitter = createEmitter(ip);
//        return emitter;
//    }
//
//    public SseEmitter get(String key) {
//        return emitterRepository.get(key);
//    }
//
//
//    private SseEmitter createEmitter(String key) {
//        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
//        emitterRepository.save(key, emitter);
//
//        emitter.onCompletion(() -> emitterRepository.delete(key));
//        emitter.onTimeout(() -> emitterRepository.delete(key));
//
//        return emitter;
//    }
//
//    public <T> void sendMessage(String key, T data, String comment, String type) {
//        SseEmitter emitter = emitterRepository.get(key);
//        try {
//            if (emitter == null) {
//                throw new IllegalArgumentException("해당하는 emitter가 없습니다.");
//            }
//            emitter.send(SseEmitter.event()
//                    .id(key)
//                    .name(type)
//                    .reconnectTime(RECONNECTION_TIMEOUT)
//                    .data(data)
//                    .comment(comment));
//        } catch (IOException e) {
//            e.printStackTrace();
//            emitter.completeWithError(e);
//        }
//    }
//}
