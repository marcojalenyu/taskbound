package com.mobdeve.s17.taskbound;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    FloatingActionButton collectiblesBtn;
    FloatingActionButton shopBtn;
    FloatingActionButton addTaskButton;
    ArrayList<MyCollectiblesData> collectiblesList;
    TextView username;
    TextView coinAmount;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.userSession = UserSession.getInstance();
        this.collectiblesList = userSession.getCurrentUser().getCollectiblesList();

        this.username = findViewById(R.id.username);
        this.coinAmount = findViewById(R.id.coinAmount);

        this.username.setText(userSession.getCurrentUser().getUserName());
        //this is just set on start, but it will be updated onResume
        this.coinAmount.setText(String.valueOf(userSession.getCurrentUser().getCoins()));


        this.collectiblesBtn = findViewById(R.id.collectiblesBtn);
        this.shopBtn = findViewById(R.id.shopBtn);
        this.addTaskButton = findViewById(R.id.addBtn);

        this.addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddTaskActivity.class);
                startActivity(intent);
            }
        });

        this.collectiblesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CollectiblesActivity.class);
                startActivity(intent);
            }
        });

        this.shopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ShopActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update the collectibles list and coin amount when the activity resumes
        this.coinAmount.setText(String.valueOf(userSession.getCurrentUser().getCoins()));
    }

    public void btnClickedLogout(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}