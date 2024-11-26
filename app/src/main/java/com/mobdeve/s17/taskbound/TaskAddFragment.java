package com.mobdeve.s17.taskbound;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

/**
 * A class that represents the dialog fragment for adding a task.
 */
public class TaskAddFragment extends DialogFragment {

    // UI components
    private EditText taskName, taskContent, taskDeadline, taskCategory;
    private Spinner taskPriority;
    // Task generation components
    private TaskManager taskManager;
    // Data components
    private User currentUser;
    private DatabaseReference cloudTaskDB;
    private LocalDBManager localDB;
    Enum<Priority> priority;

    /**
     * Default constructor for AddTaskFragment
     */
    public TaskAddFragment() {

    }

    /**
     * This method is called when the fragment is first created.
     * @param inflater - the LayoutInflater
     * @param container - the ViewGroup
     * @param savedInstanceState - the Bundle
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_addtask, container, false);
        initializeUI(view);
        initializeData();
        return view;
    }

    /**
     * This method initializes the UI components of the dialog box.
     * @param view - the view of the dialog box
     */
    private void initializeUI(View view) {
        this.taskName = view.findViewById(R.id.etTaskName);
        this.taskContent = view.findViewById(R.id.etTaskDesc);
        this.taskDeadline = view.findViewById(R.id.etDeadline);
        this.taskCategory = view.findViewById(R.id.etTaskCategory);
        this.taskPriority = view.findViewById(R.id.spinPriority);

        Button btnAddTask = view.findViewById(R.id.btnAddTask);
        Button btnCancelAddTask = view.findViewById(R.id.btnCancelTask);
        taskDeadline.setOnClickListener(this::etClickedDeadline);
        btnAddTask.setOnClickListener(this::btnClickedAddTask);
        btnCancelAddTask.setOnClickListener(this::btnClickedCancel);
        setupPrioritySpinner();
    }

    /**
     * This method sets up the priority spinner.
     */
    private void setupPrioritySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.task_priorities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taskPriority.setAdapter(adapter);
    }

    /**
     * This method initializes the data components of the dialog box.
     */
    private void initializeData() {
        this.currentUser = UserSession.getInstance().getCurrentUser();
        this.cloudTaskDB = FirebaseDatabase.getInstance().getReference("tasks").child(currentUser.getUserID());
        this.localDB = new LocalDBManager(getContext());
        // Initialize the TaskManager
        try {
            this.taskManager = new TaskManager();
        } catch (ParseException e) {
            // e.printStackTrace();
        }
    }

    /**
     * This method is called when the deadline EditText is clicked.
     */
    public void etClickedDeadline(View view) {
        // Get the calendar instance
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        // Create a new DatePickerDialog that allows the user to select a date
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

    /**
     * This method is called when the cancel button is clicked.
     */
    public void btnClickedCancel(View view) {
        dismiss();
    }

    /**
     * This method is called when the add task button is clicked.
     */
    public void btnClickedAddTask(View view) {
        // Get the task name, content, and deadline
        String name = taskName.getText().toString().trim();
        String content = taskContent.getText().toString().trim();
        String deadline = taskDeadline.getText().toString();
        String category = taskCategory.getText().toString().trim();
        priority = Priority.valueOf(taskPriority.getSelectedItem().toString().toUpperCase());

        if (category.isEmpty()) {
            category = "";
        }

        if (fieldsNotComplete()) {
            Toast.makeText(getContext(), "Your task lacks a name/deadline.", Toast.LENGTH_SHORT).show();
        }
        else {
            try {
                // Create a new task with the given name, content, and deadline
                Task taskMonster = generateRandomTask();
                String taskID = UUID.randomUUID().toString();
                Task task = new Task(taskID, currentUser.getUserID(), name,
                                    content, deadline, taskMonster.getHealth(),
                                    taskMonster.getCoins(), taskMonster.getMonster(),
                                    priority, category);
                localDB.insertTask(task);
                cloudTaskDB.child(taskID).setValue(task);
                dismiss();
                ((HomeActivity) getActivity()).onResume();
            } catch (ParseException e) {
                //  e.printStackTrace();
            }
        }
    }

    /**
     * Check if any of the fields are empty
     */
    private boolean fieldsNotComplete() {
        return taskName.getText().toString().isEmpty()
                || taskDeadline.getText().toString().isEmpty();
    }

    /**
     * Randomize the task monster
     */
    private Task generateRandomTask() {
        Random random = new Random();
        ArrayList<Integer> availableIndices = new ArrayList<>();
        ArrayList<Task> taskList = taskManager.getTasks();

        for (int i = 0; i < taskList.size(); i++) {
            if (!taskList.get(i).getPriority().equals(this.priority.toString())) {
                continue;
            }
            availableIndices.add(i);
        }

        int randomIndex = random.nextInt(availableIndices.size());

        return taskManager.getTasks().get(availableIndices.get(randomIndex));
    }

    /**
     * This method is called when the dialog is first created.
     * It sets the width and height of the dialog box.
     */
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
}
