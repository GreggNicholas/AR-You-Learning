package com.example.aryoulearning.view.fragment;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.VideoView;

import com.example.aryoulearning.R;
import com.example.aryoulearning.controller.NavListener;
import com.example.aryoulearning.model.Model;

import java.util.ArrayList;
import java.util.List;

public class TutorialScreen extends Fragment {
    private Button backToHintFragmentButton, playVideoButton, startGameButton;
    private NavListener listener;
    private List<Model> modelList;
    private VideoView tutorialVideoView;

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
