package com.example.aryoulearning.view.fragment;

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
import android.widget.LinearLayout;

import com.example.aryoulearning.R;
import com.example.aryoulearning.controller.CategoryAdapter;
import com.example.aryoulearning.model.Model;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    public static final String CATEGORY_NAME = "category-name";
    public static final String CATEGORY_IMAGE = "category-image";
    public static final String CATEGORY_KEY = "category-key";
    public static final String SIZE = "SIZE";
    private RecyclerView rv;
    private List<List<Model>> categoryList = new ArrayList<>();
    private List<String> categoryName;
    private List<String> categoryImages;
    private int size;

    private static final String TAG = "Main";

    public static ListFragment newInstance(List<List<Model>> categoryList, List<String> categoryName, List<String> categoryImages) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(CATEGORY_NAME, (ArrayList<String>) categoryName);
        args.putStringArrayList(CATEGORY_IMAGE, (ArrayList<String>) categoryImages);

        for (int i = 0; i < categoryList.size(); i++) {
            args.putParcelableArrayList(CATEGORY_KEY + i, (ArrayList<? extends Parcelable>) categoryList.get(i));
            args.putInt(SIZE, categoryList.size());
        }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            size = getArguments().getInt("SIZE");

            for (int i = 0; i < size; i++) {
                categoryList.add(getArguments().<Model>getParcelableArrayList(CATEGORY_KEY+ i));
            }
            categoryName = getArguments().getStringArrayList(CATEGORY_NAME);
            categoryImages = getArguments().getStringArrayList(CATEGORY_IMAGE);
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
        rv = view.findViewById(R.id.category_rv);
        rv.setAdapter(new CategoryAdapter(categoryList, categoryName, categoryImages));
        rv.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayout.HORIZONTAL,false));
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getFragmentManager().findFragmentByTag("result_fragment") != null){
            getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("result_fragment")).commit();
        }
        if(getFragmentManager().findFragmentByTag("ar_fragment") != null){
            getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("ar_fragment")).commit();
        }
        if(getFragmentManager().findFragmentByTag("game_fragment") != null){
            getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("game_fragment")).commit();
        }
    }
}
