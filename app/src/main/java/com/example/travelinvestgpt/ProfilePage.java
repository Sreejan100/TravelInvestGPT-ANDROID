package com.example.travelinvestgpt;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Map;
import java.util.HashMap;

import com.bumptech.glide.Glide;
import com.cloudinary.Cloudinary;
import com.cloudinary.android.MediaManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.io.IOException;
import java.net.URL;

import com.cloudinary.utils.ObjectUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilePage extends AppCompatActivity {

    TextView nameedit;
    TextView EmailEdit;
    ImageView profileimage;

    private static final int PICK_REQUEST=1;
    private Uri imageuri;

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
            Glide.with(this).load(imageurl).into(profileimage);
        }




    }

    public void mainActivityTrans(View view) {

        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
    }

    public void deleteaccount(View view) {

        String username = preferenceManager.getUsername();
        String email = preferenceManager.getEmail();

        ApiService apiService = RetrofitClient.getClient("http://192.168.1.9:5010/").create(ApiService.class);

        JsonObject body = new JsonObject();
        body.addProperty("name",username);
        body.addProperty("email",email);

        apiService.deleteUser(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
               if(response.isSuccessful() && response.body() != null) {
                   preferenceManager.logout();
                   Intent deleteIntent = new Intent(ProfilePage.this, RegisterActivity.class);
                   deleteIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                   startActivity(deleteIntent);
                   Toast.makeText(ProfilePage.this,"Account has been successfully deleted",Toast.LENGTH_SHORT).show();
                   finish();
               }else {
                   String errorMessage = "Deletion Attempt Failed";
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
                   Toast.makeText(ProfilePage.this, errorMessage, Toast.LENGTH_SHORT).show();
               }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(ProfilePage.this, "Error: "+t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void loginfunc(View view) {

        preferenceManager.logout();
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);
        finish();
    }

    public void profileimageChange(View view) {

        Intent profilePickIntent = new Intent();
        profilePickIntent.setType("image/*");
        profilePickIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(profilePickIntent,"Select Picture"), PICK_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){

        super.onActivityResult(requestCode, resultCode,data);
        if(requestCode == PICK_REQUEST && resultCode == RESULT_OK && data !=null && data.getData() !=null){
            imageuri = data.getData();
            uploadToCloudinary(imageuri);
        }
    }


    private void uploadToCloudinary(Uri uri) {
        new Thread(() -> {

            try {
                String filepath = getPathfromUri(uri);
                Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                        "cloud_name","dnis1d96v",
                                "api_key","API_KEY",
                                "api_secret","API_SERVICE"
                ));

                Map uploadResult = cloudinary.uploader().upload(new File(filepath), ObjectUtils.emptyMap());
                String imageurl = (String) uploadResult.get("url");

                saveImageInDB(imageurl);

                runOnUiThread(() -> {
                    ImageView imageView = findViewById(R.id.profileImage);
                    Glide.with(ProfilePage.this).load(imageurl).into(imageView);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }




        }).start();

    }

    private String getPathfromUri(Uri uri){
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, proj,null,null,null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    public void saveImageInDB(String imageurl){

        String username = preferenceManager.getUsername();
        String email = preferenceManager.getEmail();
        preferenceManager.saveImage(imageurl);

        ApiService apiService = RetrofitClient.getClient("http://192.168.1.9:5010/").create(ApiService.class);

        JsonObject body = new JsonObject();

        body.addProperty("name",username);
        body.addProperty("email",email);
        body.addProperty("imageurl",imageurl);

        apiService.profileImageChange(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful() && response.body() != null){
                    Toast.makeText(ProfilePage.this,"Image Saved Successfully",Toast.LENGTH_SHORT).show();
                }
                else {
                    String errorMessage = "Image Saving Attempt Failed";
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
                    Toast.makeText(ProfilePage.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
                }



            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(ProfilePage.this, "Error: "+t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });


    }
}