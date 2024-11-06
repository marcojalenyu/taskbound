package com.mobdeve.s17.taskbound;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
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
import android.widget.VideoView;

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
    ImageView collectibleImageView;
    TextView collectibleNameTextView;
    Button closeButton;
    // Media and video player for the background music and video
    private MediaPlayer mediaPlayer;
    private VideoView videoView;
    private static int playbackPosition = 0;

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
        setupVideoView();
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
        this.videoView = view.findViewById(R.id.dialog_video_view);
        collectibleImageView.setImageResource(collectible.getCollectibleImage());
        collectibleNameTextView.setText(collectible.getCollectibleName());
    }

    /**
     * Sets up the video view for the dialog fragment.
     * Includes the video of the collectible animation.
     * The video is played when the dialog is first created.
     * The video is hidden when the video is completed.
     * The collectible image, name, and close button are shown when the video is completed.
     */
    private void setupVideoView() {
        Uri videoUri = Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.raw.vid_roll_animation);
        videoView.setVideoURI(videoUri);
        videoView.setOnPreparedListener(mp -> videoView.start());
        videoView.setOnCompletionListener(mp -> {
            videoView.setVisibility(View.GONE);
            collectibleMessage.setVisibility(View.VISIBLE);
            collectibleImageView.setVisibility(View.VISIBLE);
            collectibleNameTextView.setVisibility(View.VISIBLE);
            closeButton.setVisibility(View.VISIBLE);
            collectibleImageView.setImageResource(collectible.getCollectibleImage());
            closeButton.setOnClickListener(v -> dismiss());
        });
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
        playMusic();
    }

    /**
     * This method is called when the dialog is dismissed.
     * It stops the background music.
     */
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        stopMusic();
    }

    /**
     * Plays the background music.
     */
    private void playMusic() {
        if (this.mediaPlayer == null) {
            this.mediaPlayer = MediaPlayer.create(getContext(), R.raw.usagi_flap);
            this.mediaPlayer.setLooping(true);
            this.mediaPlayer.seekTo(playbackPosition + 2);
            this.mediaPlayer.start();
        }
    }

    /**
     * Stops the background music.
     */
    private void stopMusic() {
        if (this.mediaPlayer != null) {
            playbackPosition = 0;
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
    }
}
