package com.example.travelinvestgpt;

import com.google.gson.JsonObject;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("android_register")
    Call<JsonObject> registerUser(@Body JsonObject body);


    @POST("android_login")
    Call<JsonObject> loginUser(@Body JsonObject body);

}
