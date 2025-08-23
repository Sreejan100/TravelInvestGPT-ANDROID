package com.example.travelinvestgpt;

public interface BackendResponseCallback {

    void onSuccess(String imageUrl);

    void onError(String errorMessage);
}
