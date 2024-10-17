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

public class CollectibleDialogFragment extends DialogFragment {

    private final MyCollectiblesData collectible;
    private MediaPlayer mediaPlayer;
    private VideoView videoView;
    private static int playbackPosition = 0;

    public CollectibleDialogFragment(MyCollectiblesData collectible) {
        this.collectible = collectible;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_collectible, container, false);

        this.videoView = view.findViewById(R.id.dialog_video_view);
        TextView collectibleMessage = view.findViewById(R.id.dialog_message);
        ImageView collectibleImageView = view.findViewById(R.id.dialog_collectible_image);
        TextView collectibleNameTextView = view.findViewById(R.id.dialog_collectible_name);
        Button closeButton = view.findViewById(R.id.dialog_close_button);

        collectibleImageView.setImageResource(collectible.getCollectibleImage());
        collectibleNameTextView.setText(collectible.getCollectibleName());

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

        return view;
    }

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

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        stopMusic();
    }

    private void playMusic() {
        if (this.mediaPlayer == null) {
            this.mediaPlayer = MediaPlayer.create(getContext(), R.raw.usagi_flap);
            this.mediaPlayer.setLooping(true);
            this.mediaPlayer.seekTo(playbackPosition + 2);
            this.mediaPlayer.start();
        }
    }

    private void stopMusic() {
        if (this.mediaPlayer != null) {
            playbackPosition = mediaPlayer.getCurrentPosition();
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
    }
}
