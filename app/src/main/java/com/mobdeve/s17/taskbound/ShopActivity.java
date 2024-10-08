package com.mobdeve.s17.taskbound;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Random;

public class ShopActivity extends AppCompatActivity {
    TextView moneyCount;
    ImageButton buttonRoll;
    Button buttonBack;
    ArrayList<MyCollectiblesData> collectiblesList;
    private ImageView collectibleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shop);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.collectiblesList = CollectiblesManager.getInstance().getCollectibles();
        this.buttonRoll = findViewById(R.id.roll_button);
        this.buttonBack = findViewById(R.id.back_button);
        this.collectibleImageView = findViewById(R.id.gachapon);
    }

    public void btnClickedRoll(View v){
        Random random = new Random();
        int randomNumber = random.nextInt(11);

        MyCollectiblesData collectible = this.collectiblesList.get(randomNumber);

        if (collectible != null) {
            collectible.setObtained(true);
            this.collectibleImageView.setImageResource(collectible.getCollectibleImage());
        }
    }
}