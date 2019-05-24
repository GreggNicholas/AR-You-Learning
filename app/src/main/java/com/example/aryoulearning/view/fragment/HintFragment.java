package com.example.aryoulearning.view.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.aryoulearning.R;
import com.example.aryoulearning.controller.HintAdapter;
import com.example.aryoulearning.controller.NavListener;
import com.example.aryoulearning.model.HintObjectModel;

import java.util.ArrayList;
import java.util.List;

public class HintFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private NavListener listener;

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
    public static HintFragment newInstance() {
        HintFragment fragment = new HintFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavListener) {
            listener = (NavListener) context;
        }
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
        List<HintObjectModel> modelList = new ArrayList<>();
        modelList.add(new HintObjectModel(R.drawable.hintdogimage, "dog"));
        modelList.add(new HintObjectModel(R.drawable.hintcatimage, "cat"));
        modelList.add(new HintObjectModel(R.drawable.hintratimage, "rat"));
        modelList.add(new HintObjectModel(R.drawable.hintbatimage, "bat"));
        modelList.add(new HintObjectModel(R.drawable.hintyakimage, "yak"));

        RecyclerView recyclerView = view.findViewById(R.id.hint_recycler_view);
        HintAdapter hintAdapter = new HintAdapter(modelList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setAdapter(hintAdapter);
        recyclerView.setLayoutManager(layoutManager);
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

}
