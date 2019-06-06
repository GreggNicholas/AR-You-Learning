package com.example.aryoulearning.view.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import com.example.aryoulearning.R;
import com.example.aryoulearning.audio.PronunciationUtil;
import com.example.aryoulearning.controller.HintAdapter;
import com.example.aryoulearning.controller.NavListener;
import com.example.aryoulearning.controller.SwitchListener;
import com.example.aryoulearning.model.Model;
import com.example.aryoulearning.view.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class HintFragment extends Fragment {

    private NavListener listener;
    private Switch arSwitch;
    private SwitchListener switchlistener;
    private List<Model> modelList;
    private Button startGameButton;
    private RecyclerView hintRecyclerView;
    private PronunciationUtil pronunciationUtil;
    private TextToSpeech textToSpeech;

    public HintFragment() {

    }

    public static HintFragment newInstance(List<Model> modelList) {
        HintFragment fragment = new HintFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("model-list-key", (ArrayList<? extends Parcelable>) modelList);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            modelList = getArguments().getParcelableArrayList("model-list-key");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavListener) {
            listener = (NavListener) context;
        }
        if (context instanceof SwitchListener) {
            switchlistener = (SwitchListener) context;
        }
        pronunciationUtil = new PronunciationUtil();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hint, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textToSpeech = pronunciationUtil.getTTS(requireContext());
        findViewByIds(view);
        hintRecyclerView.setAdapter(new HintAdapter(modelList, pronunciationUtil, textToSpeech));
        hintRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        setArSwitch();
        startGameButton.setOnClickListener(v -> listener.moveToGameOrARFragment(modelList, MainActivity.AR_SWITCH_STATUS));
    }

    private void findViewByIds(@NonNull View view) {
        startGameButton = view.findViewById(R.id.hint_fragment_button);
        arSwitch = view.findViewById(R.id.switch_ar);
        hintRecyclerView = view.findViewById(R.id.hint_recycler_view);
    }

    private void setArSwitch() {
        arSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> switchlistener.updateSwitchStatus(isChecked));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        switchlistener.updateSwitchStatus(false);
    }
}
