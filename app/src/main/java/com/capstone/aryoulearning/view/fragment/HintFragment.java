package com.capstone.aryoulearning.view.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Switch;

import com.capstone.aryoulearning.R;
import com.capstone.aryoulearning.audio.PronunciationUtil;
import com.capstone.aryoulearning.controller.HintAdapter;
import com.capstone.aryoulearning.controller.NavListener;
import com.capstone.aryoulearning.controller.SwitchListener;
import com.capstone.aryoulearning.model.Model;
import com.capstone.aryoulearning.view.MainActivity;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

public class HintFragment extends Fragment {

    private NavListener listener;
    private Switch arSwitch;
    private SwitchListener switchlistener;
    private List<Model> modelList;
    private Button startGameButton, tutorialButton, okButton1, okButton2;
    private RecyclerView hintRecyclerView;
    private PronunciationUtil pronunciationUtil;
    private TextToSpeech textToSpeech;
    private FloatingActionButton backFAB;
    private ShimmerFrameLayout shimmerFrameLayout;
    private View parentalSupervision;
    private View stayAlert;
    private ConstraintLayout constraintLayout;

    public HintFragment() {

    }

    public static HintFragment newInstance(final List<Model> modelList) {
        HintFragment fragment = new HintFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("model-list-key", (ArrayList<? extends Parcelable>) modelList);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableViews(constraintLayout);
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
    public View onCreateView(@Nonnull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hint, container, false);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textToSpeech = pronunciationUtil.getTTS(requireContext());
        initializeViews(view);
        startBlinkText();
        setHintRV();
        setArSwitch();
        viewClickListeners();
        startButtonShimmering();
    }

    private void startButtonShimmering() {
        shimmerFrameLayout.startShimmer();
    }

    public void setHintRV(){
        hintRecyclerView.setAdapter(new HintAdapter(modelList, pronunciationUtil, textToSpeech));
        hintRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    public void viewClickListeners(){

        startGameButton.setOnClickListener(v -> {
            disableViews(constraintLayout);
            constraintLayout.addView(parentalSupervision);
            okButton1.setOnClickListener(v1 -> {
                constraintLayout.removeView(parentalSupervision);
                constraintLayout.addView(stayAlert);
                okButton2.setOnClickListener(v11 -> {
                    constraintLayout.removeView(stayAlert);
                    listener.moveToGameOrARFragment(modelList, MainActivity.AR_SWITCH_STATUS);
                });
            });
        });
        tutorialButton.setOnClickListener(v -> listener.moveToTutorialScreen(modelList));
        backFAB.setOnClickListener(v -> Objects.requireNonNull(getActivity()).onBackPressed());
    }

    public void startBlinkText() {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500); //manage the time of the blink with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(3);
        arSwitch.startAnimation(anim);
    }

    private void initializeViews(@NonNull final View view) {
        startGameButton = view.findViewById(R.id.hint_fragment_button);
        arSwitch = view.findViewById(R.id.switch_ar);
        hintRecyclerView = view.findViewById(R.id.hint_recycler_view);
        tutorialButton = view.findViewById(R.id.hint_frag_tutorial_button);
        backFAB = view.findViewById(R.id.back_btn);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        constraintLayout = view.findViewById(R.id.hint_layout);
        parentalSupervision = getLayoutInflater().inflate(R.layout.parental_supervision_card, constraintLayout, false);
        stayAlert = getLayoutInflater().inflate(R.layout.stay_alert_card, constraintLayout, false);
        okButton1 = parentalSupervision.findViewById(R.id.warning_button_ok_1);
        okButton2 = stayAlert.findViewById(R.id.warning_button_ok_2);

    }


    private void setArSwitch() {
        arSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchlistener.updateSwitchStatus(isChecked);
            if (arSwitch.isChecked()) {
                arSwitch.setTextColor(Color.BLUE);
            } else {
                arSwitch.setTextColor(Color.BLACK);
            }
            arSwitch.clearAnimation();
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        shimmerFrameLayout = null;
    }

    private void disableViews(View view){
        if (view != null) {
            view.setClickable(false);
            if (view instanceof ViewGroup) {
                ViewGroup vg = ((ViewGroup) view);
                for (int i = 0; i < vg.getChildCount(); i++) {
                    disableViews(vg.getChildAt(i));
                }
            }
        }
    }

    private void enableViews(View view){
        if (view != null) {
            view.setClickable(true);
            if (view instanceof ViewGroup) {
                ViewGroup vg = ((ViewGroup) view);
                for (int i = 0; i < vg.getChildCount(); i++) {
                    disableViews(vg.getChildAt(i));
                }
            }
        }
    }


}
