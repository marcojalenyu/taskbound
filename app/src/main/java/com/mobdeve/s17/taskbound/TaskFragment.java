package com.mobdeve.s17.taskbound;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;

/**
 * This fragment is used to display a dialog box of the task, allowing the user to edit the task.
 */
public class TaskFragment extends DialogFragment {

    // UI components
    private Button btnSubmit;
    private EditText etTaskName, etTaskDesc, etDeadline, etTaskCategory;
    private Spinner spinPriority;
    private ImageView imgTaskIcon;
    private TextView tvHealth, tvCoins;
    // Data components
    private final Task task;
    private LocalDBManager localDB;
    Enum<Priority> priority;

    /**
     * Constructor for the TaskFragment class.
     * @param task - the task to be edited
     */
    public TaskFragment(Task task) {
        this.task = task;
    }

    /**
     * This method is called when the fragment is first created.
     * @param inflater - LayoutInflater object that can be used to inflate
     * @param container - the parent view that the fragment's UI should be attached to
     * @param savedInstanceState - the previous saved state
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_task, container, false);
        initializeUI(view);
        initializeData();
        loadTaskSprite();
        return view;
    }

    /**
     * This method initializes the UI components of the dialog box.
     */
    private void initializeUI(View view) {
        this.etTaskName = view.findViewById(R.id.etTaskName);
        this.etTaskDesc = view.findViewById(R.id.etTaskDesc);
        this.etDeadline = view.findViewById(R.id.etDeadline);
        this.etTaskCategory = view.findViewById(R.id.etTaskCategory);
        this.spinPriority = view.findViewById(R.id.spinPriority);
        this.imgTaskIcon = view.findViewById(R.id.imgTaskIcon);
        this.tvHealth = view.findViewById(R.id.tvHealth);
        this.tvCoins = view.findViewById(R.id.tvCoins);
        this.btnSubmit = view.findViewById(R.id.btnSubmit);
    }

    /**
     * This method initializes the data components of the dialog box.
     */
    private void initializeData() {
        this.localDB = new LocalDBManager(this.getContext());
        this.etTaskName.setText(this.task.getName());
        this.etTaskDesc.setText(this.task.getContent());
        this.etDeadline.setText(getFormattedDate());
        this.etTaskCategory.setText(this.task.getCategory());
        this.tvHealth.setText(String.valueOf(this.task.getHealth()));
        this.tvCoins.setText(String.valueOf(this.task.getCoins()));
        this.priority = Priority.valueOf(this.task.getPriority());

        switch (this.priority.toString()) {
            case "LOW":
                spinPriority.setSelection(0);
                break;
            case "MEDIUM":
                spinPriority.setSelection(1);
                break;
            case "HIGH":
            default:
                spinPriority.setSelection(2);
                break;
        }

        etDeadline.setOnClickListener(this::etClickedDeadline);
        btnSubmit.setOnClickListener(this::btnClickedSubmit);
    }

    /**
     * Formats the date as a string in the format YYYY-MM-DD.
     */
    private String getFormattedDate() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(this.task.getDeadline());
    }

    /**
     * Load the sprite sheet of the monster.
     */
    private void loadTaskSprite() {
        String monsterName = this.task.getMonster().toLowerCase();
        int imageID = getContext().getResources().getIdentifier("enemy_" + monsterName, "drawable", getContext().getPackageName());
        // Load the sprite sheet
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageID);
        int frameWidth = bitmap.getWidth() / 2;
        int frameHeight = bitmap.getHeight();
        Bitmap frame1 = Bitmap.createBitmap(bitmap, 0, 0, frameWidth, frameHeight);
        Bitmap frame2 = Bitmap.createBitmap(bitmap, frameWidth, 0, frameWidth, frameHeight);
        // Create an animation drawable from the sprite sheet
        AnimationDrawable animation = new AnimationDrawable();
        animation.addFrame(new BitmapDrawable(getResources(), frame1), 400);
        animation.addFrame(new BitmapDrawable(getResources(), frame2), 400);
        animation.setOneShot(false);
        // Set the animation as the background of the image view
        this.imgTaskIcon.setBackground(animation);
        // Start the animation
        this.imgTaskIcon.post(animation::start);
    }

    /**
     * This method is called when the deadline is clicked.
     */
    public void etClickedDeadline(View view) {
        // Get the date of the task
        String deadline = task.getDeadlineAsString();
        String[] date = deadline.split("-");
        // Get the year, month, and day of the task
        int year = Integer.parseInt(date[0]);
        int month = Integer.parseInt(date[1]) - 1;
        int day = Integer.parseInt(date[2]);
        // Create a date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view1, year1, month1, dayOfMonth) -> {
            month1 += 1;
            String date1 = year1 + "-" + month1 + "-" + dayOfMonth;
            etDeadline.setText(date1);
        }, year, month, day);
        datePickerDialog.show();
    }

    /**
     * This method is called when the submit button is clicked.
     */
    public void btnClickedSubmit(View view) {
        // Get the task information
        String name = String.valueOf(etTaskName.getText());
        String content = String.valueOf(etTaskDesc.getText());
        String deadline = String.valueOf(etDeadline.getText());
        String category = String.valueOf(etTaskCategory.getText());
        String priority = String.valueOf(spinPriority.getSelectedItem()).toUpperCase();
        try {
            localDB.updateTaskInfo(task.getId(), name, content, deadline, priority, category);
            dismiss();
            ((HomeActivity) getActivity()).onResume();
        } catch (Exception e) {
            Log.e("LoginReal", e + "");
        }
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
