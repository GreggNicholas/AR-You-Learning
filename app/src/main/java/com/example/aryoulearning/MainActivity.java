package com.example.aryoulearning;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

        RetrofitSingleton.getInstance()
                .create(AnimalService.class)
                .getAnimals()
                .enqueue(new Callback<AnimalModel>() {
                    @Override
                    public void onResponse(Call<AnimalModel> call, Response<AnimalModel> response) {
                        Log.d(TAG, "onResponse: " + response.body().getAnimalName());
                    }

                    @Override
                    public void onFailure(Call<AnimalModel> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                    }
                });

    }
}
