package com.mobdeve.s17.taskbound;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.NotificationChannel;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import java.util.List;
import android.util.Log;

public class DeadlineCheckReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser == null) {
            //If user is not logged in or the session is invalid
            return;
        }

        LocalDBManager localDB = new LocalDBManager(context);
        List<Task> tasks = localDB.getAllExistingTasks(currentUser.getUserID());

        // Check if the deadline for each task has passed
        for (Task task : tasks) {
            if (task.getDeadline().before(UIUtil.getCurrentDate())) {
                sendNotification(context, task);
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

        Intent notificationIntent = new Intent(context, HomeActivity.class);
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


        notificationManager.notify(task.getId().hashCode(), builder.build());

        // Log the notification details
        Log.d("DeadlineCheckReceiver", "Notification sent for task: " + task.getName());
    }
}