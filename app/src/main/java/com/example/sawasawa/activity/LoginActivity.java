package com.example.sawasawa.activity;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sawasawa.MyConstants;
import com.example.sawasawa.R;


import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    // Declare the views
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    // Declare a shared preference to store the user ID
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Retrieve references to the views
        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);


        // Retrieve the user_prefs shared preference
        mSharedPreferences = getSharedPreferences("my_app", Context.MODE_PRIVATE);

        // Set an OnClickListener on the login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the username and password values from the views
                String username = usernameEditText.getText().toString().trim();;
                String password = passwordEditText.getText().toString();

                if (!username.isEmpty()) {
                    // Save the user ID in SharedPreferences
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString("user_id", username);
                    editor.apply();

                // Call the verifyCredentials method to check if the username and password are valid
                verifyCredentials(username, password);
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter a username", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void login(final String username, final String password) {
        // If the credentials are valid, call the login method with the username and password values



        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void verifyCredentials(final String username, final String password) {
        // Define the URL for the API that checks the user's credentials
        String url = MyConstants.API_BASE_URL + "verify_credentials.php";

        // Create a StringRequest to send a POST request to the API with the username and password as parameters
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // If the response is "true", the credentials are valid
                        if (response.trim().equals("true")) {

                            login(username, password);
                        } else {
                            // If the response is "false", the credentials are invalid
                            Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If there is an error, display an error message
                        Toast.makeText(LoginActivity.this, "An error occurred while checking the credentials", Toast.LENGTH_SHORT).show();
                    }
                }) {
            // Add the username and password values to the request parameters
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        // Add the request to the Volley request queue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
}