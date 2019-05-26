package com.example.aryoulearning.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.aryoulearning.R;
import com.example.aryoulearning.augmented.ARHostFragment;
import com.example.aryoulearning.controller.NavListener;
import com.example.aryoulearning.controller.SwitchListener;
import com.example.aryoulearning.model.Model;
import com.example.aryoulearning.model.ModelResponse;
import com.example.aryoulearning.network.RetrofitSingleton;
import com.example.aryoulearning.view.fragment.GameFragment;
import com.example.aryoulearning.view.fragment.HintFragment;
import com.example.aryoulearning.view.fragment.ListFragment;
import com.example.aryoulearning.view.fragment.ResultsFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavListener, SwitchListener {
    private static final String TAG = "Main";
    public static final String ARLIST = "ARLIST";
    private List<String> categoryList = new ArrayList<>();
    private List<List<Model>> animalModelList = new ArrayList<>();
    public static boolean AR_SWITCH_STATUS;

//    ArFragment arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getRetrofit();
    }

    public void getRetrofit() {
        RetrofitSingleton.getService()
                .getModels()
                .enqueue(new Callback<List<ModelResponse>>() {
                    @Override
                    public void onResponse(Call<List<ModelResponse>> call, Response<List<ModelResponse>> response) {
                        for (int i = 0; i < response.body().size(); i++) {
                            animalModelList.add(response.body().get(i).getList());
                            categoryList.add(response.body().get(i).getCategory());
                        }
                        moveToListFragment(animalModelList, categoryList);
                    }

                    @Override
                    public void onFailure(Call<List<ModelResponse>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    @Override
    public void moveToListFragment(List<List<Model>> modelResponseList, List<String> categoryName) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ListFragment.newInstance(modelResponseList, categoryName))
                .commit();

    }

    @Override
    public void moveToGameOrARFragment(List<Model> modelList, boolean AR_is_on) {
        if (AR_is_on) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, ARHostFragment.newInstance(modelList))
//                    .addToBackStack(null)
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, GameFragment.newInstance(modelList))
//                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void moveToResultsFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ResultsFragment.newInstance())
                .commit();
    }

    @Override
    public void moveToHintFragment(List<Model> modelWithImageNameList) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, HintFragment.newInstance(modelWithImageNameList))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void updateSwitchStatus(boolean isOn) {
        if (isOn) {
            AR_SWITCH_STATUS = true;
        } else {
            AR_SWITCH_STATUS = false;
        }
    }
}
