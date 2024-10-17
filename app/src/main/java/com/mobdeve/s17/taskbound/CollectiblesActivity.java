package com.mobdeve.s17.taskbound;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CollectiblesActivity extends AppCompatActivity {

    RecyclerView collectiblesView;
    TextView collectiblesCount;
    TextView moneyCount;
    ImageButton btnBack;

    UserSession userSession;
    User user;
    int coins;
    ArrayList<MyCollectiblesData> collectiblesList;

    private TaskBoundDBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_collectibles);
        WindowInsetsControllerCompat windowInsetsController = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsController != null) {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
            windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }

        userSession = UserSession.getInstance();
        user = userSession.getCurrentUser();
        coins = user.getCoins();
        collectiblesList = user.getCollectiblesList();

        collectiblesView = findViewById(R.id.collectiblesView);
        collectiblesCount = findViewById(R.id.collectiblesCount);
        moneyCount = findViewById(R.id.textView2);
        btnBack = findViewById(R.id.btnBack);

        collectiblesView.setLayoutManager(new GridLayoutManager(this, 3));
        moneyCount.setText(String.valueOf(this.user.getCoins()));

        // Initialize TaskBoundDBHelper and fetch the latest collectibles data
        try {
            // Initialize TaskBoundDBHelper and fetch the latest collectibles data
            TaskBoundDBHelper dbHelper = new TaskBoundDBHelper(this);
            collectiblesList = dbHelper.getUserCollectibles(user.getEmail());

            if (collectiblesList != null && !collectiblesList.isEmpty()) {
                MyCollectiblesData[] myCollectiblesData = collectiblesList.toArray(new MyCollectiblesData[0]);

                MyCollectiblesAdapter myCollectiblesAdapter = new MyCollectiblesAdapter(myCollectiblesData, this, collectiblesCount);
                collectiblesView.setAdapter(myCollectiblesAdapter);
            } else {
                collectiblesCount.setText("Skill issue.");
            }
        } catch (Exception e) {
            // Handle the exception, e.g., show a toast or log the error
            Toast.makeText(this, "Error fetching collectibles: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        btnBack.setOnClickListener(v -> {
            finish();
        });
    }
}