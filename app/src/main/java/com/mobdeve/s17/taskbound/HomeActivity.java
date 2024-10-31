package com.mobdeve.s17.taskbound;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HomeActivity extends AppCompatActivity {

    RecyclerView tasksView;
    FloatingActionButton collectiblesBtn;
    FloatingActionButton shopBtn;
    FloatingActionButton addTaskButton;
    ArrayList<MyCollectiblesData> collectiblesList;
    TextView tvUsername;
    TextView tvCoinAmount;
    SearchView svSearchBar;
    private UserSession userSession;
    private TaskBoundDBHelper taskDBHelper;

    private User currentUser;
    private SortType sortType;
    private Button btnSort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        WindowInsetsControllerCompat windowInsetsController = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsController != null) {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
            windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }

        this.userSession = UserSession.getInstance();
        this.currentUser = userSession.getCurrentUser();

        this.tasksView = findViewById(R.id.tasksView);
        this.tvUsername = findViewById(R.id.tvUsername);
        this.tvCoinAmount = findViewById(R.id.tvCoinAmount);
        this.collectiblesBtn = findViewById(R.id.collectiblesBtn);
        this.shopBtn = findViewById(R.id.shopBtn);
        this.addTaskButton = findViewById(R.id.addBtn);
        this.svSearchBar = findViewById(R.id.searchView);
        this.btnSort = findViewById(R.id.btnSort);

        this.sortType = SortType.DUE_DATE_DESCENDING;
        Intent login = getIntent();

        // Authenticate user
        if (currentUser != null) {
            this.collectiblesList = currentUser.getCollectiblesList();
            this.tvCoinAmount.setText(String.valueOf(currentUser.getCoins()));
            this.tvUsername.setText(currentUser.getUserName());
        } else {
            // Handles the case where currentUser is null and redirects to login
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        // Initialize TaskBoundDBHelper and get all tasks
        this.taskDBHelper = new TaskBoundDBHelper(this);

        tasksView.setLayoutManager(new LinearLayoutManager(this));

        this.addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AddTaskFragment addTaskFragment = new AddTaskFragment();
                    addTaskFragment.show(getSupportFragmentManager(), "AddTaskFragment");
                } catch (Exception e) {
                    Log.e("LoginReal", e + "");
                }
            }
        });

        this.collectiblesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CollectiblesActivity.class);
                startActivity(intent);
            }
        });

        this.shopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ShopActivity.class);
                startActivity(intent);
            }
        });

        svSearchBar.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                svSearchBar.setIconified(false);
            }
        });

        // Handle query text change
        svSearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String q) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String q) {
                filterTasks(q);
                return false;
            }
        });

        svSearchBar.setIconifiedByDefault(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TaskBoundDBHelper dbHelper = new TaskBoundDBHelper(this);
        User currentUser = dbHelper.getUser(UserSession.getInstance().getCurrentUser().getEmail(), UserSession.getInstance().getCurrentUser().getPassword());

        if (currentUser != null) {
            // Update the coin amount when the activity resumes
            this.tvCoinAmount.setText(String.valueOf(currentUser.getCoins()));
            this.tvUsername.setText(currentUser.getUserName());
            this.userSession.getCurrentUser().setCoins(currentUser.getCoins());
            this.userSession.getCurrentUser().setUserID(currentUser.getUserID());

            // Fetch the latest tasks and update the adapter
            List<Task> tasks = dbHelper.getAllTask(currentUser.getUserID());
            MyTasksAdapter myTasksAdapter = new MyTasksAdapter(tasks, this);
            this.tasksView.setAdapter(myTasksAdapter);
        } else {
            // Handle the case where currentUser is null
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            Toast.makeText(HomeActivity.this, "User data is missing. Please log in again.", Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();
        }

    }

    public void btnClickedLogout(View v) {
        // CLear the user session and SharedPreferences
        UserSession.getInstance().clearUserData();
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Redirect to login
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void btnClickedSort(View v) {
        String txtSort = "=";
        String currQuery = this.svSearchBar.getQuery().toString();
        switch (sortType) {
            case DUE_DATE_ASCENDING:
                this.sortType = SortType.DUE_DATE_DESCENDING;
                txtSort = "v";
                break;
            case DUE_DATE_DESCENDING:
                this.sortType = SortType.DEFAULT;
                txtSort = "=";
                break;
            case DEFAULT:
                this.sortType = SortType.DUE_DATE_ASCENDING;
                txtSort = "^";
                break;
        }

        btnSort.setText(txtSort);
        filterTasks(currQuery);
    }

    public void updateCoins(int coins) {
        int newCoins = this.currentUser.getCoins() + coins;
        this.tvCoinAmount.setText(String.valueOf(newCoins));
        this.currentUser.setCoins(newCoins);
    }

    private void filterTasks(String query) {
        List<Task> filteredTasks = taskDBHelper.getAllTask(this.currentUser.getUserID())
                .stream()
                .filter(task -> task.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

        switch (this.sortType) {
            case DUE_DATE_ASCENDING:
                filteredTasks.sort(Comparator.comparing(Task::getDeadline));
                break;
            case DUE_DATE_DESCENDING:
                filteredTasks.sort(Comparator.comparing(Task::getDeadline).reversed());
                break;
        }

        MyTasksAdapter myTasksAdapter = new MyTasksAdapter(filteredTasks, this);
        this.tasksView.setAdapter(myTasksAdapter);
    }
}