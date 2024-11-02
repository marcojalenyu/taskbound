package com.mobdeve.s17.taskbound;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * This fragment is used to display a dialog box of the task, allowing the user to edit the task.
 */
public class TaskDialogFragment extends DialogFragment {
    private LocalDBManager db;
    private TaskManager taskManager;
    private final Task task;
    private Button btnSubmit;
    EditText etTaskName, etTaskDesc, etDeadline;
    ImageView imgTaskIcon;
    TextView tvHealth, tvCoins;

    private UserSession userSession;
    private User user;
    private String userID;

    private String taskID;
    private int taskHealth;
    private int taskCoins;
    private String taskMon;

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
        this.btnSubmit = view.findViewById(R.id.btnSubmit);

        this.userSession = UserSession.getInstance();
        this.user = userSession.getCurrentUser();
        this.userID = user.getUserID();
        this.db = new LocalDBManager(this.getContext());
        try {
            this.taskManager = new TaskManager();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        this.etTaskName.setText(this.task.getName());
        this.etTaskDesc.setText(this.task.getContent());

        // Format the date as String YYYY-MM-DD
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String deadline = sdf.format(this.task.getDeadline());
        this.etDeadline.setText(deadline);

        String monsterName = this.task.getMonster().toLowerCase();
        int imageID = getContext().getResources().getIdentifier("enemy_" + monsterName, "drawable", getContext().getPackageName());
        // this.imgTaskIcon.setImageResource(imageID);

        // Load the spritesheet
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageID);
        int frameWidth = bitmap.getWidth() / 2;
        int frameHeight = bitmap.getHeight();
        Bitmap frame1 = Bitmap.createBitmap(bitmap, 0, 0, frameWidth, frameHeight);
        Bitmap frame2 = Bitmap.createBitmap(bitmap, frameWidth, 0, frameWidth, frameHeight);

        AnimationDrawable animation = new AnimationDrawable();
        animation.addFrame(new BitmapDrawable(getResources(), frame1), 400);
        animation.addFrame(new BitmapDrawable(getResources(), frame2), 400);
        animation.setOneShot(false);

        this.imgTaskIcon.setBackground(animation);

        // Start the animation
        this.imgTaskIcon.post(new Runnable() {
            @Override
            public void run() {
                animation.start();
            }
        });

        this.taskID = this.task.getId();
        this.taskCoins = this.task.getCoins();
        this.taskHealth = this.task.getHealth();
        this.taskMon = this.task.getMonster();

        this.tvHealth.setText(String.valueOf(this.taskHealth));
        this.tvCoins.setText(String.valueOf(this.taskCoins));

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

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = String.valueOf(etTaskName.getText());
                String content = String.valueOf(etTaskDesc.getText());
                String deadline = String.valueOf(etDeadline.getText());

                try {
                    db.updateTask(taskID, name, content, deadline);
                    dismiss();
                    ((HomeActivity) getActivity()).onResume();
                } catch (Exception e) {
                    Log.e("LoginReal", e + "");
                }
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
