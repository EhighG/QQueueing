package com.qqueueing.main.registration.service;

/**
 * 등록·수정 요청의 값이 규칙에 맞지 않을 때 던진다. RegistrationController가 HTTP 400으로 응답한다.
 */
public class InvalidRegistrationException extends RuntimeException {

    public InvalidRegistrationException(String message) {
        super(message);
    }
}
