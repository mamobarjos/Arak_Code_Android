package com.arakadds.arak.common;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.arakadds.arak.R;
import com.arakadds.arak.presentation.activities.home.HomeActivity;
import com.arakadds.arak.presentation.activities.home.fragments.ArakStoreFragment;

import java.util.Date;

public class NotificationsHelper {

    public static void makeNotification(Context ctx, Class<?> resultActivity, boolean notificationStatus, String title, String body) {
        if (notificationStatus) {
            Intent homeIntent = new Intent(ctx, ArakStoreFragment.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
            // Adds the back stack
            stackBuilder.addParentStack(HomeActivity.class);
            // Adds the Intent to the top of the stack
            stackBuilder.addNextIntent(homeIntent);

            Intent resultIntent = new Intent(ctx, resultActivity);

            //resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            resultIntent.putExtra("title", title);
            resultIntent.putExtra("body", body);
            resultIntent.putExtra("gotoNotificationsList", true);

            stackBuilder.addNextIntent(resultIntent);

            // Gets a PendingIntent containing the entire back stack
            PendingIntent pendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);

            NotificationManager mNotificationManager =
                    (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("arak",
                        "Arak notifications",
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("Default notifications");
                if (mNotificationManager != null) {
                    mNotificationManager.createNotificationChannel(channel);
                }
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, "arak");

            builder
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.drawable.larg_arak_logo)
                    //.setLargeIcon(R.mipmap.ic_launcher);
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setTicker(ctx.getString(R.string.app_name))
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(body))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent);

            if (mNotificationManager != null) {
                mNotificationManager.notify(getUniqueId(), builder.build());
            }
        }

    }

    private static int getUniqueId() {
        return (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

    }

}
