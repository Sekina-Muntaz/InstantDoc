package com.instant.doctor.payment;

import com.instant.doctor.models.MpesaResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PaymentService {

    @POST("/lipanampesa")
    Call<MpesaResponse> makePayment(@Body Data data);

    @POST("/checkpayment")
    Call<PaymentResponse> confirmPayment(@Body ConfirmPayment payment);
}
