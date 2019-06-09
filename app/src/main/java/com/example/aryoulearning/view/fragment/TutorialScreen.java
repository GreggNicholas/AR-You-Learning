package com.example.aryoulearning.view.fragment;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.aryoulearning.R;
import com.example.aryoulearning.controller.NavListener;
import com.example.aryoulearning.model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavListener) {
            listener = (NavListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            modelList = getArguments().getParcelableArrayList("model_list_key");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tutorial_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewByIds(view);
        startGameButton.setOnClickListener(v -> {
            if (isVideoViewPlaying()) {
                tutorialVideoView.pause();
            }
            listener.moveToGameOrARFragment(modelList, true);
        });
        backToHintFragmentButton.setOnClickListener(v -> {
            if (isVideoViewPlaying()) {
                tutorialVideoView.pause();
            }
            listener.backToHintFragment(modelList);
        });
        playTutorial();
    }

    private boolean isVideoViewPlaying() {
        return tutorialVideoView.isPlaying();
    }

    private void playTutorial() {
        MediaController mediaController = new MediaController(requireContext());
        tutorialVideoView.setMediaController(mediaController);
        String pathToTutorial = "android.resource://" + Objects.requireNonNull(getActivity()).getPackageName() + "/" + R.raw.tutorial;
        Uri tutorialUri = Uri.parse(pathToTutorial);
        tutorialVideoView.setVideoURI(tutorialUri);
        playVideoButton.setOnClickListener(v -> {
            if(isVideoViewPlaying()){
                tutorialVideoView.pause();
                playVideoButton.setBackgroundResource(R.drawable.play_button_paused);
            } else {
                tutorialVideoView.start();
                playVideoButton.setBackgroundResource(R.drawable.play_button_playing);
            }
        });
    }

    private void findViewByIds(@NonNull View view) {
        tutorialVideoView = view.findViewById(R.id.tutorial_videoView);
        backToHintFragmentButton = view.findViewById(R.id.tutorial_frag_back_to_hint_button);
        startGameButton = view.findViewById(R.id.tutorial_frag_start_game_button);
        playVideoButton = view.findViewById(R.id.tutorial_frag_play_video_button);
    }
}
