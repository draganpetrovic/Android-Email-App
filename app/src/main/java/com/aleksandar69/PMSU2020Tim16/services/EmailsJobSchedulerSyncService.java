package com.aleksandar69.PMSU2020Tim16.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.aleksandar69.PMSU2020Tim16.Data;
import com.aleksandar69.PMSU2020Tim16.activities.EmailsActivity;
import com.aleksandar69.PMSU2020Tim16.javamail.ImapFetchMail;
import com.aleksandar69.PMSU2020Tim16.javamail.ImapsFetchMailConcurrent;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class EmailsJobSchedulerSyncService extends JobService {

    private static final int NOTIF_CODE = 1234;
    public static final String CHANNEL_ID = "2225";
    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("OVDE:", " onStartJOB");

        if(Data.syncTime != null) {
            try {
                doBackgroundWork(params);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            jobFinished(params, false);

            scheduleRefresh();
        }


        return true;
    }

    public void scheduleRefresh() {
        Log.d("OVDE:", " Refreshed");

        JobScheduler mJobScheduler = (JobScheduler) getApplicationContext().getSystemService(JOB_SCHEDULER_SERVICE);

        ComponentName componentName = new ComponentName(this,
                EmailsJobSchedulerSyncService.class);

        //JobInfo.Builder mJobBuilder = new JobInfo.Builder(492, new ComponentName(getPackageName(), EmailsJobSchedulerSyncService.class.getName()));

        JobInfo jobInfoObj = new JobInfo.Builder(111, componentName).setMinimumLatency(Long.parseLong(Data.syncTime)).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).build();

        mJobScheduler.schedule(jobInfoObj);

        //mJobBuilder.setMinimumLatency(60 * 1000).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);

    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("cancel", "Job cancelled before completion");
        ///jobCancelled = true;
        return false;
    }

    public synchronized void doBackgroundWork(JobParameters params) throws InterruptedException {
        Log.d("here", "dobackground");
        Thread thread = new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                Log.d("here", "inRun");

                if (Data.totalEmailsServer > Data.totalEmailsDB) {
                    showNotification("Messages");
                }

                ImapFetchMail imapFetchMail = new ImapFetchMail(getApplicationContext(), Data.account);
                //imapFetchMail.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                imapFetchMail.execute();

/*                ImapsFetchMailConcurrent fetch = new ImapsFetchMailConcurrent(getApplicationContext(),Data.account);
                try {
                    fetch.Run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/

            }
        });
        thread.start();

    }

    private class DoInBackground extends AsyncTask<Void, Void, Void> {
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
                    ImapFetchMail imapFetchMail = new ImapFetchMail(getApplicationContext(),Data.account);
                    imapFetchMail.execute();
                    // stopSelf();
                    // handler.postDelayed(this, 30000);

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
