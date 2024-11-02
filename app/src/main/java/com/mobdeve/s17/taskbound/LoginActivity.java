package com.mobdeve.s17.taskbound;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    // UI components
    private TextInputEditText editTextEmail, editTextPassword;
    // Session components
    private SharedPreferences sessionCache;
    private UserSession currSession;
    // Database components
    private FirebaseAuth userAuth;
    private LocalDBManager localDB;
    private DatabaseReference cloudUserDB;

    /**
     * This method is called when the activity is first created.
     * @param savedInstanceState - the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        UIUtil.hideSystemBars(getWindow().getDecorView());
        initializeDataAndSession();
        initializeUI();

        if (sessionExpired()) {
            clearSessionCache();
        } else {
            // TODO: Sync user data between local and cloud databases (go to syncUserData())
            // Note: This will probably need calling the FirebaseReference
            autoLogin();
        }
    }

    /**
     * This method initializes the DB and session data.
     */
    private void initializeDataAndSession() {
        this.sessionCache = getSharedPreferences("UserSession", MODE_PRIVATE);
        this.currSession = UserSession.getInstance();
        this.localDB = new LocalDBManager(this);
        this.userAuth = FirebaseAuth.getInstance();
        this.cloudUserDB = FirebaseDatabase.getInstance().getReference("users");
    }

    /**
     * This method initializes the UI components of the activity.
     */
    private void initializeUI() {
        this.editTextEmail = findViewById(R.id.email);
        this.editTextPassword = findViewById(R.id.password);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);
        btnLogin.setOnClickListener(this::btnClickedLogin);
        btnRegister.setOnClickListener(this::btnClickedRegister);
    }

    /**
     * Checks if the user's last login is within 14 days.
     * @return true if the user's last login is within 14 days, false otherwise
     */
    private boolean sessionExpired() {
        return System.currentTimeMillis() - sessionCache.getLong("lastLogin", 0) >= 1209600000;
    }

    /**
     * Clears the session cache.
     */
    private void clearSessionCache() {
        SharedPreferences.Editor editor = sessionCache.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Automatically logs in the user if user session is still valid.
     */
    private void autoLogin() {
        // Fetch user data from SharedPreferences and set to currSession
        String userID = sessionCache.getString("userID", "");
        String password = sessionCache.getString("password", "");
        // Sync user data and set to currSession
        syncUserData();
        User user = localDB.getUserWithIdAndPass(userID, password);
        currSession.addUser(user);
        currSession.setCurrentUser(user);
        saveAndRedirect();
    }

    /**
     * Saves the user data to the session cache and redirects to the HomeActivity.
     */
    private void saveAndRedirect() {
        if (currSession.getCurrentUser() != null) {
            saveSessionCache();
            // Redirect to HomeActivity
            Intent home = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(home);
            finish();
        }
    }

    /**
     * This method is called when the register button is clicked.
     */
    public void btnClickedRegister(View v){
        Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(register);
    }

    /**
     * This method is called when the login button is clicked.
     * It will authenticate the user and redirect to the HomeActivity if successful.
     * If the user is not found, it will display a toast message.
     * @param v - the view
     */
    public void btnClickedLogin(View v){
        // Get the email and password input
        String emailInput = String.valueOf(editTextEmail.getText());
        String passwordInput = String.valueOf(editTextPassword.getText());
        // If input is valid, authenticate the user
        if (inputIsValid(emailInput, passwordInput)) {
            authenticateUser(emailInput, passwordInput);
        }
    }

    /**
     * Validates the email and password input.
     * @param emailInput - the email input
     * @param passwordInput - the password input
     * @return true if the input is valid, false otherwise
     */
    private boolean inputIsValid(String emailInput, String passwordInput) {

        if (TextUtils.isEmpty(emailInput)) {
            Toast.makeText(LoginActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(passwordInput)) {
            Toast.makeText(LoginActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * Authenticates the user through Firebase.
     */
    private void authenticateUser(String email, String password) {
        userAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        FirebaseUser user = userAuth.getCurrentUser();
                        if (user != null) {
                            fetchUserData(user.getUid());
                        }
                    } else {
                        // Sign in failed
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Fetch user data from Firebase (cloudUserDB) and store it in the local database (localDB).
     */
    private void fetchUserData(String userID) {
        // Fetch user data from Firebase
        cloudUserDB.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    // Sync user data and store in local database
                    syncUserData();
                    localDB.insertUser(user);
                    currSession.addUser(user);
                    currSession.setCurrentUser(user);
                    saveAndRedirect();
                } else {
                    Toast.makeText(LoginActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error fetching user data
                Toast.makeText(LoginActivity.this, "Error fetching user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Store user credentials in the session cache.
     */
    private void saveSessionCache() {
        SharedPreferences.Editor editor = sessionCache.edit();
        editor.putLong("lastLogin", System.currentTimeMillis());
        editor.putString("userID", currSession.getCurrentUser().getUserID());
        editor.putString("password", currSession.getCurrentUser().getPassword());
        editor.apply();
    }

    /**
     * Sync user data between local and cloud databases.
     */
    private void syncUserData() {
        // TODO: Implement syncing between local and cloud databases (timestamp-based)
    }
}