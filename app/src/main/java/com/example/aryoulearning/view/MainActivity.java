package com.example.aryoulearning.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
import com.example.aryoulearning.view.fragment.ReplayFragment;
import com.example.aryoulearning.view.fragment.ResultsFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavListener, SwitchListener {
    private static final String TAG = "Main";
    public static final String ARLIST = "ARLIST";
    private static List<String> categoryList = new ArrayList<>();
    private static List<List<Model>> animalModelList = new ArrayList<>();
    private static List<String> backgroundList = new ArrayList<>();
    public static boolean AR_SWITCH_STATUS;
    public static String currentCategory;

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
                            backgroundList.add(response.body().get(i).getBackground());
                        }
                        Log.d("TAG", backgroundList.toString());

//                        for (int i = 0; i <animalModelList.get(0).size(); i++) {
//                            Model animal = animalModelList.get(0).get(i);
//                            if(i % 2 == 0) {
//                                animal.setCorrect(false);
//                                ArrayList<String> testWrongAnswers = new ArrayList<>();
//                                testWrongAnswers.add("this");
//                                testWrongAnswers.add("is");
//                                testWrongAnswers.add("test");
//                                animal.setWrongAnswerSet(testWrongAnswers);
//                            }else{
//                                animal.setCorrect(true);
//                            }
//                        }
//                        moveToReplayFragment(animalModelList.get(0));

                        moveToListFragment(animalModelList, categoryList, backgroundList);
                    }

                    @Override
                    public void onFailure(Call<List<ModelResponse>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    @Override
    public void moveToListFragment(List<List<Model>> modelResponseList, List<String> categoryName, List<String> categoryImages) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ListFragment.newInstance(modelResponseList, categoryName, categoryImages))
                .commit();

    }

    @Override
    public void moveToGameOrARFragment(List<Model> modelList, boolean AR_is_on) {
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
    public void moveToResultsFragment(List<Model> categoryList) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ResultsFragment.newInstance(categoryList), "result_fragment")
                .addToBackStack(null)
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
    public void moveToReplayFragment(List<Model> modelList, boolean wasPreviousGameTypeAR) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ReplayFragment.newInstance(modelList,wasPreviousGameTypeAR),"replay_fragment")
                .commit();
    }

    @Override
    public void updateSwitchStatus(boolean isOn) {
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
