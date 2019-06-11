package com.instant.doctor.payment;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PaymentService {

    @POST("/lipanampesa")
    Call<MyResponse> makePayment(@Body Data data);
}
