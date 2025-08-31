package com.example.travelinvestgpt;

public interface BackendResponseCallback {

    void onSuccess(String imageUrl, String token);

    void onError(String errorMessage);
}
