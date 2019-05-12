package com.example.aryoulearning.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.aryoulearning.controller.NavListener;
import com.example.aryoulearning.R;
import com.example.aryoulearning.model.AnimalResponse;
import com.example.aryoulearning.model.AnimalModel;
import com.example.aryoulearning.model.Model;
import com.example.aryoulearning.model.ModelList;
import com.example.aryoulearning.network.AnimalService;
import com.example.aryoulearning.network.RetrofitSingleton;
import com.example.aryoulearning.view.fragment.GameFragment;
import com.example.aryoulearning.view.fragment.ListFragment;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavListener {
    private static final String TAG = "Main";
    private List <ModelList> categoryList = new ArrayList<>();
    private ModelList<AnimalModel> animalModelList = new ModelList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getRetrofit();
    }

    @Override
    public void moveToListFragment(List<ModelList> categoryList) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ListFragment.newInstance(categoryList))
                .commit();
    }

    @Override
    public void moveToGameFragment(ModelList<Model> modelList) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, GameFragment.newInstance(modelList))
                .addToBackStack(null)
                .commit();
    }

    public void getRetrofit() {
        RetrofitSingleton.getInstance().create(AnimalService.class)
                .getAnimals()
                .enqueue(new Callback<AnimalResponse>() {
                    @Override
                    public void onResponse(Call<AnimalResponse> call, Response<AnimalResponse> response) {
                        Log.d(TAG, "onResponse: " + response.body().getAnimals().get(0).getName());
                        animalModelList.setModelList(response.body().getAnimals());
                        categoryList.add(animalModelList);
                        moveToListFragment(categoryList);
                    }

                    @Override
                    public void onFailure(Call<AnimalResponse> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                    }
                });
    }
}
