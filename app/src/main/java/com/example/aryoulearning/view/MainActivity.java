package com.example.aryoulearning.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.aryoulearning.R;
import com.example.aryoulearning.controller.NavListener;
import com.example.aryoulearning.model.AnimalModel;
import com.example.aryoulearning.model.AnimalResponse;
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
    private List<String> categoryList = new ArrayList<>();
    private List<List<AnimalModel>> animalModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getRetrofit();
    }

    public void getRetrofit() {
        RetrofitSingleton.getService()
                .getAnimals()
                .enqueue(new Callback<List<AnimalResponse>>() {
                    @Override
                    public void onResponse(Call<List<AnimalResponse>> call, Response<List<AnimalResponse>> response) {
                        Log.d(TAG, "onResponse: " + response.body().get(0).getCategory());
                        for(int i = 0; i < response.body().size(); i++){
                            animalModelList.add(response.body().get(i).getList());
                            categoryList.add(response.body().get(i).getCategory());
                        }
                        moveToListFragment(animalModelList, categoryList);
                    }

                    @Override
                    public void onFailure(Call<List<AnimalResponse>> call, Throwable t) {
                        Log.d(TAG, "onFailure: " + t.getMessage());
                        t.printStackTrace();
                    }
                });
    }

    @Override
    public void moveToListFragment(List<List<AnimalModel>> animalResponseList, List<String> categoryName) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ListFragment.newInstance(animalResponseList, categoryName))
                .commit();
    }

    @Override
    public void moveToGameFragment(List<AnimalModel> animalModelList) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, GameFragment.newInstance(animalModelList))
                .addToBackStack(null)
                .commit();
    }
}
