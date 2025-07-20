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

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText username, password, email;
    SharedPreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_regsiter);
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


    public void transMain(View view){

    ApiService apiService = RetrofitClient.getClient("http://127.0.0.1:5000/").create(ApiService.class);


    username = findViewById(R.id.NameText);
    email = findViewById(R.id.EmailText);
    password = findViewById(R.id.PasswordText);

    String user1 = username.getText().toString();
    String pass = password.getText().toString();
    String Email = email.getText().toString();

    JsonObject body = new JsonObject();
    body.addProperty("username",user1);
    body.addProperty("email",Email);
    body.addProperty("password",pass);

    preferenceManager = new SharedPreferenceManager(RegisterActivity.this);

    apiService.registerUser(body).enqueue( new Callback<JsonObject>() {

        @Override
        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
            if(response.isSuccessful()){
                String username = response.body().get("username").getAsString();
                String email = response.body().get("email").getAsString();
                preferenceManager.saveUsername(username);
                preferenceManager.saveEmail(email);
                Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(RegisterActivity.this, "Registration failed: Email already exists", Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        public void onFailure(Call<JsonObject> call, Throwable t) {

            Toast.makeText(RegisterActivity.this,"Error: "+ t.getMessage(), Toast.LENGTH_SHORT).show();
        }


    });
        Intent mainintent = new Intent(this, MainActivity.class);
        mainintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainintent);
    }
}