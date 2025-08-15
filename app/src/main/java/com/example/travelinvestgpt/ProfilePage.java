package com.example.travelinvestgpt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.io.IOException;
import java.net.URL;

public class ProfilePage extends AppCompatActivity {

    TextView nameedit;
    TextView EmailEdit;
    ImageView profileimage;

    private SharedPreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_page);
        preferenceManager = new SharedPreferenceManager(getApplicationContext());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        Window  window = getWindow();
        window.setNavigationBarColor(Color.parseColor("#FFFFFF"));
        WindowInsetsControllerCompat insetsController = WindowCompat.getInsetsController(window, window.getDecorView());
        if (insetsController != null) {
            insetsController.setAppearanceLightStatusBars(false);
            insetsController.setAppearanceLightNavigationBars(true);// false for light icons on dark background
        }


        EmailEdit = findViewById(R.id.emailtextcontainer);
        nameedit = findViewById(R.id.nametextcontainer);
        profileimage = findViewById(R.id.profileImage);

        String name = preferenceManager.getUsername();
        String email = preferenceManager.getEmail();
        String imageurl = preferenceManager.getImage();


        nameedit.setText(name);
        EmailEdit.setText(email);
        if (imageurl != null && !imageurl.isEmpty()) {
            Bitmap bmp = null;
            try {
                bmp = BitmapFactory.decodeStream(new URL(imageurl).openConnection().getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            profileimage.setImageBitmap(bmp);
        }




    }

    public void mainActivityTrans(View view) {

        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
    }

    public void deleteaccount(View view) {

        Intent deleteIntent = new Intent(this, RegisterActivity.class);
        deleteIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(deleteIntent);
    }

    public void loginfunc(View view) {

        preferenceManager.logout();
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);
        finish();
    }
}