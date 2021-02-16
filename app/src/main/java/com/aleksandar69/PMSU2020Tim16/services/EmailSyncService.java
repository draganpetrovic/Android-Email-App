package com.aleksandar69.PMSU2020Tim16.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.aleksandar69.PMSU2020Tim16.Data;
import com.aleksandar69.PMSU2020Tim16.activities.EmailsActivity;
import com.aleksandar69.PMSU2020Tim16.javamail.ImapFetchMail;

public class EmailSyncService extends Service {

    private static final int NOTIF_CODE = 121345;
    public static final String CHANNEL_ID = "2212";

    public EmailSyncService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("OVDE:", "onCreate");
                if (Data.totalEmailsServer > Data.totalEmailsDB) {
                    showNotification("Messages");
                }
                ImapFetchMail imapFetchMail = new ImapFetchMail(getApplicationContext(),Data.account);
                imapFetchMail.execute();
               // handler.postDelayed(this, 4000);

            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new DoInBackground().execute();

        return START_STICKY;
    }

    private class DoInBackground extends AsyncTask<Void, Void, Void>{
        Handler handler;
        @Override
        protected void onPreExecute() {
            handler = new Handler();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("OVDE:", " onStartCommand");
                    if (Data.totalEmailsServer > Data.totalEmailsDB) {
                        showNotification("Messages");
                    }
                    ImapFetchMail imapFetchMail = new ImapFetchMail(getApplicationContext(), Data.account);
                    imapFetchMail.execute();
                   // stopSelf();
                    handler.postDelayed(this, 30000);

                }
            });
            return null;
        }
    }

    private void showNotification(String text) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Email Notification";
            String description = "Get New Emails";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
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
}