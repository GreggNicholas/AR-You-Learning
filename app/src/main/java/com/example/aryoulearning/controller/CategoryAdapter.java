package com.example.aryoulearning.controller;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aryoulearning.Model.AnimalModel;
import com.example.aryoulearning.R;
import com.example.aryoulearning.view.CategoryViewHolder;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {
    private List<AnimalModel> animalList;

    public CategoryAdapter(List<AnimalModel> animalList) {
        this.animalList = animalList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.animal_item, viewGroup, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.onBind(animalList.get(position));
    }

    @Override
    public int getItemCount() {
        return animalList.size();
    }
}
