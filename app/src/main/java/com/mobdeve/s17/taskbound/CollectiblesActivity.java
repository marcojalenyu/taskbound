package com.mobdeve.s17.taskbound;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * A class that represents the activity for the collectibles.
 */
public class CollectiblesActivity extends AppCompatActivity {

    // UI Components
    RecyclerView collectiblesView;
    TextView collectiblesCount;
    TextView moneyCount;
    // Data Components
    User user;
    ArrayList<Collectible> collectiblesList;
    private LocalDBManager localDB;

    /**
     * This method initializes the activity.
     * @param savedInstanceState - the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_collectibles);
        UIUtil.hideSystemBars(getWindow().getDecorView());
        initializeUI();
        initializeData();
        setComponents();
        fetchCollectiblesData();
    }

    /**
     * Refreshes the collectibles data when the activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        updateCoins();
        fetchCollectiblesData();
    }

    /**
     * Initializes the UI components of the activity.
     */
    private void initializeUI() {
        this.collectiblesView = findViewById(R.id.collectiblesView);
        this.collectiblesCount = findViewById(R.id.collectiblesCount);
        this.moneyCount = findViewById(R.id.moneyCount);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this::btnBackClicked);
    }

    /**
     * Initializes the data components of the activity.
     */
    private void initializeData() {
        this.localDB = new LocalDBManager(this);
        this.user = UserSession.getInstance().getCurrentUser();
        this.collectiblesList = user.getCollectiblesList();
    }

    /**
     * Sets the data of the activity.
     */
    private void setComponents() {
        this.collectiblesCount.setText(String.valueOf(this.collectiblesList.size()));
        this.moneyCount.setText(String.valueOf(this.user.getCoins()));
        collectiblesView.setLayoutManager(new GridLayoutManager(this, 3));
    }

    /**
     * Fetches the latest coins data from the database.
     */
    public void updateCoins() {
        int coins = this.localDB.getUserCoins(this.user.getUserID());
        this.user.setCoins(coins);
        this.moneyCount.setText(String.valueOf(coins));
    }

    /**
     * Fetches the latest collectibles data from the database.
     */
    private void fetchCollectiblesData() {
        try {
            // Fetch the latest collectibles data from the database
            collectiblesList = localDB.getUserCollectibles(user.getUserID());
            // If the collectibles list is not empty, set the adapter
            if (collectiblesList != null && !collectiblesList.isEmpty()) {
                Collectible[] myCollectibles = collectiblesList.toArray(new Collectible[0]);
                CollectibleAdapter collectibleAdapter = new CollectibleAdapter(myCollectibles, this, collectiblesCount);
                collectiblesView.setAdapter(collectibleAdapter);
            } else {
                collectiblesCount.setText("?");
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error fetching collectibles: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method finishes the activity if the back button is pressed.
     */
    public void btnBackClicked(View view) {
        finish();
    }
}