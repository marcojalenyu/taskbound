package com.mobdeve.s17.taskbound;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.util.Date;

/**
 * Utility class for UI-related functions
 */
public class UIUtil {

    /**
     * Hides the system bars (status bar and navigation bar) of the activity
     * @param view - the view to hide the system bars from
     */
    public static void hideSystemBars(View view) {
        WindowInsetsControllerCompat windowInsetsController = ViewCompat.getWindowInsetsController(view);
        if (windowInsetsController != null) {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
            windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }

    /**
     * Gets the current date for use in changing look of task when past deadline
     */
    public static Date getCurrentDate() {
        return new Date();
    }

    /**
     * Start cloud background animation.
     */
    public static void startCloudAnimations(View view) {
        ImageView frame1 = view.findViewById(R.id.imgTitleBgLayer2A);
        ImageView frame2 = view.findViewById(R.id.imgTitleBgLayer2B);

        frame1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                frame1.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                float width = frame1.getWidth();
                frame2.setTranslationX(width);

                ValueAnimator animator = ValueAnimator.ofFloat(0, width);
                animator.setDuration(30000);
                animator.setRepeatCount(ValueAnimator.INFINITE);
                animator.addUpdateListener(animation -> {
                    float value = (float) animation.getAnimatedValue();
                    frame1.setTranslationX(-value);
                    frame2.setTranslationX(width - value);
                });

                animator.start();
            }
        });
    }
}