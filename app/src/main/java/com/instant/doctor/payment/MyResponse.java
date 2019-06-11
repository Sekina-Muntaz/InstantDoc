package com.instant.doctor.payment;

public class MyResponse {
    private int responseCode;

    public MyResponse(int responseCode) {
        this.responseCode = responseCode;
    }

    public MyResponse() {
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
}
