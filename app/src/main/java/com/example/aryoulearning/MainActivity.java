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
    private RecyclerView rv;
    private CategoryAdapter adapter;

    private static final String TAG = "Main";
    private List<AnimalModel> animalModelList = new LinkedList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        moveToListFragment();
        initializeViews();
        getRetrofit();
    }

    public void initializeViews() {
        rv = findViewById(R.id.category_rv);
    }

    public void showRecyclerView(List<AnimalModel> animalModelList) {
        adapter = new CategoryAdapter(animalModelList);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    public void getRetrofit(){
        RetrofitSingleton.getService()
                .getAnimals()
                .enqueue(new Callback<AnimalList>() {
                    @Override
                    public void onResponse(Call<AnimalList> call, Response<AnimalList> response) {
                        Log.d(TAG, "onResponse: " + response.body().getAnimals().get(0).getName());
                        showRecyclerView(response.body().getAnimals());
                    }
                    @Override
                    public void onFailure(Call<AnimalList> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                    }
                });
    }


    @Override
    public void moveToListFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new ListFragment())
                .commit();
    }
}
