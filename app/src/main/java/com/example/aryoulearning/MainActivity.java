package com.example.aryoulearning;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.aryoulearning.controller.CategoryAdapter;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rv;
    private CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
    }

    public void initializeViews(){
        rv = findViewById(R.id.category_rv);
    }

    public void showRecyclerView(){
//        adapter = new CategoryAdapter();
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }
}
