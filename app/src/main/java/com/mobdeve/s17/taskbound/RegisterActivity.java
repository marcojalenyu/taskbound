package com.mobdeve.s17.taskbound;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText newEmail, newUsername, newPassword;
    Button buttonBackToLogin, buttonCreateAccount;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.userSession = UserSession.getInstance();
        // Initialize the TextInputEditText fields
        newEmail = findViewById(R.id.newEmail);
        newUsername = findViewById(R.id.newUsername);
        newPassword = findViewById(R.id.newPassword);
    }

    public void btnClickedBackToLogin(View v){
        Intent backToLogin = new Intent(RegisterActivity.this, LoginActivity.class);
        backToLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(backToLogin);
        finish();
    }



    public void btnClickedCreateAccount(View v){
        String email, username, password;
        email = String.valueOf(newEmail.getText()); //not doing newEmail.getText().toString() to prevent null pointer exceptions
        username = String.valueOf(newUsername.getText());
        password = String.valueOf(newPassword.getText());

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(RegisterActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(RegisterActivity.this, "Enter username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        //Firebase stuff
        if (userSession.addUser(email, username, password)) {
            Toast.makeText(RegisterActivity.this, "Registered user " + username, Toast.LENGTH_SHORT).show();
            Intent backToLogin = new Intent(RegisterActivity.this, LoginActivity.class);
            backToLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(backToLogin);
            finish();
        } else {
            Toast.makeText(RegisterActivity.this, "User already exists.", Toast.LENGTH_SHORT).show();
        }
    }
}