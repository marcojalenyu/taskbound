package com.mobdeve.s17.taskbound;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

import java.text.ParseException;
import java.util.Random;

public class AddTaskActivity extends AppCompatActivity {
    private TaskBoundDBHelper db;
    private TaskManager taskManager;
    private Button btnAddTask;
    private Button btnCancelAddTask;
    private EditText taskName, taskContent, taskDeadline;
    private User currentUser;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_addtask);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new TaskBoundDBHelper(this);
        try {
            taskManager = new TaskManager();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        this.userSession = UserSession.getInstance();
        currentUser = userSession.getCurrentUser();

        taskName = findViewById(R.id.taskName);
        taskContent = findViewById(R.id.taskContent);
        taskDeadline = findViewById(R.id.taskDeadline);
        btnAddTask = findViewById(R.id.applyAddTaskButton);
        btnCancelAddTask = findViewById(R.id.cancelAddTaskButton);

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser != null) {
                    String name = taskName.getText().toString();
                    String content = taskContent.getText().toString();
                    String deadline = taskDeadline.getText().toString();

                    // Generate a random index to select a random task from TaskManager
                    Random random = new Random();
                    int randomIndex = random.nextInt(taskManager.getTasks().size());
                    Task taskData = taskManager.getTasks().get(randomIndex);

                    // Get the current user's ID
                    int userID = UserSession.getInstance().getCurrentUser().getUserID();
                    //Toast userID

                    try {
                        Task task = new Task(0, userID, name, content, deadline, taskData.getHealth(), taskData.getCoins(), taskData.getMonster());
                        db.insertTask(task);
                        finish();
                        Toast.makeText(AddTaskActivity.this, "Task added", Toast.LENGTH_SHORT).show();
                        Toast toastUserID = Toast.makeText(AddTaskActivity.this, "User ID: " + userID, Toast.LENGTH_SHORT);
                        toastUserID.show();
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(AddTaskActivity.this, "Invalid date format", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle the case where currentUser is null
                    Toast.makeText(AddTaskActivity.this, "User data is missing. Please log in again.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        btnCancelAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



    }

}
