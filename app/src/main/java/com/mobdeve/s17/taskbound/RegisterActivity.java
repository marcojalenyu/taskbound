package com.mobdeve.s17.taskbound;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
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

import org.mindrot.jbcrypt.BCrypt;

/**
 * A class that represents the registration activity of the application.
 */
public class RegisterActivity extends AppCompatActivity {

    // UI components
    TextView tvErrorRegister;
    TextInputEditText newEmail, newUsername, newPassword;
    // Session components
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
        setContentView(R.layout.activity_register);
        UIUtil.hideSystemBars(getWindow().getDecorView());
        initializeDataAndSession();
        initializeUI();
        UIUtil.startCloudAnimations(findViewById(R.id.mainRegister));
    }

    /**
     * This method initializes the DB and session data.
     */
    private void initializeDataAndSession() {
        this.currSession = UserSession.getInstance();
        this.localDB = new LocalDBManager(this);
        this.userAuth = FirebaseAuth.getInstance();
        this.cloudUserDB = FirebaseDatabase.getInstance().getReference("users");
    }

    /**
     * This method initializes the UI components.
     */
    private void initializeUI() {
        this.newEmail = findViewById(R.id.newEmail);
        this.newUsername = findViewById(R.id.newUsername);
        this.newPassword = findViewById(R.id.newPassword);
        this.tvErrorRegister = findViewById(R.id.tvErrorRegister);
        Button btnCreateAccount = findViewById(R.id.btnCreateAccount);
        ImageButton btnBackToLogin = findViewById(R.id.btnBackToLogin);
        btnCreateAccount.setOnClickListener(this::btnClickedCreateAccount);
        btnBackToLogin.setOnClickListener(this::btnClickedBackToLogin);
    }

    /**
     * This method is called when the user clicks the back to login button.
     */
    public void btnClickedBackToLogin(View v){
        finish();
    }

    /**
     * This method is called when the user clicks the create account button.
     * @param v - the view
     */
    public void btnClickedCreateAccount(View v){
        // Get the username, email, and password input
        String usernameInput = String.valueOf(newUsername.getText());
        String emailInput = String.valueOf(newEmail.getText());
        String passwordInput = String.valueOf(newPassword.getText());
        // If the input is valid, register the user
        if (inputIsValid(usernameInput, emailInput, passwordInput)) {
            registerUser(usernameInput, emailInput, passwordInput);
        }
    }

    /**
     * Validates the email, username, and password input.
     *  @param usernameInput - the username input
     *  @param emailInput - the email input
     *  @param passwordInput - the password input
     */
    @SuppressLint("SetTextI18n")
    private boolean inputIsValid(String usernameInput, String emailInput, String passwordInput) {

        if (TextUtils.isEmpty(usernameInput)) {
            tvErrorRegister.setText("Username is empty");
            tvErrorRegister.setVisibility(View.VISIBLE);
            return false;
        }

        if (TextUtils.isEmpty(emailInput)) {
            tvErrorRegister.setText("Email is empty");
            tvErrorRegister.setVisibility(View.VISIBLE);
            return false;
        }

        if (TextUtils.isEmpty(passwordInput)) {
            tvErrorRegister.setText("Password is empty");
            tvErrorRegister.setVisibility(View.VISIBLE);
            return false;
        }

        if (!emailInput.contains("@") || !emailInput.contains(".")) {
            tvErrorRegister.setText("Invalid email");
            tvErrorRegister.setVisibility(View.VISIBLE);
            return false;
        }

        if (passwordInput.length() < 6) {
            tvErrorRegister.setText("Password is too short");
            tvErrorRegister.setVisibility(View.VISIBLE);
            return false;
        }

        return true;
    }

    /**
     * Registers the user using Firebase Authentication.
     */
    @SuppressLint("SetTextI18n")
    private void registerUser(String username, String email, String password) {
        userAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Registration success
                        FirebaseUser registeredUser = userAuth.getCurrentUser();
                        if (registeredUser != null) {
                            registerUserData(registeredUser.getUid(), username, email, password);
                            Toast.makeText(RegisterActivity.this, "Registered " + username, Toast.LENGTH_SHORT).show();
                            tvErrorRegister.setVisibility(View.GONE);
                            returnToLogin();
                        }
                    } else {
                        // If registration failed
                        tvErrorRegister.setText("Registration failed");
                    }
                });
    }

    /**
     * Create a new user, store it in the Firebase Realtime Database, and store it in the local SQLite database.
     */
    private void registerUserData(String userID, String username, String email, String password) {
        // Hash the password for security purposes
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        // Create a new user, then store it in the Firebase Realtime Database and local SQLite database
        User newUser = new User(userID, email, username, hashedPassword);
        cloudUserDB.child(userID).setValue(newUser);
        localDB.insertUser(newUser);
    }

    /**
     * Redirects the user to the login activity.
     */
    private void returnToLogin() {
        Intent backToLogin = new Intent(RegisterActivity.this, LoginActivity.class);
        backToLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(backToLogin);
        finish();
    }

    /**
     * This method is called when the activity is started.
     * It fetches the users from the Firebase Realtime Database and stores them in the current session.
     */
    @Override
    protected void onStart() {
        super.onStart();
        cloudUserDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                        currSession.addUser(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RegisterActivity.this, "Error fetching users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * This method is called when the activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        tvErrorRegister.setVisibility(View.GONE);
    }
}