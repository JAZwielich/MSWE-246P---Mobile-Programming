package com.hfad.todolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;

import android.app.Fragment;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.view.View;
import android.widget.CheckBox;
import android.content.ContentValues;

import androidx.annotation.Nullable;


public class TodoDetailFragment extends Fragment {

    private TodoListFragment.TodoListListener listener;
    private long todoID;
    private SQLiteDatabase db;
    private Cursor cursor;
    private final String[] lists = {"SCHOOL", "JOB", "INTERNSHIP"};
    private String howSort = null;


    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        ListView listItems = view.findViewById(R.id.textTitle);
        getDatabase(lists[(int) todoID], 0);
        CustomAdapter listAdapter = new CustomAdapter(getActivity(),
                R.layout.custom_button,
                cursor,
                new String[]{"NAME","_id"},
                new int[]{R.id.customText, R.id.item_id},
                lists[(int) todoID],
                db);
        listItems.setAdapter(listAdapter);
        Button btnSearch = (Button) getActivity().findViewById(R.id.search_button);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText entry = getActivity().findViewById(R.id.editTextSearch);
                String item = entry.getText().toString();
                Cursor c = cursor;
                int searchTable = -1;
                for (int i = 0; i < 3; i++){
                    String list = lists[i];
                    String msg = "select * from " + list + " where name=\""+ item + "\"";
                    System.out.println(msg);
                    c = db.rawQuery(msg,null);
                    if (c.moveToFirst()){
                        searchTable=i;
                        break;
                    }
                }
                AlertDialog.Builder results = new AlertDialog.Builder(getActivity());
                if (c.moveToFirst() || searchTable == -1){
                    try {
                        results.setTitle(item + " is ID " + c.getString(0) + " on table " + lists[searchTable]);
                    }catch (CursorIndexOutOfBoundsException e){
                        results.setTitle(item + " is not in any databases");
                    }
                } else{
                    results.setTitle(item + " is not in any databases");
                }
                results.show();
                c.close();
            }
        });

        CheckBox sortBox = (CheckBox) getActivity().findViewById(R.id.sort_checkbox);
        if (howSort == null) {sortBox.setChecked(false);}
        else {sortBox.setChecked(true);}
        sortBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked){
                    howSort = "NAME ASC";
                } else {
                    howSort = null;
                }
                getActivity().recreate();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            todoID = savedInstanceState.getLong("todoID");
            howSort = savedInstanceState.getString("howSort");
        }
        return inflater.inflate(R.layout.fragment_todo_detail, container, false);
    }

    public void setTodoID(long id) {
        this.todoID = id;
    }

    public void getDatabase(String tableName, int id){
        try {
            SQLiteOpenHelper todolistDatabaseHelper = new TodolistDatabaseHelper(getActivity());
            db = todolistDatabaseHelper.getWritableDatabase();
            cursor = db.query(tableName, new String[]{ "_id", "NAME","TODO"},
                    null, null, null, null, howSort);
            //Code to do something with the cursor
        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(getActivity(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        cursor.close();
        db.close();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong("todoID", todoID);
        savedInstanceState.putString("howSort", howSort);
    }
}