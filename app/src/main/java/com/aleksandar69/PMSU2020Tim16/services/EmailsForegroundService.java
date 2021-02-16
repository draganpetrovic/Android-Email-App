package com.aleksandar69.PMSU2020Tim16.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.aleksandar69.PMSU2020Tim16.Data;
import com.aleksandar69.PMSU2020Tim16.R;
import com.aleksandar69.PMSU2020Tim16.activities.EmailsActivity;
import com.aleksandar69.PMSU2020Tim16.javamail.ImapFetchMail;

public class EmailsForegroundService extends Service {

    private static final int NOTIF_CODE = 123;
    public static final String CHANNEL_ID2 = "222";

    public EmailsForegroundService() {
    }

    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");

        createNotification(input);

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("OVDE:", " onStartCommand");
                if (Data.totalEmailsServer > Data.totalEmailsDB) {
                    showNotification("Messages");
                }
                ImapFetchMail imapFetchMail = new ImapFetchMail(getApplicationContext(),Data.account);
                imapFetchMail.execute();
                //handler.postDelayed(this, 4000);
                stopForeground(true);
                stopSelf();

            }
        });
        return START_NOT_STICKY;
    }

    public void createNotification(String input){
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, EmailsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText(input)
                .setSmallIcon(R.drawable.baseline_send_white_24dp)
                .setContentIntent(pendingIntent)
                .build();



        startForeground(1, notification);
        //do heavy work on a background thread
        //stopSelf();

    }

    private void showNotification(String text) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Email Notification";
            String description = "Get New Emails";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID2, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this, channel.getId())
                            .setSmallIcon(android.R.drawable.sym_def_app_icon)
                            .setContentTitle("Messages received")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setVibrate(new long[]{0, 1000})
                            .setAutoCancel(true);

            Intent actionIntent = new Intent(this, EmailsActivity.class);
            PendingIntent actionPendingIntent = PendingIntent.getActivity(this, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(actionPendingIntent);

            //NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIF_CODE, builder.build());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}