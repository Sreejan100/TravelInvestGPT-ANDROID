package com.example.travelinvestgpt;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class RegsiterActivity extends AppCompatActivity {

    EditText username, password, email;

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

    User user = new User(user1,Email,pass);











        Intent mainintent = new Intent(this, MainActivity.class);
        mainintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainintent);
    }
}