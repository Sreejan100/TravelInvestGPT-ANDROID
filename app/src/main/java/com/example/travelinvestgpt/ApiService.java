package com.example.travelinvestgpt;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("android_register")
    Call<ResponseBody> registerUser(@Body User user);


    @POST("android_login")
    Call<ResponseBody> loginUser(@Body User user);

}
