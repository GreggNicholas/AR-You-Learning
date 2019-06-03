package com.example.aryoulearning.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

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
    private NavListener navListener;
    private SwitchListener switchListener;

    private ImageButton resultsIB, homeIB, playagainIB;
    private TextView resultsTV, homeTV, playagainTV;

    private List<List<Model>> listOfModelList;
    private List<Model> modelList = new ArrayList<>();
    private List<String> categoryList = new ArrayList<>();

    private TextToSpeech textToSpeech;
    private PronunciationUtil pronunciationUtil;


    public ReplayFragment() {
    }

    public static ReplayFragment newInstance(List<Model> modelList) {
        ReplayFragment fragment = new ReplayFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_Model, (ArrayList<? extends Parcelable>) modelList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            modelList = getArguments().getParcelableArrayList(ARG_Model);
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
        if (context instanceof SwitchListener) {
            switchListener = (SwitchListener) context;
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
        resultsIB = view.findViewById(R.id.playagain_imagebutton);
        homeIB = view.findViewById(R.id.home_imagebutton);
        playagainIB = view.findViewById(R.id.showresults_imagebutton);
        resultsTV = view.findViewById(R.id.results_textview);
        homeTV = view.findViewById(R.id.home_textview);
        playagainTV = view.findViewById(R.id.playagain_textview);

        resultsIB.setOnClickListener(v -> navListener.moveToResultsFragment(modelList));
        homeIB.setOnClickListener(v -> navListener.moveToListFragment(listOfModelList, categoryList));
        playagainIB.setOnClickListener(v -> navListener.moveToGameOrARFragment(modelList,
                MainActivity.AR_SWITCH_STATUS));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        textToSpeech.shutdown();
        pronunciationUtil = null;
        navListener = null;
        switchListener = null;
    }


}
