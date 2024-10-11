package com.mobdeve.s17.taskbound;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
    int[] collectibleIndices;
    private int cumWeight;
    private int[] nums;
    private int coins;

    private UserSession userSession;
    private User user;

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

        this.userSession = UserSession.getInstance();
        this.user = userSession.getCurrentUser();

        this.moneyCount = findViewById(R.id.money_count);
        this.collectiblesList = this.user.getCollectiblesList();
        this.coins = this.user.getCoins();

        this.moneyCount.setText(String.valueOf(coins));
        
        this.cumWeight = this.userSession.getCollectiblesManager().getCumWeight();
        this.collectibleIndices = new int[cumWeight];

        this.nums = this.userSession.getCollectiblesManager().getNums();
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
    }

    public void btnClickedRoll(View v){
        if (this.coins < 100) {
            Toast.makeText(v.getContext(), "Not enough coins.", Toast.LENGTH_SHORT).show();
            return;
        }
        Random random = new Random();
        int randomNumber = random.nextInt(this.cumWeight);

        MyCollectiblesData collectible = this.collectiblesList.get(this.collectibleIndices[randomNumber]);

        if (collectible != null) {
            int collectibleID = collectible.getCollectibleID();
            this.user.obtainCollectible(collectible.getCollectibleID(), true);
            this.coins -= 100;
            this.user.setCoins(this.coins);
            this.moneyCount.setText(String.valueOf(this.user.getCoins()));
            CollectibleDialogFragment dialog = new CollectibleDialogFragment(collectible);
            dialog.show(getSupportFragmentManager(), "CollectibleDialog");
        } else {
            Toast.makeText(v.getContext(), "Error: Collectible not found.", Toast.LENGTH_SHORT).show();
        }
    }

    public void btnClickedBack(View v) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("coins", this.coins);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}