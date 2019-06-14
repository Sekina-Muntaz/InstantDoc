package com.instant.doctor.models;

public class MpesaResponse {

    private String MerchantRequestID;
    private String ResponseDescription;
    private String CustomerMessage;
    private int ResponseCode;

    public MpesaResponse() {
    }

    public MpesaResponse(String merchantRequestID, String responseDescription, String customerMessage, int responseCode) {
        MerchantRequestID = merchantRequestID;
        ResponseDescription = responseDescription;
        CustomerMessage = customerMessage;
        ResponseCode = responseCode;
    }

    public String getMerchantRequestID() {
        return MerchantRequestID;
    }

    public void setMerchantRequestID(String merchantRequestID) {
        MerchantRequestID = merchantRequestID;
    }

    public String getResponseDescription() {
        return ResponseDescription;
    }

    public void setResponseDescription(String responseDescription) {
        ResponseDescription = responseDescription;
    }

    public String getCustomerMessage() {
        return CustomerMessage;
    }

    public void setCustomerMessage(String customerMessage) {
        CustomerMessage = customerMessage;
    }

    public int getResponseCode() {
        return ResponseCode;
    }

    public void setResponseCode(int responseCode) {
        ResponseCode = responseCode;
    }
}
