package com.example.tes24.qqueueing.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Q2ServerResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 5619234618L;

    String clientId;
    String clientKey;
    String userId;
    String userKey;
    Long userSequence;
    Long currentQueueSize;
    Long capacity;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public Long getUserSequence() {
        return userSequence;
    }

    public void setUserSequence(Long userSequence) {
        this.userSequence = userSequence;
    }

    public Long getCurrentQueueSize() {
        return currentQueueSize;
    }

    public void setCurrentQueueSize(Long currentQueueSize) {
        this.currentQueueSize = currentQueueSize;
    }

    public Long getCapacity() {
        return capacity;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Q2ServerResponse that = (Q2ServerResponse) o;
        return Objects.equals(clientId, that.clientId) && Objects.equals(clientKey, that.clientKey) && Objects.equals(userId, that.userId) && Objects.equals(userKey, that.userKey) && Objects.equals(userSequence, that.userSequence) && Objects.equals(currentQueueSize, that.currentQueueSize) && Objects.equals(capacity, that.capacity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, clientKey, userId, userKey, userSequence, currentQueueSize, capacity);
    }

    @Override
    public String toString() {
        return "Q2ServerResponse{" +
                "clientId='" + clientId + '\'' +
                ", clientKey='" + clientKey + '\'' +
                ", userId='" + userId + '\'' +
                ", userKey='" + userKey + '\'' +
                ", userSequence=" + userSequence +
                ", currentQueueSize=" + currentQueueSize +
                ", capacity=" + capacity +
                '}';
    }
}
