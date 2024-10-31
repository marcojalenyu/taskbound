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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText newEmail, newUsername, newPassword;
    private UserSession userSession;
    private TaskBoundDBHelper userDBHelper;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseUsers;

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

        // Initialize the database reference
        mAuth = FirebaseAuth.getInstance();
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        // Initialize the TextInputEditText fields
        newEmail = findViewById(R.id.newEmail);
        newUsername = findViewById(R.id.newUsername);
        newPassword = findViewById(R.id.newPassword);
    }

    public void btnClickedBackToLogin(View v){
        finish();
    }

    public void btnClickedCreateAccount(View v){
        String email = String.valueOf(newEmail.getText()); //not doing newEmail.getText().toString() to prevent null pointer exceptions
        String username = String.valueOf(newUsername.getText());
        String password = String.valueOf(newPassword.getText());

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

        // Register the user using Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // If sign in succeeds, display a message to the user.
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        System.out.println(1000);
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            int initialCoins = 1000; // Default value for coins
                            ArrayList<MyCollectiblesData> collectiblesList = new ArrayList<>(); // Initialize empty collectibles list for new user

                            User newUser = new User(userId, email, username, password, initialCoins, collectiblesList); // ID is auto-incremented, see UserDBHelper

                            System.out.println("New user: " + newUser);
                            System.out.println(databaseUsers);

                            // Store the user in the Firebase Realtime Database
                            databaseUsers.child(userId).setValue(newUser);

                            // Store the user in thesetValue local SQLite database
                            userDBHelper.insertUser(newUser);

                            // Pass user data via session to intent
                            Toast.makeText(RegisterActivity.this, "Registered user " + username, Toast.LENGTH_SHORT).show();
                            Intent backToLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                            backToLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(backToLogin);
                            finish();
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(RegisterActivity.this, "User already exists.", Toast.LENGTH_SHORT).show();
                    }
                });



//        int initialCoins = 1000; // Default value for coins
//        String hashedPassword = HashUtil.hashPassword(password);
//        ArrayList<MyCollectiblesData> collectiblesList = new ArrayList<>(); // Initialize empty collectibles list for new user
//
//        User newUser = new User(0, email, username, hashedPassword, initialCoins, collectiblesList); // ID is auto-incremented, see UserDBHelper
//
//        // Use UserDBHelper to add a new user
//        if (userDBHelper.insertUser(newUser)) {
//            //userSession.addUser(email, username, password);
//            Toast.makeText(RegisterActivity.this, "Registered user " + username, Toast.LENGTH_SHORT).show();
//            Intent backToLogin = new Intent(RegisterActivity.this, LoginActivity.class);
//            backToLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(backToLogin);
//            finish();
//        } else {
//            Toast.makeText(RegisterActivity.this, "User already exists.", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                        userSession.addUser(user.getUserID(), user.getEmail(), user.getUserName(), user.getPassword(), user.getCoins(), user.getCollectiblesList());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(RegisterActivity.this, "Error fetching users", Toast.LENGTH_SHORT).show();
            }
        });
    }
}