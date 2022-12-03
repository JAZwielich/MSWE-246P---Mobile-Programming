package com.hfad.todolist;

import android.os.Bundle;
import android.app.Activity;


public class DetailActivity extends Activity {
    public static final String EXTRA_TODO_ID = "id";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        TodoDetailFragment todoDetailFragment = (TodoDetailFragment)
                getFragmentManager().findFragmentById(R.id.detail_frag);
        int todoID = (int) getIntent().getExtras().get(EXTRA_TODO_ID);
        todoDetailFragment.setTodoID(todoID);
    }
}