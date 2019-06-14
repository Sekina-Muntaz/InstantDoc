package com.instant.doctor.payment;

public class PaymentResponse {

    private String message;
    private int success;

    public PaymentResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }
}
