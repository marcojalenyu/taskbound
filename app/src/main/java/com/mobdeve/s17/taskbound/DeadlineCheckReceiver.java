package com.mobdeve.s17.taskbound;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DeadlineCheckReceiver extends BroadcastReceiver {

    // private static final String TAG = "DeadlineCheckReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Log.d(TAG, "onReceive: enqueuing work to DeadlineCheckService");
        DeadlineCheckService.enqueueWork(context, new Intent(context, DeadlineCheckService.class));
    }
}