package com.instant.doctor.payment;

public class ConfirmPayment {

    private String MerchantRequestID;

    public ConfirmPayment() {
    }

    public ConfirmPayment(String merchantRequestID) {
        MerchantRequestID = merchantRequestID;
    }

    public String getMerchantRequestID() {
        return MerchantRequestID;
    }

    public void setMerchantRequestID(String merchantRequestID) {
        MerchantRequestID = merchantRequestID;
    }
}
