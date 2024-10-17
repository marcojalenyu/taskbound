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
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText newEmail, newUsername, newPassword;
    Button buttonBackToLogin, buttonCreateAccount;
    private UserSession userSession;
    private TaskBoundDBHelper userDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        WindowInsetsControllerCompat windowInsetsController = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsController != null) {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
            windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }

        this.userSession = UserSession.getInstance();
        this.userDBHelper = new TaskBoundDBHelper(this);
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


        int coins = 1000; // Default value for coins
        ArrayList<MyCollectiblesData> collectiblesList = new ArrayList<>(); // Initialize empty collectibles list for new user

        User newUser = new User(0, email, username, password, coins, collectiblesList); // ID is auto-incremented, see UserDBHelper

        // Use UserDBHelper to add a new user
        if (userDBHelper.insertUser(newUser)) {
            //userSession.addUser(email, username, password);
            Toast.makeText(RegisterActivity.this, "Registered user " + username, Toast.LENGTH_SHORT).show();
            Intent backToLogin = new Intent(RegisterActivity.this, LoginActivity.class);
            backToLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(backToLogin);
            finish();
        } else {
            Toast.makeText(RegisterActivity.this, "User already exists.", Toast.LENGTH_SHORT).show();
        }

        /*
        if (userSession.addUser(email, username, password)) {
            Toast.makeText(RegisterActivity.this, "Registered user " + username, Toast.LENGTH_SHORT).show();
            Intent backToLogin = new Intent(RegisterActivity.this, LoginActivity.class);
            backToLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(backToLogin);
            finish();
        } else {
            Toast.makeText(RegisterActivity.this, "User already exists.", Toast.LENGTH_SHORT).show();
        }
        */

    }
}