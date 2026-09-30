package com.example.tes24.service;

import com.example.tes24.dto.WaitingStatusResponse;

public interface MainService {
    WaitingStatusResponse getWaitingStatus(Long waitingIdx, String idVal);
}
