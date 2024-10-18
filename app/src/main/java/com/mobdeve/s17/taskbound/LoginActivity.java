package com.mobdeve.s17.taskbound;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

public class LoginActivity extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin, buttonRegister;
    UserSession userSession;
    TaskBoundDBHelper userDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        WindowInsetsControllerCompat windowInsetsController = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsController != null) {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
            windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
        this.userSession = UserSession.getInstance(); // Initialize UserSession
        this.userDBHelper = new TaskBoundDBHelper(this); // Initialize TaskBoundDBHelper
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.login_button);
        buttonRegister = findViewById(R.id.register_button);
    }

    //TODO: Create Intent for Home View once its made
    public void btnClickedLogin(View v){
        String email, password;
        email = String.valueOf(editTextEmail.getText()); //not doing editTextEmail.getText().toString() to prevent null pointer exceptions
        password = String.valueOf(editTextPassword.getText());

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(LoginActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        //Firebase stuff

        // Use UserDBHelper to authenticate the user
        User user = userDBHelper.getUser(email, password);

        if (user != null) {
            //pass user data via session to intent
            this.userSession.addUser(user.getEmail(), user.getUserName(), user.getPassword(), user.getCoins(), user.getCollectiblesList());
            if (this.userSession.setCurrentUser(email, password)) {
                Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                Intent home = new Intent(LoginActivity.this, HomeActivity.class);
                home.putExtra("password", password);
                startActivity(home);
            } else {
                Toast.makeText(LoginActivity.this, "Account not found.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(LoginActivity.this, "Account not found.", Toast.LENGTH_SHORT).show();
        }

    }


    public void btnClickedRegister(View v){
        Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(register);
    }
}