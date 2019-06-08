package com.example.aryoulearning.view.fragment;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aryoulearning.R;
import com.example.aryoulearning.model.Model;

import java.util.ArrayList;
import java.util.List;

public class TutorialScreen extends Fragment {


    public TutorialScreen() {
    }
    public static TutorialScreen newInstance(List<Model> modelList) {
        TutorialScreen tutorialScreen = new TutorialScreen();
        Bundle args = new Bundle();
        args.putParcelableArrayList("model_list_key", (ArrayList<? extends Parcelable>) modelList);
        tutorialScreen.setArguments(args);
        return tutorialScreen;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tutorial_screen, container, false);
    }

}
