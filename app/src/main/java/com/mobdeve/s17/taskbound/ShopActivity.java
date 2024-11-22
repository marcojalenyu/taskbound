package com.mobdeve.s17.taskbound;

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

/**
 * A class that represents the activity for the shop.
 */
public class ShopActivity extends AppCompatActivity {

    // UI Components
    TextView moneyCount;
    // Data Components
    private ArrayList<Collectible> collectiblesList;
    private int[] collectibleIndices;
    private int cumWeight;
    private int[] rarityWeights;
    // Database Components
    private User user;
    private LocalDBManager localDB;

    /**
     * This method is called when the activity is first created.
     * It initializes the UI and data components of the activity.
     * It also sets up the collectible indices based on the rarity of the collectibles.
     * It also fetches the latest coins data from the database.
     * @param savedInstanceState - the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shop);
        UIUtil.hideSystemBars(getWindow().getDecorView());
        initializeUI();
        initializeData();
        updateCoins();
        setupCollectibleIndices();
    }

    /**
     * Initializes the UI components of the activity.
     */
    private void initializeUI() {
        this.moneyCount = findViewById(R.id.money_count);
        Button btnRoll = findViewById(R.id.btnRoll);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnRoll.setOnClickListener(this::btnRollClicked);
        btnBack.setOnClickListener(this::btnBackClicked);
    }

    /**
     * Initializes the data components of the activity.
     */
    private void initializeData() {
        UserSession currSession = UserSession.getInstance();
        this.localDB = new LocalDBManager(this);
        this.user = currSession.getCurrentUser();
        this.collectiblesList = this.user.getCollectiblesList();
        this.cumWeight = currSession.getCollectiblesManager().getCumWeight();
        this.rarityWeights = currSession.getCollectiblesManager().getNumOfEachRarity();
    }

    /**
     * Sets up the collectible indices based on the rarity of the collectibles.
     */
    private void setupCollectibleIndices() {
        this.collectibleIndices = new int[this.cumWeight];
        int index = 0;
        int num;
        // Assign indices based on rarity of collectibles
        for (int i = 0; i < this.collectiblesList.size(); i++) {
            switch (this.collectiblesList.get(i).getCollectiblesRarity()) {
                case R:
                    num = this.rarityWeights[0];
                    break;
                case SR:
                    num = this.rarityWeights[1];
                    break;
                case SSR:
                    num = this.rarityWeights[2];
                    break;
                case LILY:
                    num = this.rarityWeights[3];
                    break;
                default:
                    num = 0;
            }
            for (int j = 0; j < num; j++) {
                this.collectibleIndices[index] = i;
                index++;
            }
        }
    }

    /**
     * This method is called when the back button is clicked.
     * @param v - the view
     */
    public void btnBackClicked(View v) {
        finish();
    }

    /**
     * This method is called when the roll button is clicked.
     * It rolls for a collectible and adds it to the user's collection.
     * @param v - the view
     */
    public void btnRollClicked(View v) {
        User user = localDB.getUserWithIdAndPass(this.user.getUserID(), this.user.getPassword());
        // Check if user has enough coins
        if (!hasEnoughCoins(user.getCoins())) {
            Toast.makeText(v.getContext(), "Not enough coins.", Toast.LENGTH_SHORT).show();
        }
        else {
            // Check if there are collectibles available
            if (this.collectiblesList.isEmpty()) {
                Toast.makeText(v.getContext(), "No collectibles available.", Toast.LENGTH_SHORT).show();
            }
            else {
                rollCollectible(user);
                updateCoins();
            }
        }
    }

    /**
     * Checks if the user has enough coins to roll for a collectible.
     */
    public boolean hasEnoughCoins(int coins) {
        return coins >= 100;
    }

    /**
     * Roll a collectible from the shop.
     */
    public void rollCollectible(User user) {
        Random random = new Random();
        int randomNumber = random.nextInt(this.cumWeight);
        Collectible collectible = this.collectiblesList.get(this.collectibleIndices[randomNumber]);
        // Add collectible to user's collection
        if (collectible != null) {
            int collectibleID = collectible.getCollectibleID();
            localDB.addCollectibleToUser(user.getUserID(), collectibleID);
            localDB.deductUserCoins(user.getUserID(), 100);
            // Show dialog for the collectible
            CollectibleAddFragment dialog = new CollectibleAddFragment(collectible);
            dialog.show(getSupportFragmentManager(), "CollectibleDialog");
        } else {
            Toast.makeText(this, "Error rolling for collectible.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Refreshes the coins displayed in the activity when the activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        updateCoins();
    }

    /**
     * Fetches the latest coins data from the database.
     */
    public void updateCoins() {
        int coins = this.localDB.getUserCoins(this.user.getUserID());
        this.user.setCoins(coins);
        this.moneyCount.setText(String.valueOf(coins));
    }
}