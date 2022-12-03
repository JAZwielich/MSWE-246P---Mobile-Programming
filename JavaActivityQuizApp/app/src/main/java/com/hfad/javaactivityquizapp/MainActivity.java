package com.hfad.javaactivityquizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private int attemptNum = 1;
    private int totalSeconds = 0;
    private int seconds = 0;
    public boolean isVisible;
    public boolean isImage;
    public int newTime = 0;
    private TimerService totalTimer;
    private TimerService localTimer;
    private boolean bound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            totalSeconds = savedInstanceState.getInt("seconds");
            seconds = savedInstanceState.getInt("instanceSeconds");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        isVisible = intent.getBooleanExtra("isVisible", false);
        isImage = intent.getBooleanExtra("isImage", false);
        newTime = intent.getIntExtra("newTime", 0);
        Button button = (Button) findViewById(R.id.button);
        ImageButton imageButton = (ImageButton)  findViewById(R.id.imageButton);
        imageButton.setVisibility(isImage ? View.VISIBLE : View.INVISIBLE);
        button.setVisibility(isImage ? View.INVISIBLE : View.VISIBLE);
        try {
            runTotalTimer();
            runInstanceTimer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void submitQuestionOne(View view) {
        Intent pass = new Intent(this, Question2.class);
        Intent fail = new Intent(this, Results.class);
        Spinner answer1 = (Spinner) findViewById(R.id.spinner);
        Spinner answer3 = (Spinner) findViewById(R.id.spinner2);
        Button button = (Button) findViewById(R.id.button);
        if (String.valueOf(answer1.getSelectedItem()).equals("Azathoth") && String.valueOf(answer3.getSelectedItem()).equals("Cthulhu")){
            pass.putExtra("correct", 2);
            pass.putExtra("seconds", totalSeconds);
            pass.putExtra("newTime", newTime);
            pass.putExtra("isVisible", isVisible);
            pass.putExtra("isImage", isImage);
            startActivity(pass);
        } else if (attemptNum == 2){
            if (String.valueOf(answer1.getSelectedItem()).equals("Azathoth") || String.valueOf(answer3.getSelectedItem()).equals("Cthulhu")){
                fail.putExtra("correct", 1);
            } else{
                fail.putExtra("correct", 0);
            }
            fail.putExtra("seconds", totalSeconds);
            startActivity(fail);
        } else{
            attemptNum++;
            String text = "try again";
            button.setText(text);
        }
    }

    private void runTotalTimer() throws InterruptedException {
        final TextView timeView = (TextView)findViewById(R.id.time_view);
        timeView.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = totalSeconds/3600;
                int minutes = (totalSeconds%3600)/60;
                int secs = totalSeconds%60;
                String time = String.format("Total Quiz Time: %d:%02d:%02d",
                        hours, minutes, secs);
                timeView.setText(time);
                if (totalTimer != null) {
                    synchronized (this) {
                        totalSeconds += totalTimer.getTime();
                    }
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void runInstanceTimer() throws InterruptedException {
        final TextView timeView = (TextView)findViewById(R.id.time_instance);
        timeView.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
        Intent intent = new Intent(this, MainActivity.class);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds/3600;
                int minutes = (seconds%3600)/60;
                int secs = seconds%60;
                String time = String.format("Question 2 Time Elapsed: %d:%02d:%02d",
                        hours, minutes, secs);
                timeView.setText(time);
                if (localTimer != null) {
                    synchronized (this) {
                        seconds += localTimer.getTime();
                    }
                };
                handler.postDelayed(this, 1000);
                if (newTime + 1 == seconds && newTime != 0){
                    attemptNum = 2;
                    localTimer.createNotification();
                    submitQuestionOne(findViewById(R.id.button));
                }
            }
        });
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            TimerService.TimerBinder timerBinder =
                    (TimerService.TimerBinder) binder;
            totalTimer = timerBinder.getTimer();
            localTimer = timerBinder.getTimer();
            localTimer.setExpire(newTime);
            bound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };

    public void onDestroy() {
        localTimer.stopSelf();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, TimerService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            unbindService(connection);
            bound = false;
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("seconds", totalSeconds);
        savedInstanceState.putInt("instanceSeconds", seconds);
    }

}