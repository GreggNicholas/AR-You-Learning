package com.capstone.aryoulearning.view.fragment;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.capstone.aryoulearning.R;
import com.capstone.aryoulearning.controller.CategoryAdapter;
import com.capstone.aryoulearning.model.Model;
import com.capstone.aryoulearning.view.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListFragment extends Fragment {

    public static final String CATEGORY_NAME = "category-name";
    public static final String CATEGORY_IMAGE = "category-image";
    public static final String CATEGORY_KEY = "category-key";
    public static final String SIZE = "SIZE";

    private List<List<Model>> categoryList = new ArrayList<>();
    private List<String> categoryName = new ArrayList<>();
    private List<String> categoryImages = new ArrayList<>();
    private RecyclerView rv;
    private TextView textView;
    private Button privacyButton;


    public static ListFragment newInstance(final List<List<Model>> categoryList, final List<String> categoryName, final List<String> categoryImages) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(CATEGORY_NAME, (ArrayList<String>) categoryName);
        args.putStringArrayList(CATEGORY_IMAGE, (ArrayList<String>) categoryImages);
        addCategoryListToBundle(categoryList, args);
        fragment.setArguments(args);
        return fragment;
    }

    public static void addCategoryListToBundle(final List<List<Model>> categoryList, final Bundle args) {
        for (int i = 0; i < categoryList.size(); i++) {
            args.putParcelableArrayList(CATEGORY_KEY + i, (ArrayList<? extends Parcelable>) categoryList.get(i));
            args.putInt(SIZE, categoryList.size());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int size = getArguments().getInt("SIZE");
                for (int i = 0; i < size; i++) {
                    categoryList.add(getArguments().getParcelableArrayList(CATEGORY_KEY + i));
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
        initializeViews(view);
        setListRV();
        animateIt();
        privacyButtonClick();
    }

    private void privacyButtonClick() {
        privacyButton.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://www.termsfeed.com/privacy-policy/be6a24e959c28f96b1f94fce7e33e7d1");
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(i);
        });
    }

    public void animateIt() {
        ObjectAnimator textColor = ObjectAnimator.ofInt(textView, "textColor", Color.GREEN, Color.RED);
        textColor.setInterpolator(new LinearInterpolator());
        textColor.setEvaluator(new ArgbEvaluator());
        textColor.setRepeatCount(ValueAnimator.INFINITE);
        textColor.setRepeatMode(ValueAnimator.REVERSE);
        textColor.setDuration(4000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(textColor);
        animatorSet.start();
    }

    public void initializeViews(final View view) {
        textView = view.findViewById(R.id.category_title);
        rv = view.findViewById(R.id.category_rv);
        privacyButton = view.findViewById(R.id.privacy_button);
    }

    public void setListRV() {
        rv.setAdapter(new CategoryAdapter(categoryList, categoryName, categoryImages));
        rv.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayout.HORIZONTAL, false));
    }

    @SuppressLint("CommitTransaction")
    @Override
    public void onResume() {
        super.onResume();
        MainActivity.AR_SWITCH_STATUS = false;
        if (Objects.requireNonNull(getFragmentManager()).findFragmentByTag("result_fragment") != null) {
            getFragmentManager().beginTransaction().remove(Objects.requireNonNull(getFragmentManager().findFragmentByTag("result_fragment"))).commit();
        }
        if (getChildFragmentManager().findFragmentById(R.id.ux_fragment) != null) {
            getChildFragmentManager().beginTransaction().remove(Objects.requireNonNull(getChildFragmentManager().findFragmentById(R.id.ux_fragment)));
        }
        if (getFragmentManager().findFragmentByTag("ar_fragment") != null) {
            getFragmentManager().beginTransaction().remove(Objects.requireNonNull(getFragmentManager().findFragmentByTag("ar_fragment"))).commit();
        }
        if (getFragmentManager().findFragmentByTag("game_fragment") != null) {
            getFragmentManager().beginTransaction().remove(Objects.requireNonNull(getFragmentManager().findFragmentByTag("game_fragment"))).commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
