package com.example.travelinvestgpt;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {

    private static final String PREF_NAME = "TravelInvestAPP";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPreferenceManager (Context context) {

        sharedPreferences  = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveUsername(String username){
        editor.putString("UserName", username);
        editor.apply();
    }

    public String getUsername(){
        return sharedPreferences.getString("UserName",null);
    }

    public void saveEmail(String email){
        editor.putString("Email", email);
        editor.apply();

    }

    public String getEmail() {
        return sharedPreferences.getString("Email",null);
    }

    public void saveImage(String imageurl) {

        editor.putString("Image",imageurl);
        editor.apply();
    }

    public String getImage() {

        return sharedPreferences.getString("Image",null);
    }

    public void setLoggedIn(boolean bool){

        editor.putBoolean("isLoggedIn",bool);
        editor.apply();
    }


    public boolean getLoggedIn(){

        return sharedPreferences.getBoolean("isLoggedIn", false);

    }

    public void logout() {
        editor.clear();
        editor.apply();

    }



}
