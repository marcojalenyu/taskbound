package com.mobdeve.s17.taskbound;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CollectiblesActivity extends AppCompatActivity {

    RecyclerView collectiblesView;

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
        collectiblesView.setLayoutManager(new GridLayoutManager(this, 3));
        MyCollectiblesData[] myCollectiblesData = new MyCollectiblesData[]{
                new MyCollectiblesData("1", "Coin", "R", R.drawable.coin),
                new MyCollectiblesData("2", "Lily", "SSR", R.drawable.lily),
                new MyCollectiblesData("3", "Bunny", "R", R.drawable.bunny),
                new MyCollectiblesData("4", "Chest", "SR", R.drawable.chest),
                new MyCollectiblesData("5", "Lily", "SSR", R.drawable.lily),
                new MyCollectiblesData("6", "Lily", "SSR", R.drawable.lily),
                new MyCollectiblesData("7", "Lily", "SSR", R.drawable.lily),
                new MyCollectiblesData("8", "Lily", "SSR", R.drawable.lily),
                new MyCollectiblesData("9", "Lily", "SSR", R.drawable.lily),
                new MyCollectiblesData("10", "Lily", "SSR", R.drawable.lily),
                new MyCollectiblesData("11", "Lily", "SSR", R.drawable.lily),
                new MyCollectiblesData("12", "Lily", "SSR", R.drawable.lily),
                new MyCollectiblesData("13", "Lily", "SSR", R.drawable.lily),
                new MyCollectiblesData("14", "Lily", "SSR", R.drawable.lily),
                new MyCollectiblesData("15", "Lily", "SSR", R.drawable.lily),
                new MyCollectiblesData("16", "Lily", "SSR", R.drawable.lily),
                new MyCollectiblesData("17", "Lily", "SSR", R.drawable.lily),
                new MyCollectiblesData("18", "Lily", "SSR", R.drawable.lily),
                new MyCollectiblesData("19", "Lily", "SSR", R.drawable.lily),
                new MyCollectiblesData("20", "Lily", "SSR", R.drawable.lily),
                new MyCollectiblesData("21", "Lily", "SSR", R.drawable.lily),
                new MyCollectiblesData("22", "Lily", "SSR", R.drawable.lily),
                new MyCollectiblesData("23", "Lily", "SSR", R.drawable.lily),
                new MyCollectiblesData("24", "Lily", "SSR", R.drawable.lily),
        };

        MyCollectiblesAdapter myCollectiblesAdapter = new MyCollectiblesAdapter(myCollectiblesData, this);
        collectiblesView.setAdapter(myCollectiblesAdapter);
    }
}