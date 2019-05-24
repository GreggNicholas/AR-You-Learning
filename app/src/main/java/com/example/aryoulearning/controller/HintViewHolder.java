package com.example.aryoulearning.controller;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aryoulearning.R;
import com.example.aryoulearning.model.HintObjectModel;
import com.example.aryoulearning.model.Model;
import com.squareup.picasso.Picasso;

public class HintViewHolder extends RecyclerView.ViewHolder {
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
