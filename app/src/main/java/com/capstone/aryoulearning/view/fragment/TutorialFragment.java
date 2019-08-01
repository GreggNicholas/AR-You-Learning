package com.capstone.aryoulearning.view.fragment;


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

import com.capstone.aryoulearning.R;
import com.capstone.aryoulearning.controller.NavListener;
import com.capstone.aryoulearning.model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

public class TutorialFragment extends Fragment {
    public static final String MODEL_LIST = "MODEL_LIST";
    private Button backButton, playVideoButton, startGameButton;
    private NavListener listener;
    private List<Model> modelList;
    private VideoView tutorialVideoView;

    public TutorialFragment() {}

    public static TutorialFragment newInstance(final List<Model> modelList) {
        TutorialFragment tutorialFragment = new TutorialFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(MODEL_LIST, (ArrayList<? extends Parcelable>) modelList);
        tutorialFragment.setArguments(args);
        return tutorialFragment;
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
            modelList = getArguments().getParcelableArrayList(MODEL_LIST);
        }
    }


    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tutorial_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        viewClickListeners();
        playTutorial();
    }

    public void viewClickListeners(){
        startGameButton.setOnClickListener(v -> {
            if (isVideoViewPlaying()) {
                tutorialVideoView.pause();
            }
            listener.moveToGameOrARFragment(modelList, true);
        });
        backButton.setOnClickListener(v -> {
            if (isVideoViewPlaying()) {
                tutorialVideoView.pause();
            }
            listener.backToHintFragment(modelList);
        });
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

    private boolean isVideoViewPlaying() {
        return tutorialVideoView.isPlaying();
    }

    private void playTutorial() {
        MediaController mediaController = new MediaController(requireContext());
        tutorialVideoView.setMediaController(mediaController);
        String pathToTutorial = "android.resource://" + Objects.requireNonNull(getActivity()).getPackageName() + "/" + R.raw.ar_tutorial;
        Uri tutorialUri = Uri.parse(pathToTutorial);
        tutorialVideoView.setVideoURI(tutorialUri);
    }

    private void initializeViews(@NonNull final View view) {
        tutorialVideoView = view.findViewById(R.id.tutorial_videoView);
        backButton = view.findViewById(R.id.tutorial_frag_back_to_hint_button);
        startGameButton = view.findViewById(R.id.tutorial_frag_start_game_button);
        playVideoButton = view.findViewById(R.id.tutorial_frag_play_video_button);
    }
}
