package com.instant.doctor.payment;

public class Data {
    private String pNumber;
    private String sessionId;
    private String userId;

    public Data(String pNumber, String sessionId, String userId) {
        this.pNumber = pNumber;
        this.sessionId = sessionId;
        this.userId = userId;
    }

    public Data() {
    }

    public String getpNumber() {
        return pNumber;
    }

    public void setpNumber(String pNumber) {
        this.pNumber = pNumber;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
