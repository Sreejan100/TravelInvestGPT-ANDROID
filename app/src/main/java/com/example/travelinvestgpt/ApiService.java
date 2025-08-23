package com.example.travelinvestgpt;

import com.google.gson.JsonObject;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("mobile_register")
    Call<JsonObject> registerUser(@Body JsonObject body);


    @POST("mobile_login")
    Call<JsonObject> loginUser(@Body JsonObject body);

    @POST("mobile_profile_delete")
    Call<JsonObject> deleteUser(@Body JsonObject body);

    @POST("mobile_profile_image_upload")
    Call<JsonObject> profileImageChange(@Body JsonObject body);

    @POST("google_authentication")
    Call<JsonObject> googleSignIn(@Body JsonObject body);

}
