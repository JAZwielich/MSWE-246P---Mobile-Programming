package com.hfad.javaactivityquizapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.location.LocationManager;
import android.app.PendingIntent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class TimerService extends Service {

    private final IBinder binder = new TimerBinder();
    private int seconds = 0;
    private int expire = 0;
    public static final int NOTIFICATION_ID = 5453;
    private static final String CHANNEL_ID = "Group_1";

    public class TimerBinder extends Binder {
        public TimerService getTimer() {
            return TimerService.this;
        }
    }

    public TimerService() throws InterruptedException {
    }

    public void onCreate(){
        seconds++;
    }

    public void setExpire(int expire){
        this.expire = expire;
    }

    public void onTaskRemoved(Intent rootIntent){
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    public int getTime(){
        return this.seconds;
    }


        public void createNotification(){
        Toast.makeText(getApplicationContext(), "Time limit for question has expired. Quiz Failed.", Toast.LENGTH_SHORT).show();
            // Create an explicit intent for an Activity in your app
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("Time Limit Expired")
                    .setContentText("Quiz Time Limit has expired. Quiz failed.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(NOTIFICATION_ID, builder.build());

        }


    @Override
    public IBinder onBind(Intent intent)   {
        return binder;
    }
}