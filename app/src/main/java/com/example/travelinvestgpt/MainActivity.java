package com.example.travelinvestgpt;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.view.WindowCompat;
import androidx.media3.common.util.UnstableApi;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    private SharedPreferenceManager preferenceManager;
    ImageView profileImage;

    @UnstableApi
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);
        preferenceManager=new SharedPreferenceManager(getApplicationContext());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
        Window window = getWindow();
        window.setNavigationBarColor(Color.parseColor("#FFFFFF"));
        WindowInsetsControllerCompat insetsController = WindowCompat.getInsetsController(window, window.getDecorView());
        if (insetsController != null) {
            insetsController.setAppearanceLightStatusBars(false);
            insetsController.setAppearanceLightNavigationBars(true);// false for light icons on dark background
        }

        boolean isLoggedIn = preferenceManager.getLoggedIn();

        if (!isLoggedIn){
            Intent loginIntent = new Intent(this,LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
        }

        profileImage = findViewById(R.id.profileimage);


        String imageurl = preferenceManager.getImage();

        if (imageurl != null && !imageurl.isEmpty()) {
            Glide.with(this).load(imageurl).into(profileImage);
        }



    }

    public void profilePageTrans(View view) {

        Intent profileintent = new Intent(this, ProfilePage.class);
        profileintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(profileintent);
    }
}