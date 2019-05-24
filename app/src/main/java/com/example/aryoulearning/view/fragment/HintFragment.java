package com.example.aryoulearning.view.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.aryoulearning.R;
import com.example.aryoulearning.controller.HintAdapter;
import com.example.aryoulearning.controller.NavListener;
import com.example.aryoulearning.controller.SwitchListener;
import com.example.aryoulearning.model.HintObjectModel;
import com.example.aryoulearning.model.Model;
import com.example.aryoulearning.view.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class HintFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private NavListener listener;
    private Switch arSwitch;
    private SwitchListener switchlistener;
    private List<Model> modelList;


    private String mParam1;
    private String mParam2;


    public HintFragment() {

    }

    //    public static HintFragment newInstance(String param1, String param2) {
//        HintFragment fragment = new HintFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
    public static HintFragment newInstance(List<Model> modelList ) {
        HintFragment fragment = new HintFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hint, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button startGameButton = view.findViewById(R.id.hint_fragment_button);
        arSwitch = view.findViewById(R.id.switch_ar);

//        List<HintObjectModel> hintObjectModelList = new ArrayList<>();
//        hintObjectModelList.add(new HintObjectModel(R.drawable.hintdogimage, "dog"));
//        hintObjectModelList.add(new HintObjectModel(R.drawable.hintcatimage, "cat"));
//        hintObjectModelList.add(new HintObjectModel(R.drawable.hintratimage, "rat"));
//        hintObjectModelList.add(new HintObjectModel(R.drawable.hintbatimage, "bat"));
//        hintObjectModelList.add(new HintObjectModel(R.drawable.hintyakimage, "yak"));

        RecyclerView recyclerView = view.findViewById(R.id.hint_recycler_view);
        HintAdapter hintAdapter = new HintAdapter(modelList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setAdapter(hintAdapter);
        recyclerView.setLayoutManager(layoutManager);
        setArSwitch();
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.moveToGameOrARFragment(modelList, MainActivity.AR_SWITCH_STATUS);
            }
        });
    }

    private void setArSwitch() {
        arSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchlistener.updateSwitchStatus(isChecked);
            }
        });
    }
}
