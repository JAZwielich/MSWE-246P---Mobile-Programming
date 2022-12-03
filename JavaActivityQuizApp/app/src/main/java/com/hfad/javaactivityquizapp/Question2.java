package com.hfad.javaactivityquizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.Bundle;

public class Question2 extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "correct";
    public int correct = 0;
    private int attemptNum = 1;
    private int totalSeconds = 0;
    private int seconds = 0;
    public boolean isVisible = false;
    public boolean isImage = false;
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
        setContentView(R.layout.activity_question2);
        Intent intent = getIntent();
        correct += intent.getIntExtra(EXTRA_MESSAGE, 0);
        totalSeconds = intent.getIntExtra("seconds", 0);
        isVisible = intent.getBooleanExtra("isVisible", false);
        isImage = intent.getBooleanExtra("isImage", false);
        newTime = intent.getIntExtra("newTime", 0);
        Button button = (Button) findViewById(R.id.button2);
        ImageButton imageButton = (ImageButton)  findViewById(R.id.imageButton2);
        imageButton.setVisibility(isImage ? View.VISIBLE : View.INVISIBLE);
        button.setVisibility(isImage ? View.INVISIBLE : View.VISIBLE);
        runTotalTimer();
        runInstanceTimer();
    }

    public void submitQuestionTwo(View view) {
        Intent intent = new Intent(this, Results.class);
        EditText answer2 = (EditText) findViewById(R.id.editTextAnswerTwo);
        Button button = (Button) findViewById(R.id.button2);
        if ((String.valueOf(answer2.getText()).equalsIgnoreCase("horus"))){
            correct++;
            intent.putExtra("correct", correct);
            intent.putExtra("seconds", totalSeconds);
            startActivity(intent);
        }else if (attemptNum == 2){
            intent.putExtra("correct", correct);
            intent.putExtra("seconds", totalSeconds);
            startActivity(intent);
        } else{
            attemptNum++;
            String text = "try again";
            button.setText(text);
        }
    }

    private void runTotalTimer() {
        final TextView timeView = (TextView)findViewById(R.id.time_view2);
        final Handler handler = new Handler();
        timeView.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
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

    private void runInstanceTimer() {
        final TextView timeView = (TextView)findViewById(R.id.time_instance2);
        final Handler handler = new Handler();
        Intent intent = new Intent(this, MainActivity.class);
        timeView.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
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
                    submitQuestionTwo(findViewById(R.id.button2));
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