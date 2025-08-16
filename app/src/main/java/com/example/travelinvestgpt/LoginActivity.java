package com.example.travelinvestgpt;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.media3.common.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText username, email, password;

    private SharedPreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        preferenceManager = new SharedPreferenceManager(getApplicationContext());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Window window = getWindow();
        window.setNavigationBarColor(Color.parseColor("#2852FF"));
        WindowInsetsControllerCompat insetsController = WindowCompat.getInsetsController(window, window.getDecorView());
        if (insetsController != null) {
            insetsController.setAppearanceLightStatusBars(false);
            insetsController.setAppearanceLightNavigationBars(false);// false for light icons on dark background
        }
    }

    public void loginMain(View view){

        username = findViewById(R.id.loginNameText);
        email = findViewById(R.id.loginEmailtext);
        password = findViewById(R.id.loginPasswordtext);

        String usern = username.getText().toString();
        String pass = password.getText().toString();
        String Email = email.getText().toString();



        JsonObject body = new JsonObject();
        body.addProperty("username",usern);
        body.addProperty("email",Email);
        body.addProperty("password",pass);

        ApiService apiService = RetrofitClient.getClient("http://192.168.1.2:5010/").create(ApiService.class);

        apiService.loginUser(body).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response){
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JsonObject responseBody = response.body();
                        // Check for key existence before accessing
                       // Log the JSON

                        String responseUsername="";
                        if(responseBody.has("username") && !responseBody.get("username").isJsonNull())
                        {
                            responseUsername = responseBody.get("username").getAsString();
                        }

                        String responseEmail="";
                        if (responseBody.has("email") && !responseBody.get("email").isJsonNull())
                        {
                            responseEmail = responseBody.get("email").getAsString();
                        }

                        String responseImageUrl="";
                        if(responseBody.has("imageurl")){
                            if (!responseBody.get("imageurl").isJsonNull()){
                                responseImageUrl = responseBody.get("imageurl").getAsString();
                            }
                        }
                        preferenceManager.saveUsername(responseUsername);
                        preferenceManager.saveEmail(responseEmail);
                        preferenceManager.saveImage(responseImageUrl);
                        preferenceManager.setLoggedIn(true);

                        Toast.makeText(LoginActivity.this, "Welcome Back, " + responseUsername + "!", Toast.LENGTH_SHORT).show();

                        Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(loginIntent);
                        finish(); // Finish LoginActivity

                    } catch (Exception e) {
                        e.printStackTrace(); // Log the error for debugging
                        Toast.makeText(LoginActivity.this, "Login successful, but error processing response.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    String errorMessage = "Invalid Credentials";
                    if (response.errorBody() != null) {
                        try {
                            String errorBodyString = response.errorBody().string(); // Read once
                            JsonObject errorJson = new Gson().fromJson(errorBodyString, JsonObject.class);
                            if (errorJson != null && errorJson.has("message")) {
                                errorMessage = errorJson.get("message").getAsString();
                            } else {
                                errorMessage += " (Code: " + response.code() + ")";
                            }
                        } catch (Exception e) { // Catches IOException from string() or JsonSyntaxException
                            errorMessage += " (Error parsing error response)";
                        }
                    } else {
                        errorMessage += " (Code: " + response.code() + ")";
                    }
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t){
                Toast.makeText(LoginActivity.this, "Error: "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } );





    }

    public void registerfunc(View view){

        Intent registerintent = new Intent(this, RegisterActivity.class);
        registerintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(registerintent);

    }
}