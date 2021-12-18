package com.nature.stock.common.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import androidx.core.app.NotificationCompat;
import com.nature.stock.R;
import com.nature.stock.common.activity.MainActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotifyUtil {

    public static Context context;

    public static void doNotify(Context context, int id, String title, String content) {
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(title, content, NotificationManager.IMPORTANCE_DEFAULT);
        if (manager == null) throw new RuntimeException("there is no notification manager");
        manager.createNotificationChannel(channel);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel.getId());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentTitle(title).setContentText(content).setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setWhen(System.currentTimeMillis()).setContentIntent(pi).setDefaults(Notification.DEFAULT_SOUND);
        manager.notify(id, builder.build());
    }

    public static void doNotify(int id, String title, String content) {
        doNotify(context, id, title, content);
    }
}
