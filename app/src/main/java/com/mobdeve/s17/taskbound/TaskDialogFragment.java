package com.mobdeve.s17.taskbound;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;

/**
 * This fragment is used to display a dialog box of the task, allowing the user to edit the task.
 */
public class TaskDialogFragment extends DialogFragment {
    private TaskBoundDBHelper db;
    private final Task task;
    EditText etTaskName, etTaskDesc, etDeadline;
    ImageView imgTaskIcon;
    TextView tvHealth, tvCoins;

    public TaskDialogFragment(Task task) {
        this.task = task;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_task, container, false);

        this.etTaskName = view.findViewById(R.id.etTaskName);
        this.etTaskDesc = view.findViewById(R.id.etTaskDesc);
        this.etDeadline = view.findViewById(R.id.etDeadline);
        this.imgTaskIcon = view.findViewById(R.id.imgTaskIcon);
        this.tvHealth = view.findViewById(R.id.tvHealth);
        this.tvCoins = view.findViewById(R.id.tvCoins);

        this.etTaskName.setText(this.task.getName());
        this.etTaskDesc.setText(this.task.getContent());

        // Format the date as String YYYY-MM-DD
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String deadline = sdf.format(this.task.getDeadline());
        this.etDeadline.setText(deadline);

        String monsterName = this.task.getMonster().toLowerCase();
        int imageID = getContext().getResources().getIdentifier("enemy_" + monsterName, "drawable", getContext().getPackageName());
        this.imgTaskIcon.setImageResource(imageID);
        this.tvHealth.setText(String.valueOf(this.task.getHealth()));
        this.tvCoins.setText(String.valueOf(this.task.getCoins()));

        etDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the date of the task
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String deadline = sdf.format(task.getDeadline());
                String[] date = deadline.split("-");
                int year = Integer.parseInt(date[0]);
                int month = Integer.parseInt(date[1]) - 1;
                int day = Integer.parseInt(date[2]);

                // Create a date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year1, month1, dayOfMonth) -> {
                    month1 += 1;
                    String date1 = year1 + "-" + month1 + "-" + dayOfMonth;
                    etDeadline.setText(date1);
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        return view;
    }
}
