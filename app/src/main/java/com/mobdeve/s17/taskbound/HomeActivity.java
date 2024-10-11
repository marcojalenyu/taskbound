package com.mobdeve.s17.taskbound;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
    ArrayList<MyCollectiblesData> collectiblesList;
    private int coins;

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

        this.collectiblesList = CollectiblesManager.getInstance().getCollectibles();

        this.collectiblesBtn = findViewById(R.id.collectiblesBtn);
        this.shopBtn = findViewById(R.id.shopBtn);

        this.coins = 1000;

        this.collectiblesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CollectiblesActivity.class);
                intent.putParcelableArrayListExtra("collectibles", collectiblesList);
                startActivity(intent);
            }
        });

        this.shopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ShopActivity.class);
                intent.putExtra("coins", coins);
                shopResult.launch(intent);
            }
        });

    }

    public void btnClickedLogout(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void setCoins(int coins) {
        this.coins = coins;
    }

    private final ActivityResultLauncher<Intent> shopResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>()
            {
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            setCoins(data.getIntExtra("coins", -1));
                        }
                    }
                }
            }
    );
}