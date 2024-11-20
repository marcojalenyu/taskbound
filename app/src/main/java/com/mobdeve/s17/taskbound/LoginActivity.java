package com.mobdeve.s17.taskbound;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A class that represents the login activity of the application.
 */
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
            autoLogin();
        }

        startAnimations();
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
     * Start background animations.
     */
    private void startAnimations() {
        ImageView frame1 = findViewById(R.id.imgTitleBgLayer2A);
        ImageView frame2 = findViewById(R.id.imgTitleBgLayer2B);

        frame1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                frame1.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                float width = frame1.getWidth();
                frame2.setTranslationX(width);

                ValueAnimator animator = ValueAnimator.ofFloat(0, width);
                animator.setDuration(30000);
                animator.setRepeatCount(ValueAnimator.INFINITE);
                animator.addUpdateListener(animation -> {
                    float value = (float) animation.getAnimatedValue();
                    frame1.setTranslationX(-value);
                    frame2.setTranslationX(width - value);
                });

                animator.start();
            }
        });
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
        User user = localDB.getUserWithIdAndPass(userID, password);
        fetchUserData(user.getUserID());
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
                // Only sync user data if user is found in cloud database
                if (user != null) {
                    syncUserData(user);
                    currSession.addUser(user);
                    currSession.setCurrentUser(user);
                    fetchTaskData(user.getUserID());
                    saveAndRedirect();
                } else {
                    localDB.hardDeleteUser(userID);
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
     * Fetch task data from Firebase (cloudTaskDB) and store it in the local database (localDB).
     */
    private void fetchTaskData(String userID) {
        // Fetch task data from Firebase
        DatabaseReference cloudTaskDB = FirebaseDatabase.getInstance().getReference("tasks");
        cloudTaskDB.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Task> cloudTasks = new ArrayList<>();
                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    if (task != null) {
                        cloudTasks.add(task);
                    }
                }
                syncTasks(cloudTasks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error fetching task data
                Toast.makeText(LoginActivity.this, "Error fetching task data.", Toast.LENGTH_SHORT).show();
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
    private void syncUserData(User cloudUser) {
        try {
            User localUser = localDB.getUserWithIdAndPass(cloudUser.getUserID(), cloudUser.getPassword());
            // Check if user data is found in local database
            if (localUser == null) {
                if (cloudUser.isDeleted()) {
                    cloudUserDB.child(cloudUser.getUserID()).removeValue();
                } else {
                    localDB.insertUser(cloudUser);
                }
            } else {
                // If local user data is found, update whichever is more recent
                if (cloudUser.getLastUpdated() > localUser.getLastUpdated()) {
                    localDB.updateUser(cloudUser);
                } else {
                    cloudUserDB.child(cloudUser.getUserID()).setValue(localUser);
                }
                // If user is deleted, remove from both databases
                if (cloudUser.isDeleted()) {
                    localDB.hardDeleteUser(cloudUser.getUserID());
                    cloudUserDB.child(cloudUser.getUserID()).removeValue();
                }
            }
            Toast.makeText(LoginActivity.this, "Syncing successful.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(LoginActivity.this, "Syncing failed.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sync task data between local and cloud databases.
     */
    private void syncTasks(List<Task> cloudTasks) {
        List<Task> localTasks = localDB.getAllTasks(currSession.getCurrentUser().getUserID());
        DatabaseReference cloudTaskDB = FirebaseDatabase.getInstance().getReference("tasks").child(currSession.getCurrentUser().getUserID());
        Map<String, Task> localTaskMap = localTasks.stream().
                collect(Collectors.toMap(Task::getId, task -> task));
        // Sync the tasks from cloud to local
        for (Task cloudTask : cloudTasks) {
            Task localTask = localTaskMap.get(cloudTask.getId());
            // Check if the task is found in the local database
            if (localTask == null) {
                if (cloudTask.isDeleted()) {
                    cloudTaskDB.child(cloudTask.getId()).removeValue();
                } else {
                    localDB.insertTask(cloudTask);
                }
            } else {
                // If the task is found, update whichever is more recent
                if (cloudTask.getLastUpdated() > localTask.getLastUpdated()) {
                    localDB.updateTask(cloudTask);
                } else {
                    cloudTaskDB.child(localTask.getId()).setValue(localTask);
                }
                // If the task is deleted in the cloud, delete it in the local database
                if (cloudTask.isDeleted()) {
                    localDB.hardDeleteTask(localTask.getId());
                    cloudTaskDB.child(localTask.getId()).removeValue();
                }
            }
        }
        // Sync the tasks from local to cloud
        for (Task localTask : localTasks) {
            // If the task is not found in the cloud database
            if (!cloudTasks.contains(localTask)) {
                // If the task is deleted in the local database, delete it in the cloud database
                if (localTask.isDeleted()) {
                    cloudTaskDB.child(localTask.getId()).removeValue();
                    localDB.hardDeleteTask(localTask.getId());
                } else {
                    cloudTaskDB.child(localTask.getId()).setValue(localTask);
                }
            }
        }
    }
}