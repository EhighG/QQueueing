package com.example.tes24.qqueue_module.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Q2ServerResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 5619234618L;

    String clientId;
    String userId;
    Long userSequence;
    Long currentQueueSize;
    Long capacity;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Q2ServerResponse that = (Q2ServerResponse) object;
        return Objects.equals(clientId, that.clientId) && Objects.equals(userId, that.userId) && Objects.equals(userSequence, that.userSequence) && Objects.equals(currentQueueSize, that.currentQueueSize) && Objects.equals(capacity, that.capacity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, userId, userSequence, currentQueueSize, capacity);
    }

    @Override
    public String toString() {
        return "Q2ServerResponse{" +
                "clientId='" + clientId + '\'' +
                ", userId='" + userId + '\'' +
                ", userSequence=" + userSequence +
                ", currentQueueSize=" + currentQueueSize +
                ", capacity=" + capacity +
                '}';
    }
}
