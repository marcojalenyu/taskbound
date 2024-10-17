package com.mobdeve.s17.taskbound;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    RecyclerView tasksView;
    FloatingActionButton collectiblesBtn;
    FloatingActionButton shopBtn;
    FloatingActionButton addTaskButton;
    ArrayList<MyCollectiblesData> collectiblesList;
    TextView tvUsername;
    TextView tvCoinAmount;
    private UserSession userSession;
    private TaskBoundDBHelper taskDBHelper;

    private User currentUser;

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

        if (currentUser != null) {
            this.collectiblesList = currentUser.getCollectiblesList();
            this.tvCoinAmount.setText(String.valueOf(currentUser.getCoins()));
            this.tvUsername.setText(currentUser.getUserName());
            //toast current user data
            Toast toast = Toast.makeText(HomeActivity.this, currentUser.toString(), Toast.LENGTH_SHORT);
            toast.show();
        } else {
            // Handles the case where currentUser is null and redirects to login
            Toast toastSession = Toast.makeText(HomeActivity.this, "currentUser is null", Toast.LENGTH_SHORT);
            toastSession.show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }


        //this.tvUsername.setText(userSession.getCurrentUser().getUserName());
        //this is just set on start, but it will be updated onResume
        //this.tvCoinAmount.setText(String.valueOf(userSession.getCurrentUser().getCoins()));


        // Initialize TaskBoundDBHelper and get all tasks
        this.taskDBHelper = new TaskBoundDBHelper(this);
        //List<Task> tasks = taskDBHelper.getAllTask(this.userSession.getCurrentUser().getUserID());
        List<Task> tasks = taskDBHelper.getAllTask(this.currentUser.getUserID());

        // Temporary data for the tasks list
        /*
        Task[] tasks = null;
        try {
            tasks = new Task[]{
                    new Task(1, "Study OPESY", "Deal with operating systems, schedulers, etc.", "2024-08-01", 0, 0, "default"),
                    // new Task(2, "Study MOBDEVE", "", "2024-08-01", 0, 0, "default"),
                    // new Task(3, "CSARCH2", "talk to me", "2024-08-01", 0, 0, "default"),
                    // new Task(4, "CSALGCM", "", "2024-08-01", 0, 0, "default"),
            };
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        */


        MyTasksAdapter myTasksAdapter = new MyTasksAdapter(tasks, this);
        this.tasksView.setAdapter(myTasksAdapter);

        tasksView.setLayoutManager(new LinearLayoutManager(this));

        this.addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddTaskActivity.class);
                startActivity(intent);
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        TaskBoundDBHelper dbHelper = new TaskBoundDBHelper(this);
        User currentUser = dbHelper.getUser(UserSession.getInstance().getCurrentUser().getEmail(), null);

        Toast toastUserID = Toast.makeText(HomeActivity.this, currentUser.toString(), Toast.LENGTH_SHORT);
        toastUserID.show();

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
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}