package com.kakao_bank.adbservicetest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;

public class ForegroundService extends Service {
    private static final int NOTIFY_ID = 1;
    public static final String CHANNEL_ID = "channel_default";
    public static final String CHANNEL_DESCRIPTION = "channel_description";
    private NotificationManager mNotificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReciver();
        startForegroundService();
        return super.onStartCommand(intent, flags, startId);
    }

    void registerReciver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("broadcast.receiver.content_update");
        filter.addAction("broadcast.receiver.service_remove");
        registerReceiver(receiver, filter);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("broadcast.receiver.content_update")) {
                updateNotification();
            } else if (action.equals("broadcast.receiver.service_remove")) {
                cancelForegroundService();
            }
        }
    };

    void startForegroundService() {
        startForeground(NOTIFY_ID, generateNotification("create"));
    }

    void cancelForegroundService() {
        stopForeground(false);
        mNotificationManager.cancel(NOTIFY_ID);
    }

    private Notification generateNotification(String text) {
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_DESCRIPTION, NotificationManager.IMPORTANCE_HIGH);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(text)
                .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                .setPriority(NotificationManager.IMPORTANCE_MAX);

        return builder.build();
    }

    private void updateNotification() {
        String text = "Some text that will update the notification";
        Notification notification = generateNotification(text);
        mNotificationManager.notify(NOTIFY_ID, notification);
    }
}
