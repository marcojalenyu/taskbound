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
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;

public class CollectibleViewFragment extends DialogFragment {
    // UI components
    private Button submitButton;
    private ImageView collectibleImageView;
    private TextView collectibleNameTextView, collectibleMessage;;
    private VideoView videoView;
    // Data components
    private final Collectible collectible;
    private User currentUser;
    private LocalDBManager localDB;

    /**
     * Constructor for the TaskFragment class.
     * @param collectible - the collectible to be viewed
     */
    public CollectibleViewFragment(Collectible collectible) {
        this.collectible = collectible;
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
        View view = inflater.inflate(R.layout.dialog_collectible, container, false);
        initializeUI(view);
        initializeData();
        return view;
    }

    /**
     * This method initializes the UI components of the dialog box.
     */
    private void initializeUI(View view) {
        this.videoView = view.findViewById(R.id.dialog_video_view);
        this.collectibleMessage = view.findViewById(R.id.dialog_message);
        this.collectibleImageView = view.findViewById(R.id.dialog_collectible_image);
        this.collectibleNameTextView = view.findViewById(R.id.dialog_collectible_name);
        this.submitButton = view.findViewById(R.id.dialog_close_button);

        this.videoView.setVisibility(View.GONE);
        this.collectibleMessage.setVisibility(View.VISIBLE);
        this.collectibleImageView.setVisibility(View.VISIBLE);
        this.collectibleNameTextView.setVisibility(View.VISIBLE);
        this.submitButton.setVisibility(View.VISIBLE);
    }

    /**
     * This method initializes the data components of the dialog box.
     */
    private void initializeData() {
        this.currentUser = UserSession.getInstance().getCurrentUser();
        this.localDB = new LocalDBManager(this.getContext());
        this.collectibleNameTextView.setText(this.collectible.getCollectibleName());
        this.collectibleImageView.setImageResource(this.collectible.getCollectibleImage());

        String text = " ";
        this.collectibleMessage.setText(text);

        text = "Set pfp";
        this.submitButton.setText(text);

        this.submitButton.setOnClickListener(this::btnClickedSubmit);
    }

    /**
     * This method is called when the submit button is clicked.
     */
    public void btnClickedSubmit(View view) {
        // Get the task information
//        String name = String.valueOf(etTaskName.getText());
//        String content = String.valueOf(etTaskDesc.getText());
//        String deadline = String.valueOf(etDeadline.getText());
//        try {
//            localDB.updateTaskInfo(task.getId(), name, content, deadline);
//            dismiss();
//            ((HomeActivity) getActivity()).onResume();
//        } catch (Exception e) {
//            Log.e("LoginReal", e + "");
//        }
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
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.CENTER;
            window.setAttributes(params);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}
