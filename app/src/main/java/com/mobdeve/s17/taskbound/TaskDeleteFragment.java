package com.mobdeve.s17.taskbound;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * This fragment is used to display a dialog box that asks the user if they want to delete a task.
 */
public class TaskDeleteFragment extends DialogFragment {

    // UI components
    private final Task task;
    private TextView tvHeader;

    /**
     * Constructor for the TaskDeleteFragment class.
     * @param task - the task to be deleted
     */
    public TaskDeleteFragment(Task task) {
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
        View view = inflater.inflate(R.layout.dialog_delete, container, false);
        initializeUI(view);
        return view;
    }

    /**
     * This method initializes the UI components of the dialog box.
     * @param view - the view of the dialog box
     */
    private void initializeUI(View view) {
        this.tvHeader = view.findViewById(R.id.tvTaskName);
        setupHeader();
        Button btnDeleteTask = view.findViewById(R.id.btnDeleteTask);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        btnDeleteTask.setOnClickListener(this::btnClickedDelete);
        btnCancel.setOnClickListener(this::btnClickedCancel);
    }

    /**
     * This method sets the header of the dialog box.
     */
    private void setupHeader() {
        // If the task name is too long, truncate it
        if (this.task.getName().length() > 18)
            this.tvHeader.setText(String.format("Delete %s...", this.task.getName().substring(0, 15)));
        else {
            this.tvHeader.setText(String.format("Delete %s", this.task.getName()));
        }
    }

    /**
     * This method is called when the delete button is clicked.
     */
    public void btnClickedDelete(View view) {
        try {
            LocalDBManager localDB = new LocalDBManager(getContext());
            localDB.deleteTask(task.getId());
            dismiss();
            MediaPlayer.create(getContext(), R.raw.sfx_delete).start();
            ((HomeActivity) getActivity()).onResume();
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    /**
     * This method is called when the cancel button is clicked.
     */
    public void btnClickedCancel(View view) {
        dismiss();
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
            window.setAttributes(params);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}