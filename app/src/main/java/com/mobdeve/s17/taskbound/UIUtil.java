package com.mobdeve.s17.taskbound;

import android.view.View;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

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
}