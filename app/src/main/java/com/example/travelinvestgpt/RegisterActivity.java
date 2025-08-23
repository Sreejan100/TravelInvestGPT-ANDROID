package com.example.travelinvestgpt;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.Credential;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;
import java.util.concurrent.Executors;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText username, password, email;
    SharedPreferenceManager preferenceManager;
    private CredentialManager credentialManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_regsiter);
        credentialManager = CredentialManager.create(getApplicationContext());
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

    public void loginRedirect(View view) {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);
        finish();
    }


    public void googleRegister(View view){
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder().setServerClientId(getString(R.string.server_client_id)).setFilterByAuthorizedAccounts(false).build();
        GetCredentialRequest request = new GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build();

        credentialManager.getCredentialAsync(
                RegisterActivity.this,
                request,
                null,
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @OptIn(markerClass = UnstableApi.class)
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        Credential credential = result.getCredential();
                        if (credential.getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)){
                            GoogleIdTokenCredential googlecreds = GoogleIdTokenCredential.createFrom(credential.getData());
                            String idToken = googlecreds.getIdToken();
                            String username = googlecreds.getDisplayName();
                            String pictureUrl = googlecreds.getProfilePictureUri() != null ?
                                    googlecreds.getProfilePictureUri().toString() : "";

                            String email = googlecreds.getId();
                            Log.d("GoogleSignIn", "Name: " + username);
                            Log.d("GoogleSignIn", "Email/ID: " + email);
                            Log.d("GoogleSignIn", "Picture: " + pictureUrl);

                            preferenceManager.setSignInMethod("google");
                            preferenceManager.saveEmail(email);
                            preferenceManager.saveUsername(username);
                            preferenceManager.saveImage(pictureUrl);
                            preferenceManager.setLoggedIn(true);
                            sendToBackendRegister(idToken);
                            Intent goMainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                            goMainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(goMainIntent);

                        }

                    }

                    @OptIn(markerClass = UnstableApi.class)
                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        Log.e("GoogleSignIn", "Error: "+e.getMessage(),e);
                        runOnUiThread(() -> {
                            if (e instanceof androidx.credentials.exceptions.NoCredentialException) {
                                Toast.makeText(RegisterActivity.this, "No Google accounts found or selected. Please add an account or try again.", Toast.LENGTH_LONG).show();
                            } else if (e instanceof androidx.credentials.exceptions.GetCredentialCancellationException) {
                                Toast.makeText(RegisterActivity.this, "Sign-in cancelled.", Toast.LENGTH_SHORT).show();
                            }
                            // You can add more specific handling for other GetCredentialException subtypes if needed
                            // e.g., GetUserInteractionRequiredException, GetDisabledException etc.
                            else {
                                Toast.makeText(RegisterActivity.this, "Problem signing in. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
        );

    }

    public void sendToBackendRegister(String idToken){

        JsonObject body = new JsonObject();
        body.addProperty("idToken",idToken);

        ApiService apiService = RetrofitClient.getClient("http://192.168.1.2:5030/").create(ApiService.class);

        apiService.googleSignIn(body).enqueue(new Callback<JsonObject>() {
              @Override
              public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                  if(response.isSuccessful() && response.body() != null) {
                      Toast.makeText(RegisterActivity.this,"Logged In Successfully",Toast.LENGTH_SHORT).show();
                  }
                  else {
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
                      Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                  }
              }

              @Override
              public void onFailure(Call<JsonObject> call, Throwable t) {
                  Toast.makeText(RegisterActivity.this, "Error: "+t.getMessage(), Toast.LENGTH_SHORT).show();


              }
                                              }
        );
    }


    public void transMain(View view){

    ApiService apiService = RetrofitClient.getClient("http://192.168.1.2:5030/").create(ApiService.class);


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
                try {
                    String username = response.body().get("username").getAsString();
                    String email = response.body().get("email").getAsString();
                    preferenceManager.saveUsername(username);
                    preferenceManager.saveEmail(email);
                    preferenceManager.setLoggedIn(true);
                    preferenceManager.setSignInMethod("email");
                    Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    Intent mainintent = new Intent(RegisterActivity.this, MainActivity.class);
                    mainintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mainintent);
                } catch (Exception e) {

                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "Registration successful but some other error", Toast.LENGTH_SHORT).show();
                }
            }
            else {

                String errorMessage = "Registration Failed";
                if (response.errorBody() != null){
                    try {

                        String errorBodyString = response.errorBody().string();
                        JsonObject errorJson = new Gson().fromJson(errorBodyString, JsonObject.class);
                        if (errorJson.has("message")) {
                            errorMessage += ": " + errorJson.get("message").getAsString();
                        } else {
                            errorMessage += " (Code: " + response.code() + ")";
                        }
                    }catch(Exception e) {
                        errorMessage += " (Error parsing error response)";
                    }
                }else {
                    errorMessage += " (Code: " + response.code() + ")";
                }
                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        public void onFailure(Call<JsonObject> call, Throwable t) {

            Toast.makeText(RegisterActivity.this,"Error: "+ t.getMessage(), Toast.LENGTH_SHORT).show();
        }


    });

    }
}