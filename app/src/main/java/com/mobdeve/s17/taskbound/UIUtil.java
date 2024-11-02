// UIUtils.java
package com.mobdeve.s17.taskbound;

import android.view.View;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class UIUtil {

    public static void hideSystemBars(View view) {
        WindowInsetsControllerCompat windowInsetsController = ViewCompat.getWindowInsetsController(view);
        if (windowInsetsController != null) {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
            windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }
}