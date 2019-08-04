package com.capstone.aryoulearning.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.capstone.aryoulearning.R;
import com.capstone.aryoulearning.augmented.ARHostFragment;
import com.capstone.aryoulearning.controller.NavListener;
import com.capstone.aryoulearning.controller.SwitchListener;
import com.capstone.aryoulearning.model.Model;
import com.capstone.aryoulearning.model.ModelResponse;
import com.capstone.aryoulearning.network.RetrofitSingleton;
import com.capstone.aryoulearning.view.fragment.GameFragment;
import com.capstone.aryoulearning.view.fragment.HintFragment;
import com.capstone.aryoulearning.view.fragment.ListFragment;
import com.capstone.aryoulearning.view.fragment.ReplayFragment;
import com.capstone.aryoulearning.view.fragment.ResultsFragment;
import com.capstone.aryoulearning.view.fragment.TutorialFragment;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavListener, SwitchListener {
    private static List<String> categoryList = new ArrayList<>();
    private static List<List<Model>> animalModelList = new ArrayList<>();
    private static List<String> backgroundList = new ArrayList<>();
    public static boolean AR_SWITCH_STATUS;
    public static String currentCategory;


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
                    public void onResponse(@Nonnull Call<List<ModelResponse>> call, @Nonnull Response<List<ModelResponse>> response) {
                        if (response.body() != null) {
                            animalModelList = new ArrayList<>();
                            for (int i = 0; i < response.body().size(); i++) {
                                animalModelList.add(response.body().get(i).getList());
                                categoryList.add(response.body().get(i).getCategory());
                                backgroundList.add(response.body().get(i).getBackground());
                            }
                            moveToListFragment(animalModelList, categoryList, backgroundList);
                        }
                    }

                    @Override
                    public void onFailure(@Nonnull Call<List<ModelResponse>> call, @Nonnull Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    @Override
    public void moveToListFragment(final List<List<Model>> modelResponseList, final List<String> categoryName, final List<String> categoryImages) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ListFragment.newInstance(modelResponseList, categoryName, categoryImages))
                .commit();

    }

    @Override
    public void moveToGameOrARFragment(final List<Model> modelList, final boolean AR_is_on) {
        if (AR_is_on) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, ARHostFragment.newInstance(modelList), "ar_fragment")
//                    .addToBackStack(null)
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, GameFragment.newInstance(modelList), "game_fragment")
//                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void moveToResultsFragment(final List<Model> categoryList) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ResultsFragment.newInstance(categoryList), "result_fragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void moveToHintFragment(final List<Model> modelWithImageNameList) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, HintFragment.newInstance(modelWithImageNameList))
                .addToBackStack(null)
                .commit();
    }
    @Override
    public void backToHintFragment(final List<Model> modelList) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, HintFragment.newInstance(modelList))
                .commit();
    }

    @Override
    public void moveToReplayFragment(final List<Model> modelList, final boolean wasPreviousGameTypeAR) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ReplayFragment.newInstance(modelList,wasPreviousGameTypeAR),"replay_fragment")
                .commit();
    }
    @Override
    public void moveToTutorialScreen(final List<Model> modelList) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, TutorialFragment.newInstance(modelList))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void updateSwitchStatus(final boolean isOn) {
        AR_SWITCH_STATUS = isOn;
    }

    public static List<List<Model>> getAnimalModelList() {
        return animalModelList;
    }

    public static List<String> getBackgroundList() {
        return backgroundList;
    }

    public static List<String> getCategoryList() {
        return categoryList;
    }
}
