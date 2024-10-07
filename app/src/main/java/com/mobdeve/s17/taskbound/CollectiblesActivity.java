package com.mobdeve.s17.taskbound;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CollectiblesActivity extends AppCompatActivity {

    RecyclerView collectiblesView;
    TextView collectiblesCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_collectibles);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        collectiblesView = findViewById(R.id.collectiblesView);
        collectiblesCount = findViewById(R.id.collectiblesCount);

        collectiblesView.setLayoutManager(new GridLayoutManager(this, 3));

        MyCollectiblesData[] myCollectiblesData = initializeCollectibles();
        // Mark some collectibles as obtained
        myCollectiblesData[0].setObtained(true);
        myCollectiblesData[5].setObtained(true);
        myCollectiblesData[6].setObtained(true);
        myCollectiblesData[9].setObtained(true);

        MyCollectiblesAdapter myCollectiblesAdapter = new MyCollectiblesAdapter(myCollectiblesData, this, collectiblesCount);
        collectiblesView.setAdapter(myCollectiblesAdapter);
    }

    /**
     * Initializes collectibles (for testing purposes)
     * @return array of collectibles
     */
    public MyCollectiblesData[] initializeCollectibles() {
        // Initialize collectibles here
        return new MyCollectiblesData[]{
                new MyCollectiblesData(1, "Chicken", "R", R.drawable.collectible_chicken),
                new MyCollectiblesData(2, "Capybara", "R", R.drawable.collectible_capybara),
                new MyCollectiblesData(3, "Cat", "R", R.drawable.collectible_cat),
                new MyCollectiblesData(4, "Coin", "R", R.drawable.collectible_coin),
                new MyCollectiblesData(5, "Clover", "R", R.drawable.collectible_clover),

                new MyCollectiblesData(101, "Katana", "SR", R.drawable.collectible_katana),
                new MyCollectiblesData(102, "Diamond", "SR", R.drawable.collectible_diamond),
                new MyCollectiblesData(103, "Key", "SR", R.drawable.collectible_key),
                new MyCollectiblesData(4, "Flower", "SR", R.drawable.collectible_flower),

                new MyCollectiblesData(201, "Lily", "SSR", R.drawable.collectible_lily),
        };
    }
}