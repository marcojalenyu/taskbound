package com.mobdeve.s17.taskbound;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * A dialog fragment that displays the collectible that the user has obtained.
 */
public class CollectibleAddFragment extends DialogFragment {

    // The collectible that the user has obtained
    private final Collectible collectible;
    // UI components
    TextView collectibleMessage;
    ImageView collectibleRollingView, collectibleImageView;
    TextView collectibleNameTextView;
    Button closeButton;

    /**
     * Constructor for the CollectibleAddFragment class.
     * @param collectible - the collectible that the user has obtained
     */
    public CollectibleAddFragment(Collectible collectible) {
        this.collectible = collectible;
    }

    /**
     * This method is called when the fragment is first created.
     * @param inflater - the LayoutInflater
     * @param container - the ViewGroup
     * @param savedInstanceState - the Bundle
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_collectible, container, false);
        setCancelable(false);
        initializeUI(view);
        setupRollAnimation();
        return view;
    }

    /**
     * Initializes the UI components of the dialog fragment.
     */
    private void initializeUI(View view) {
        this.collectibleMessage = view.findViewById(R.id.dialog_message);
        this.collectibleImageView = view.findViewById(R.id.dialog_collectible_image);
        this.collectibleNameTextView = view.findViewById(R.id.dialog_collectible_name);
        this.closeButton = view.findViewById(R.id.dialog_close_button);
        this.collectibleRollingView = view.findViewById(R.id.dialog_animated_roll);
        collectibleImageView.setImageResource(collectible.getCollectibleImage());
        collectibleNameTextView.setText(collectible.getCollectibleName());
    }

    /**
     * Sets up the animation for rolling the collectible.
     */
    private void setupRollAnimation() {
        AnimationDrawable animation = UIUtil.getAnimatedSprite(getContext(), R.drawable.animated_roll, 4, 200);
        // Wait for 5 seconds before ending the animation
        collectibleRollingView.setBackground(animation);
        collectibleRollingView.post(animation::start);
        collectibleRollingView.postDelayed(() -> {
            collectibleRollingView.post(animation::stop);
            collectibleRollingView.setVisibility(View.GONE);
            collectibleMessage.setVisibility(View.VISIBLE);
            collectibleImageView.setVisibility(View.VISIBLE);
            collectibleNameTextView.setVisibility(View.VISIBLE);
            closeButton.setVisibility(View.VISIBLE);
            collectibleImageView.setImageResource(collectible.getCollectibleImage());
            closeButton.setOnClickListener(v -> dismiss());
        }, 5000);

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
     * It stops the background music.
     */
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (getActivity() instanceof ShopActivity) {
            ((ShopActivity) getActivity()).onDialogDismissed();
        }
    }

    /**
     * This method is called when the dialog is paused.
     */
    @Override
    public void onPause() {
        super.onPause();
    }
}
