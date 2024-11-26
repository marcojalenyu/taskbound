package com.mobdeve.s17.taskbound;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class CollectibleViewFragment extends DialogFragment {
    // UI components
    private Button submitButton;
    private ImageView imgCollectible;
    private TextView tvCollectibleRarity, tvCollectible;
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
        this.tvCollectible = view.findViewById(R.id.dialog_message);
        this.imgCollectible = view.findViewById(R.id.dialog_collectible_image);
        this.tvCollectibleRarity = view.findViewById(R.id.dialog_collectible_name);
        this.submitButton = view.findViewById(R.id.dialog_close_button);

        this.videoView.setVisibility(View.GONE);
        this.tvCollectible.setVisibility(View.VISIBLE);
        this.imgCollectible.setVisibility(View.VISIBLE);
        this.tvCollectibleRarity.setVisibility(View.VISIBLE);
        this.submitButton.setVisibility(View.VISIBLE);
    }

    /**
     * This method initializes the data components of the dialog box.
     */
    @SuppressLint("SetTextI18n")
    private void initializeData() {
        this.currentUser = UserSession.getInstance().getCurrentUser();
        this.localDB = new LocalDBManager(this.getContext());
        this.tvCollectible.setText(this.collectible.getCollectibleName());
        this.imgCollectible.setImageResource(this.collectible.getCollectibleImage());
        this.tvCollectibleRarity.setText("Rarity: " + this.collectible.getCollectiblesRarity().toString());
        this.submitButton.setText("Set Profile");
        this.submitButton.setOnClickListener(this::btnClickedSubmit);
    }

    /**
     * This method is called when the submit button is clicked.
     */
    public void btnClickedSubmit(View view) {
        // Get the task information
        int collectibleID = this.collectible.getCollectibleID();
        String userID = this.currentUser.getUserID();
        try {
            localDB.updateUserPicture(userID, collectibleID);
            currentUser.setPicture(collectibleID);
            dismiss();
            ((CollectiblesActivity) getActivity()).onResume();
        } catch (Exception e) {
            Log.e("SetProfilePicture", e + "");
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
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.CENTER;
            window.setAttributes(params);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    /**
     * This method is called when the dialog is dismissed.
     */
    @Override
    public void onDismiss(@Nullable DialogInterface dialog) {
        super.onDismiss(dialog);
        ((CollectiblesActivity) getActivity()).onResume();
    }
}
