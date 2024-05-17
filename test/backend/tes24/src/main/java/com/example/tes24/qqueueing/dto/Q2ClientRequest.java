package com.example.tes24.qqueueing.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Q2ClientRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 5619234617L;

    String clientId;
    String clientKey;
    String userId;
    String userKey;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Q2ClientRequest that = (Q2ClientRequest) o;
        return Objects.equals(clientId, that.clientId) && Objects.equals(clientKey, that.clientKey) && Objects.equals(userId, that.userId) && Objects.equals(userKey, that.userKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, clientKey, userId, userKey);
    }

    @Override
    public String toString() {
        return "Q2ClientRequest{" +
                "clientId='" + clientId + '\'' +
                ", clientKey='" + clientKey + '\'' +
                ", userId='" + userId + '\'' +
                ", userKey='" + userKey + '\'' +
                '}';
    }
}
