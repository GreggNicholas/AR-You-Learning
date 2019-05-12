package com.example.aryoulearning.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.aryoulearning.model.AnimalModel;
import com.example.aryoulearning.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder {
    private TextView animalName;

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        animalName = itemView.findViewById(R.id.animal_name);
    }

    public void onBind(final AnimalModel animalModel) {
        animalName.setText(animalModel.getName());
    }
}
