package com.instant.doctor.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    //TODO replace with server key
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAls-AhHc:APA91bFnmtSOWChX6Yt_u0cWUqubIybbnxHoKgoUGQoXuuaEU963-5JIfDH2cXJv-5jl3zhXf0ppq4qLbkbCH_5vGc807kdTWIMb8p49A91wXtJ1Ydm11ThxTvZFhFSh-Qzdq1hhy6bt"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotication(@Body Sender body);




}
