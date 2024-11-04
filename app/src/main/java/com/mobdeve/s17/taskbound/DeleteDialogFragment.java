package com.mobdeve.s17.taskbound;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
public class DeleteDialogFragment extends DialogFragment {

    private LocalDBManager db;
    private final Task task;
    TextView tvHeader;
    Button btnDeleteTask, btnCancel;

    public DeleteDialogFragment(Task task) {
        this.task = task;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_delete, container, false);

        this.tvHeader = view.findViewById(R.id.tvTaskName);
        this.btnDeleteTask = view.findViewById(R.id.btnDeleteTask);
        this.btnCancel = view.findViewById(R.id.btnCancel);

        if (this.task.getName().length() > 18)
            this.tvHeader.setText("Delete " + this.task.getName().substring(0, 15) + "...");
        else {
            this.tvHeader.setText("Delete " + this.task.getName());
        }

        this.btnDeleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db = new LocalDBManager(getContext());
                db.hardDeleteTask(task.getId());
                dismiss();
                ((HomeActivity) getActivity()).onResume();
            }
        });

        this.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
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
            window.setAttributes(params);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}