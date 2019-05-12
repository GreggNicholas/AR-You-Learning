package com.example.aryoulearning;

import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.aryoulearning.controller.CategoryAdapter;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rv;
    private CategoryAdapter adapter;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.aryoulearning.Model.AnimalList;
import com.example.aryoulearning.Network.AnimalService;
import com.example.aryoulearning.Network.RetrofitSingleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main";



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
       adapter = new CategoryAdapter();
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));


        RetrofitSingleton.getInstance()
                .create(AnimalService.class)
                .getAnimals()
                .enqueue(new Callback<AnimalList>() {
                    @Override
                    public void onResponse(Call<AnimalList> call, Response<AnimalList> response) {
                        Log.d(TAG, "onResponse: " + response.body().getAnimals().get(0).getAnimalName());
                    }

                    @Override
                    public void onFailure(Call<AnimalList> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                    }
                });


    }
}
