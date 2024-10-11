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

import java.util.ArrayList;

public class CollectiblesActivity extends AppCompatActivity {

    RecyclerView collectiblesView;
    TextView collectiblesCount;
    TextView moneyCount;

    UserSession userSession;
    User user;
    int coins;
    ArrayList<MyCollectiblesData> collectiblesList;

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

        userSession = UserSession.getInstance();
        user = userSession.getCurrentUser();
        coins = user.getCoins();
        collectiblesList = user.getCollectiblesList();

        collectiblesView = findViewById(R.id.collectiblesView);
        collectiblesCount = findViewById(R.id.collectiblesCount);
        moneyCount = findViewById(R.id.textView2);

        collectiblesView.setLayoutManager(new GridLayoutManager(this, 3));
        moneyCount.setText(String.valueOf(this.user.getCoins()));

        if (collectiblesList != null && !collectiblesList.isEmpty()) {
            MyCollectiblesData[] myCollectiblesData = collectiblesList.toArray(new MyCollectiblesData[0]);

            MyCollectiblesAdapter myCollectiblesAdapter = new MyCollectiblesAdapter(myCollectiblesData, this, collectiblesCount);
            collectiblesView.setAdapter(myCollectiblesAdapter);
        } else {
            collectiblesCount.setText("Skill issue.");
        }
    }
}