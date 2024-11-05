package com.mobdeve.s17.taskbound;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class ShopActivity extends AppCompatActivity {
    TextView moneyCount;
    Button btnRoll;
    ImageButton buttonBack;
    ArrayList<Collectible> collectiblesList;
    int[] collectibleIndices;
    private int cumWeight;
    private int[] nums;
    private int coins;

    private UserSession userSession;
    private User user;
    private LocalDBManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shop);
        UIUtil.hideSystemBars(getWindow().getDecorView());

        this.db = new LocalDBManager(this);

        this.userSession = UserSession.getInstance();
        this.user = userSession.getCurrentUser();

        Intent home = getIntent();

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

        this.btnRoll = findViewById(R.id.btnRoll);
        this.buttonBack = findViewById(R.id.back_button);
    }

    public void btnClickedRoll(View v) {
        User user = db.getUserWithIdAndPass(this.user.getUserID(), this.user.getPassword());

        if (user.getCoins() < 100) {
            Toast.makeText(v.getContext(), "Not enough coins.", Toast.LENGTH_SHORT).show();
            return;
        }

        Random random = new Random();
        int randomNumber = random.nextInt(this.cumWeight);

        Collectible collectible = this.collectiblesList.get(this.collectibleIndices[randomNumber]);

        if (this.collectiblesList.isEmpty()) {
            Toast.makeText(v.getContext(), "No collectibles available.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (collectible != null) {
            int collectibleID = collectible.getCollectibleID();
            db.addCollectibleToUser(user.getUserID(), collectibleID);
            db.deductUserCoins(user.getUserID(), 100);

            user = db.getUserWithIdAndPass(this.user.getUserID(), this.user.getPassword());
            this.coins = user.getCoins();
            this.moneyCount.setText(String.valueOf(this.coins));

            CollectibleAddFragment dialog = new CollectibleAddFragment(collectible);
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