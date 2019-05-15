package com.example.aryoulearning.view.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aryoulearning.R;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class ResultsFragment extends Fragment {
    private SharedPreferences sharedPreferences;
    private Set<String> rightAnswer = new HashSet<>();
    private HashMap<String, String> map = new HashMap<>();
    private Set<String> wrongAnswer;
    private int correctAnswer;


    public ResultsFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        extractSharedPrefs();
    }

    public void extractSharedPrefs(){
        rightAnswer = sharedPreferences.getStringSet(GameFragment.RIGHTANSWERS, null);
        wrongAnswer = sharedPreferences.getStringSet(GameFragment.WRONGANSWER, null);
        correctAnswer = sharedPreferences.getInt(GameFragment.ANSWERSCORRECT, 0);
        for(String wrong : wrongAnswer){
            map.put(wrong, sharedPreferences.getString(wrong, null));
        }
    }

    public static ResultsFragment newInstance(){
        return new ResultsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


}
