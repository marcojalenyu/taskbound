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

import java.text.ParseException;

public class AddTaskActivity extends AppCompatActivity {
    private TaskDBHelper db;
    private Button btnAddTask;
    private Button btnCancelAddTask;
    private EditText taskName, taskContent, taskDeadline;

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

        db = new TaskDBHelper(this);
        taskName = findViewById(R.id.taskName);
        taskContent = findViewById(R.id.taskContent);
        taskDeadline = findViewById(R.id.taskDeadline);
        btnAddTask = findViewById(R.id.applyAddTaskButton);
        btnCancelAddTask = findViewById(R.id.cancelAddTaskButton);

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = taskName.getText().toString();
                String content = taskContent.getText().toString();
                String deadline = taskDeadline.getText().toString();
                try {
                    Task task = new Task(0, name, content, deadline);
                    db.insertTask(task);
                    finish();
                    Toast.makeText(AddTaskActivity.this, "Task added", Toast.LENGTH_SHORT).show();
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(AddTaskActivity.this, "Invalid date format", Toast.LENGTH_SHORT).show();
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
