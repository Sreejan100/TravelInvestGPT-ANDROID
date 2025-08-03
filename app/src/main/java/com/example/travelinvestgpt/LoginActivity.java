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

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText username, email, password;

    SharedPreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
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

        preferenceManager = new SharedPreferenceManager(LoginActivity.this);

        JsonObject body = new JsonObject();
        body.addProperty("username",usern);
        body.addProperty("email",Email);
        body.addProperty("password",pass);

        ApiService apiService = RetrofitClient.getClient("http://192.168.1.12:5000/").create(ApiService.class);

        apiService.loginUser(body).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response){
                if (response.isSuccessful()){

                    String user2 = response.body().get("username").getAsString();
                    String image = response.body().get("imageurl").getAsString();
                    String Email1 = response.body().get("email").getAsString();
                    preferenceManager.saveUsername(user2);
                    preferenceManager.saveEmail(Email1);
                    preferenceManager.saveImage(image);
                    preferenceManager.setLoggedIn(true);

                    Toast.makeText(LoginActivity.this, "Welcome Back", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t){
                Toast.makeText(LoginActivity.this, "Error: "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } );



        Intent loginintent = new Intent(this, MainActivity.class);
        loginintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginintent);

    }

    public void registerfunc(View view){

        Intent registerintent = new Intent(this, RegisterActivity.class);
        registerintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(registerintent);

    }
}