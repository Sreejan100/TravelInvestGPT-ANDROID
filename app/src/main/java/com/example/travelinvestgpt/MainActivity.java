package com.example.travelinvestgpt;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.view.WindowCompat;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private SharedPreferenceManager preferenceManager;
    ImageView profileImage;

    private RecyclerView recyclerView;
    private ChatAdaptar chatAdaptar;
    private EditText edittext;
    private  ImageView imageView;

    private List<Message> messageList;

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
        messageList = new ArrayList<>();
        chatAdaptar = new ChatAdaptar(messageList);
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

        recyclerView = findViewById(R.id.MessageRecycler);
        edittext = findViewById(R.id.messageInput);
        imageView = findViewById(R.id.sendButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdaptar);
        imageView.setOnClickListener(v -> {
            String userInput = edittext.getText().toString().trim();

            if (!userInput.isEmpty()) {

                messageList.add(new Message(userInput, true));
                chatAdaptar.notifyItemInserted(messageList.size()-1);
                recyclerView.scrollToPosition(messageList.size()-1);
                edittext.setText("");
                sendMessageToServer(userInput);
            }
        });

    }


    public void sendMessageToServer(String userInput) {
        JsonObject body = new JsonObject();
        String token = preferenceManager.getJwttoken();

        body.addProperty("text",userInput);
        ApiService apiService = RetrofitClient.getClient("http://192.168.1.2:5030/").create(ApiService.class);

        apiService.sendreceiveMessage("Bearer " + token,body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null){
                    JsonObject responseBody = response.body();
                    String ModelResponse = responseBody.get("message").getAsString();

                    runOnUiThread(() -> {
                        messageList.add(new Message(ModelResponse,false));
                        chatAdaptar.notifyItemInserted(messageList.size()-1);
                        recyclerView.scrollToPosition(messageList.size()-1);
                    });
                }
                else {
                    String errorMessage = "Error receiving Message";
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
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void profilePageTrans(View view) {

        Intent profileintent = new Intent(this, ProfilePage.class);
        profileintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(profileintent);
    }


}