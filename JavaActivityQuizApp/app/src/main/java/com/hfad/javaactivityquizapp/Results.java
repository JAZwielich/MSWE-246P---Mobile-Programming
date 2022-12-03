package com.hfad.javaactivityquizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;

public class Results extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "correct";
    public int correct = 0;
    public int[] total = {-1,-1};
    private int totalSeconds = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {totalSeconds = savedInstanceState.getInt("seconds");}
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Intent intent = getIntent();
        correct += intent.getIntExtra(EXTRA_MESSAGE, 0);
        TextView messageView = (TextView)findViewById(R.id.text_results);
        String msg;
        total = readResultTotal();
        if (correct == 3){
            msg = "You passed! With " + correct + " out of 3 correct!";
            total[0]++;
        }
        else {
            msg = "You did not pass! With " + correct + " out of 3 correct!";
            total[1]++;
        }
        writeResultTotal(total);
        messageView.setText(msg);
        totalSeconds = intent.getIntExtra("seconds", 0);
        runTotalTimer();
    }

    public void getResults(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        String messageText = total[0] + " Passed: " +total[1] + " Failed";
        intent.putExtra(Intent.EXTRA_TEXT, messageText);
        intent.setType("text/plain");
        Intent chosenIntent = Intent.createChooser(intent, "Send message...");
        startActivity(chosenIntent);
    }

    public int[] readResultTotal(){
        int[] total ={0,0};
        try {
            File file = new File(getApplicationInfo().dataDir + "/total_results.txt");
            FileReader fileReader = new FileReader(file); // A stream that connects to the text file
            BufferedReader bufferedReader = new BufferedReader(fileReader); // Connect the FileReader to the BufferedReader
            String line = bufferedReader.readLine();
            String[] parts = line.split(" ");
            for (int i = 0; i < parts.length ; i++){
                total[i] = Integer.parseInt(parts[i]);
            }
            bufferedReader.close();
            return total;
        } catch (Exception e) {
            return total;
        }
    }

    public void writeResultTotal(int[] newResults){
        try {
            File file = new File(getApplicationInfo().dataDir + "/total_results.txt");
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write((Integer)newResults[0] + " " + (Integer)newResults[1]);
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void runTotalTimer() {
        final TextView timeView = (TextView)findViewById(R.id.time_view3);
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
                totalSeconds++;
                handler.postDelayed(this, 1000);
            }
        });
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("seconds", totalSeconds);
    }
}