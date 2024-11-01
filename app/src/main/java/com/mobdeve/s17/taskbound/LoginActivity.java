package com.mobdeve.s17.taskbound;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin, buttonRegister;
    UserSession userSession;
    TaskBoundDBHelper userDBHelper;
    private FirebaseAuth mAuth;
    DatabaseReference databaseUsers;

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

        mAuth = FirebaseAuth.getInstance();
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.login_button);
        buttonRegister = findViewById(R.id.register_button);

        // Check if user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        long lastLogin = sharedPreferences.getLong("lastLogin", 0);

        // If user is logged in and last login is within 14 days, redirect to HomeActivity
        if (isLoggedIn && System.currentTimeMillis() - lastLogin < 1209600000) {
            String userID = sharedPreferences.getString("userID", "");
            String email = sharedPreferences.getString("email", "");
            String userName = sharedPreferences.getString("userName", "");
            String password = sharedPreferences.getString("password", "");
            int coins = sharedPreferences.getInt("coins", 0);
            userSession.addAndSetUser(userID, email, userName, password, coins, new ArrayList<>());
            Intent home = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(home);
            finish();
        } else {
            // If user is not logged in or last login is more than 14 days, clear SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }

    }

    /**
     * This method is called when the login button is clicked.
     * It will authenticate the user and redirect to the HomeActivity if successful.
     * If the user is not found, it will display a toast message.
     * @param v - the view
     */
    public void btnClickedLogin(View v){
        String email = String.valueOf(editTextEmail.getText()); //not doing editTextEmail.getText().toString() to prevent null pointer exceptions
        String password = String.valueOf(editTextPassword.getText());

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(LoginActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                   if (task.isSuccessful()) {
                       // Sign in success
                       FirebaseUser user = mAuth.getCurrentUser();
                       if (user != null) {
                           // Access other data from the user
                           databaseUsers.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(DataSnapshot snapshot) {
                                   // Log.d("LoginActivity", "onDataChange called");
                                   User snapshotUser = snapshot.getValue(User.class);
                                   if (snapshotUser != null) {
                                       // Cache user data local database
                                       userDBHelper.insertUser(snapshotUser);
                                       // Pass user data via session to intent
                                       userSession.addUser(snapshotUser.getUserID(), snapshotUser.getEmail(), snapshotUser.getUserName(), snapshotUser.getPassword(), snapshotUser.getCoins(), snapshotUser.getCollectiblesList());
                                       if (userSession.setCurrentUser(email, password)) {
                                           // Save user data to SharedPreferences
                                           SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                                             SharedPreferences.Editor editor = sharedPreferences.edit();
                                             editor.putBoolean("isLoggedIn", true);
                                             editor.putString("userID", snapshotUser.getUserID());
                                             editor.putString("password", snapshotUser.getPassword());
                                             editor.putString("email", snapshotUser.getEmail());
                                             editor.putString("userName", snapshotUser.getUserName());
                                             editor.putInt("coins", snapshotUser.getCoins());
                                             editor.putLong("lastLogin", System.currentTimeMillis());
                                             editor.apply();



                                           Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                                           Intent home = new Intent(LoginActivity.this, HomeActivity.class);
                                           startActivity(home);
                                       } else {
                                           Toast.makeText(LoginActivity.this, "Account not found.", Toast.LENGTH_SHORT).show();
                                       }
                                   } else {
                                       Toast.makeText(LoginActivity.this, "Account not found.", Toast.LENGTH_SHORT).show();
                                   }
                               }

                               @Override
                               public void onCancelled(DatabaseError error) {
                                   Toast.makeText(LoginActivity.this, "Account not found.", Toast.LENGTH_SHORT).show();
                               }
                           });
                       }
                     } else {
                          // If sign in fails, display a message to the user.
                          Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                   }
                });

    }


    public void btnClickedRegister(View v){
        Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(register);
    }
}