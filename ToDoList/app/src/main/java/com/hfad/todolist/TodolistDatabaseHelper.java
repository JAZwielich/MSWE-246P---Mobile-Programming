package com.hfad.todolist;

import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class TodolistDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "todolist"; // the name of our database
    private static final int DB_VERSION = 2; // the version of the databas
    private static SQLiteDatabase db;

    TodolistDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db, 0, DB_VERSION);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.db = db;
        if (oldVersion < 1) {
            db.execSQL("CREATE TABLE SCHOOL ("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "NAME TEXT, "
                    + "DESCRIPTION TEXT);");

            insertTask("SCHOOL", "Module Assignment 5", "Make SQL Database");
            insertTask("SCHOOL", "Read Chapter 5", "Read Chapter of SQL Database");
            insertTask("SCHOOL", "Meet with Alberto", "Go to engineering building to see alberto for advisement");

            db.execSQL("CREATE TABLE JOB ("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "NAME TEXT, "
                    + "DESCRIPTION TEXT);");

            insertTask("JOB", "Answer Emails", "Respond to email about my potato");
            insertTask("JOB", "Create Salesforce Case", "Create salesforce case after answer customer question");
            insertTask("JOB", "Attend 11:00 Meeting", "Attend meeting regarding the future of GALAXY");

            db.execSQL("CREATE TABLE INTERNSHIP ("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "NAME TEXT, "
                    + "DESCRIPTION TEXT);");

            insertTask("INTERNSHIP", "Submit Applications", "Submit 10 internship applications");
            insertTask("INTERNSHIP", "Do OA", "Do OA for Amazon");
            insertTask("INTERNSHIP", "Attend Interview", "Attend interview for SHURE");
        }
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE SCHOOL ADD COLUMN TODO NUMERIC;");
            db.execSQL("ALTER TABLE JOB ADD COLUMN TODO NUMERIC;");
            db.execSQL("ALTER TABLE INTERNSHIP ADD COLUMN TODO NUMERIC;");
        }
    }

    public static void insertTask(String table, String name,
                                    String description) {
        ContentValues taskValues = new ContentValues();
        taskValues.put("NAME", name);
        taskValues.put("DESCRIPTION", description);
        db.insert(table, null, taskValues);
    }
}
