package com.example.donategood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private static final String EMAIL = "email";

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnRegister;
    private LoginButton fbLoginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();

        fbLoginButton = (LoginButton) findViewById(R.id.btnFBLogin);
        fbLoginButton.setReadPermissions(Arrays.asList(EMAIL));

        // Callback registration
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.i(TAG, "onSuccess FB login");
            }

            @Override
            public void onCancel() {
                // App code
                Log.i(TAG, "onCancel FB login");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.i(TAG, "onError FB login");
            }
        });

        //if user is already logged in, send to MainActivity
        if (ParseUser.getCurrentUser() != null) {
            goToMainActivity();
        }

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick login button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick register button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                registerUser(username, password);
            }
        });
    }

    private void loginUser(String username, String password) {
        Log.i(TAG, "attempting to login user " + username);

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    //TODO: better error handling to tell user what is wrong
                    Log.e(TAG, "Issue with login", e);
                    Toast.makeText(LoginActivity.this, "Issue with login!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //navigate to the main activity if the user has signed in properly
                Toast.makeText(LoginActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                goToMainActivity();
            }
        });
    }

    private void registerUser(String username, String password) {
        Log.i(TAG, "attempting to register user " + username);

        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with registration", e);
                    Toast.makeText(LoginActivity.this, "Issue with registering!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //navigate to the main activity if the user has signed in properly
                Toast.makeText(LoginActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                goToMainActivity();
            }
        });
    }

    private void goToMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}