package com.example.tes24.service;

import com.example.tes24.dto.WaitingStatusResponseRecord;
import org.springframework.http.ResponseEntity;

public interface MainService {
    ResponseEntity<WaitingStatusResponseRecord> getWaitingStatus(Long waitingIdx, String idVal);
}
