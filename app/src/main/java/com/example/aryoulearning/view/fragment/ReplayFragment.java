package com.example.aryoulearning.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aryoulearning.R;
import com.example.aryoulearning.audio.PronunciationUtil;
import com.example.aryoulearning.controller.NavListener;
import com.example.aryoulearning.controller.SwitchListener;
import com.example.aryoulearning.model.Model;
import com.example.aryoulearning.view.MainActivity;

import java.util.ArrayList;
import java.util.List;


public class ReplayFragment extends Fragment {

    private static final String ARG_Model = "param1";
    private static final String PREVIOUS_GAME_STATUS = "param2";
    private NavListener navListener;

    private CardView resultsButtonCard, homeButtonCard, playagainButtonCard;

    private List<Model> modelList = new ArrayList<>();

    private TextToSpeech textToSpeech;
    private PronunciationUtil pronunciationUtil;

    private boolean previousGameTypeIsAR;


    public ReplayFragment() {
    }

    public static ReplayFragment newInstance(List<Model> modelList, boolean wasPreviousGameTypeAR) {
        ReplayFragment fragment = new ReplayFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_Model, (ArrayList<? extends Parcelable>) modelList);
        args.putBoolean(PREVIOUS_GAME_STATUS,wasPreviousGameTypeAR);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            modelList = getArguments().getParcelableArrayList(ARG_Model);
            previousGameTypeIsAR = getArguments().getBoolean(PREVIOUS_GAME_STATUS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_replay, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavListener) {
            navListener = (NavListener) context;
        }
        pronunciationUtil = new PronunciationUtil();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        textToSpeech = pronunciationUtil.getTTS(requireContext());
    }

    private void initViews(View view) {
        playagainButtonCard = view.findViewById(R.id.cardView_playagain);
        homeButtonCard = view.findViewById(R.id.cardView_home);
        resultsButtonCard = view.findViewById(R.id.cardView_results);

        resultsButtonCard.setOnClickListener(v -> {
            pronunciationUtil.textToSpeechAnnouncer("Showing progress", textToSpeech);
            navListener.moveToResultsFragment(modelList);
        });
        homeButtonCard.setOnClickListener(v -> {
            pronunciationUtil.textToSpeechAnnouncer("Lets go home", textToSpeech);
            Intent homeIntent = new Intent(getContext(), MainActivity.class);
            startActivity(homeIntent);
        });
        playagainButtonCard.setOnClickListener(v -> {
            pronunciationUtil.textToSpeechAnnouncer("Lets play again!", textToSpeech);
            navListener.moveToGameOrARFragment(modelList,previousGameTypeIsAR);
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        textToSpeech.shutdown();
        pronunciationUtil = null;
        navListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getFragmentManager().findFragmentByTag("result_fragment") != null){
            getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("result_fragment")).commit();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().clear().apply();
    }
}
