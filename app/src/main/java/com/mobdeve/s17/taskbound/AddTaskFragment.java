package com.mobdeve.s17.taskbound;

import android.app.DatePickerDialog;
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
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

public class AddTaskFragment extends DialogFragment {

    private LocalDBManager db;
    private TaskManager taskManager;
    private Button btnAddTask;
    private Button btnCancelAddTask;
    private EditText taskName, taskContent, taskDeadline;
    private User currentUser;
    private UserSession userSession;
    private DatabaseReference cloudTaskDB;

    public AddTaskFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_addtask, container, false);

        db = new LocalDBManager(getContext());
        try {
            taskManager = new TaskManager();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        this.userSession = UserSession.getInstance();
        currentUser = userSession.getCurrentUser();
        cloudTaskDB = FirebaseDatabase.getInstance().getReference("tasks").child(currentUser.getUserID());

        taskName = view.findViewById(R.id.etTaskName);
        taskContent = view.findViewById(R.id.etTaskDesc);
        taskDeadline = view.findViewById(R.id.etDeadline);
        btnAddTask = view.findViewById(R.id.btnAddTask);
        btnCancelAddTask = view.findViewById(R.id.btnCancelTask);

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
                    String userID = UserSession.getInstance().getCurrentUser().getUserID();

                    try {
                        // Trim the name
                        name = name.trim();
                        if (name.isEmpty() || deadline.isEmpty()) {
                            Toast.makeText(getContext(), "Your task lacks a name/deadline.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Generate an id
                        String taskID = UUID.randomUUID().toString();

                        // Create a new task
                        Task task = new Task(taskID, userID, name, content, deadline, taskData.getHealth(), taskData.getCoins(), taskData.getMonster());
                        db.insertTask(task);
                        cloudTaskDB.child(taskID).setValue(task);

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
