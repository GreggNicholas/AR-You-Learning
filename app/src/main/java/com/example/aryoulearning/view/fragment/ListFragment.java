package com.example.aryoulearning.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.aryoulearning.R;
import com.example.aryoulearning.controller.CategoryAdapter;
import com.example.aryoulearning.controller.SwitchListener;
import com.example.aryoulearning.model.Model;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {
    private SwitchListener listener;
    private RecyclerView rv;
    private CategoryAdapter adapter;
    private List<List<Model>> categoryList = new ArrayList<>();
    private List<String> categoryName;
    private int size;
    private Switch arSwitch;

    private static final String TAG = "Main";

    public static ListFragment newInstance(List<List<Model>> categoryList, List<String> categoryName) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("category-name", (ArrayList<String>) categoryName);

        for (int i = 0; i < categoryList.size(); i++) {
            args.putParcelableArrayList("category-key" + i, (ArrayList<? extends Parcelable>) categoryList.get(i));
            args.putInt("SIZE", categoryList.size());
        }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SwitchListener) {
            listener = (SwitchListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            size = getArguments().getInt("SIZE");

            for (int i = 0; i < size; i++) {
                categoryList.add(getArguments().<Model>getParcelableArrayList("category-key" + i));
            }

            categoryName = getArguments().getStringArrayList("category-name");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        adapter = new CategoryAdapter(categoryList, categoryName);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

    }

    private void initializeViews(@NonNull View view) {
        rv = view.findViewById(R.id.category_rv);
        arSwitch = view.findViewById(R.id.switch_ar);
        setArSwitch();
    }

    private void setArSwitch() {
        arSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listener.updateSwitchStatus(isChecked);
            }
        });
    }
}
