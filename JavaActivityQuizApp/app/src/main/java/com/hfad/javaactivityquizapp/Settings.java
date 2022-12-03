package com.hfad.javaactivityquizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import java.io.Console;

public class Settings extends AppCompatActivity {
    private static final String CHANNEL_ID = "Group_1";
    public boolean isVisible = false;
    public boolean isImage = false;
    public int newTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    public void onTimerSwitch(View view) {
        Switch s = (Switch) findViewById(R.id.switch_timer);
        isVisible = s.isChecked();
    }

    public void onButtonSwitch(View view) {
        Switch s = (Switch) findViewById(R.id.switch_button);
        isImage = ((Switch) view).isChecked();
    }

    public void submitSettings(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        EditText editTime = (EditText) findViewById(R.id.editTime);
        try {
            newTime = Integer.parseInt((String.valueOf(editTime.getText())));
        } catch (NumberFormatException e) {
            System.out.println(e);
        }
        intent.putExtra("newTime", newTime);
        intent.putExtra("isVisible", isVisible);
        intent.putExtra("isImage", isImage);
        startActivity(intent);
    }
}