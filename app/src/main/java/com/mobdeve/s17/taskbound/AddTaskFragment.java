package com.mobdeve.s17.taskbound;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Random;

public class AddTaskFragment extends DialogFragment {

    private TaskBoundDBHelper db;
    private TaskManager taskManager;
    private Button btnAddTask;
    private Button btnCancelAddTask;
    private EditText taskName, taskContent, taskDeadline;
    private User currentUser;
    private UserSession userSession;

    public AddTaskFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_addtask, container, false);

        db = new TaskBoundDBHelper(getContext());
        try {
            taskManager = new TaskManager();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        this.userSession = UserSession.getInstance();
        currentUser = userSession.getCurrentUser();

        taskName = view.findViewById(R.id.taskName);
        taskContent = view.findViewById(R.id.taskContent);
        taskDeadline = view.findViewById(R.id.taskDeadline);
        btnAddTask = view.findViewById(R.id.applyAddTaskButton);
        btnCancelAddTask = view.findViewById(R.id.cancelAddTaskButton);

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

                    try {
                        // Trim the name
                        name = name.trim();
                        if (name.isEmpty() || deadline.isEmpty()) {
                            Toast.makeText(getContext(), "Your task lacks a name/deadline.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Create a new task
                        Task task = new Task(0, userID, name, content, deadline, taskData.getHealth(), taskData.getCoins(), taskData.getMonster());
                        db.insertTask(task);

                        dismiss();
                        ((HomeActivity) getActivity()).onResume();

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle the case where currentUser is null
                    Toast.makeText(getContext(), "User data is missing. Please log in again.", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        });

        btnCancelAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        taskDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        (view1, year1, month1, dayOfMonth1) -> {
                            month1 += 1;
                            String date = year1 + "-" + month1 + "-" + dayOfMonth1;
                            taskDeadline.setText(date);
                        },
                        year, month, dayOfMonth
                );

                datePickerDialog.show();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.CENTER;
            window.setAttributes(params);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
