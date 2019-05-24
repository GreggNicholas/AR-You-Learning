package com.example.aryoulearning.controller;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.aryoulearning.R;
import com.example.aryoulearning.model.Model;

import java.util.List;

public class HintAdapter extends RecyclerView.Adapter<HintViewHolder> {
    private List<Model> modelList;

    public HintAdapter(List<Model> hintModelList) {
        this.modelList = hintModelList;
    }

    @NonNull
    @Override
    public HintViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new HintViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.hint_item_view, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HintViewHolder hintViewHolder, int i) {
        hintViewHolder.onBind(modelList.get(i));
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
}
