package com.example.aryoulearning.controller;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aryoulearning.R;
import com.example.aryoulearning.model.Model;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HintAdapter extends RecyclerView.Adapter<HintAdapter.HintViewHolder> {
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

    class HintViewHolder extends RecyclerView.ViewHolder {
        public HintViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void onBind(Model model) {
            ImageView imageView = itemView.findViewById(R.id.hint_fragment_image_view);
            TextView textView = itemView.findViewById(R.id.hint_fragment_textview);
            Picasso.get().load(model.getImage()).into(imageView);
            textView.setText(model.getName());
        }
    }
}
