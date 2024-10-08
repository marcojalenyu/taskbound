package com.mobdeve.s17.taskbound;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Random;

public class ShopActivity extends AppCompatActivity {
    TextView moneyCount;
    ImageButton buttonRoll;
    Button buttonBack;
    ArrayList<MyCollectiblesData> collectiblesList;
    int[] collectibleIndices;
    private ImageView collectibleImageView;
    private int cumWeight;
    private int[] nums;

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

        this.cumWeight = CollectiblesManager.getInstance().getCumWeight();
        this.collectibleIndices = new int[cumWeight];

        this.nums = CollectiblesManager.getInstance().getNums();
        int num;
        int index = 0;

        for (int i = 0; i < collectiblesList.size(); i++) {
            switch (collectiblesList.get(i).getCollectiblesRarity()) {
                case R:
                    num = nums[0];
                    break;
                case SR:
                    num = nums[1];
                    break;
                case SSR:
                    num = nums[2];
                    break;
                default:
                    num = 0;
            }
            for (int j = 0; j < num; j++) {
                this.collectibleIndices[index] = i;
                index++;
            }
        }

        this.buttonRoll = findViewById(R.id.roll_button);
        this.buttonBack = findViewById(R.id.back_button);
        this.collectibleImageView = findViewById(R.id.gachapon);
    }

    public void btnClickedRoll(View v){
        Random random = new Random();
        int randomNumber = random.nextInt(this.cumWeight);

        MyCollectiblesData collectible = this.collectiblesList.get(this.collectibleIndices[randomNumber]);

        if (collectible != null) {
            collectible.setObtained(true);
            this.collectibleImageView.setImageResource(collectible.getCollectibleImage());
        } else {
            Toast.makeText(v.getContext(), "Error: Collectible not found.", Toast.LENGTH_SHORT).show();
        }
    }
}