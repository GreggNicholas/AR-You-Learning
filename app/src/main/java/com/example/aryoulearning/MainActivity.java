package com.example.aryoulearning;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.aryoulearning.controller.CategoryAdapter;
import com.example.aryoulearning.fragment.ListFragment;
import com.example.aryoulearning.model.AnimalList;
import com.example.aryoulearning.model.AnimalModel;
import com.example.aryoulearning.network.RetrofitSingleton;

import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        moveToListFragment();
    }

    @Override
    public void moveToListFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ListFragment.newInstance())
                .commit();
    }
}
