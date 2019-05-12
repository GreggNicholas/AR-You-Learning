package com.example.aryoulearning.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aryoulearning.R;
import com.example.aryoulearning.controller.CategoryAdapter;
import com.example.aryoulearning.model.AnimalList;
import com.example.aryoulearning.model.AnimalModel;
import com.example.aryoulearning.network.RetrofitSingleton;

import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListFragment extends Fragment {
    private RecyclerView rv;
    private CategoryAdapter adapter;

    private static final String TAG = "Main";
    private List<AnimalModel> animalModelList = new LinkedList<>();

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        getRetrofit();
    }

    private void initializeViews(@NonNull View view) {
        rv = view.findViewById(R.id.category_rv);
    }

    public void showRecyclerView(List<AnimalModel> animalModelList) {
        adapter = new CategoryAdapter(animalModelList);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    public void getRetrofit() {
        RetrofitSingleton.getService()
                .getAnimals()
                .enqueue(new Callback<AnimalList>() {
                    @Override
                    public void onResponse(Call<AnimalList> call, Response<AnimalList> response) {
                        Log.d(TAG, "onResponse: " + response.body().getAnimals().get(0).getName());
                        showRecyclerView(response.body().getAnimals());
                    }

                    @Override
                    public void onFailure(Call<AnimalList> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                    }
                });
    }
}
