package com.mobdeve.s17.taskbound;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import java.util.List;

public class DeadlineCheckService extends JobIntentService {

    // private static final String TAG = "DeadlineCheckService";
    private static final int JOB_ID = 1000;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, DeadlineCheckService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString("userID", null);
        if (userID == null) {
            return;
        }

        LocalDBManager localDB = new LocalDBManager(this);
        List<Task> tasks = localDB.getAllExistingTasks(userID);

        for (Task task : tasks) {
            if (task.getDeadline().before(UIUtil.getCurrentDate())) {
                sendNotification(this, task);
            }
        }
    }

    private void sendNotification(Context context, Task task) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "task_deadline_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Task Deadline Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Intent notificationIntent = new Intent(context, LoginActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(notificationIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Task Deadline Passed")
                .setContentText("The deadline for task \"" + task.getName() + "\" has passed.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        Log.d("DeadlineCheckService", "Sending notification for task: " + task.getName());
        notificationManager.notify(task.getId().hashCode(), builder.build());
    }
}