package com.hfad.todolist;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;


public class CustomAdapter extends SimpleCursorAdapter {
    private Context mContext;
    private Context appContext;
    private int layout;
    private Cursor cr;
    private final LayoutInflater inflater;
    String[] from;
    int[] to;
    private String mName ="";
    String table;
    SQLiteDatabase db;

    public CustomAdapter(Context context, int layout, Cursor c, String[] from, int[] to, String table, SQLiteDatabase db) {
        super(context, layout, c, from, to);
        this.layout = layout;
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.cr = c;
        this.from = from;
        this.to = to;
        this.table = table;
        this.db =db;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(layout, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);
        TextView textView = view.findViewById(to[1]);
        cursor.moveToFirst();
        while (!(cursor.getString(0 ).equals(textView.getText().toString()))){
            cursor.moveToNext();
        }
        Spinner todoSpinner = (Spinner) view.findViewById(R.id.spinnerStatus);
        try{
            todoSpinner.setSelection(Integer.parseInt(cursor.getString(2)));
        } catch (NumberFormatException e){
            todoSpinner.setSelection(0);
        }
        Button btnPlus = (Button) view.findViewById(R.id.btnPlus);
        btnPlus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder inputDialog = new AlertDialog.Builder(context);
                inputDialog.setTitle("Create new entry");
                inputDialog.setMessage("Enter what you need to do");
                final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                inputDialog.setView(input);
                inputDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mName = input.getText().toString();
                                ContentValues taskValues = new ContentValues();
                                taskValues.put("NAME", mName);
                                taskValues.put("DESCRIPTION", "description");
                                taskValues.put("TODO", "1");
                                db.insert(table, null, taskValues);
                            }
                        });
                inputDialog.show();

            }

        });

        Button btnMinus = (Button) view.findViewById(R.id.btnMinus);
        btnMinus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cursor.moveToFirst();
                if (cursor.moveToNext()) {
                    TextView textView = view.findViewById(to[1]);
                    db.delete(table, "_id = ?", new String[]{textView.getText().toString()});
                }
            }
        });
        todoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                TextView textView = view.findViewById(to[1]);
                ContentValues todoStatus = new ContentValues();
                todoStatus.put("TODO",position);
                db.update(table,todoStatus, "_id = ?", new String[] {textView.getText().toString()});
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
            }
        });
    }
}