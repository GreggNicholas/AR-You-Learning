package com.example.aryoulearning.controller;

import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aryoulearning.R;
import com.example.aryoulearning.audio.PronunciationUtil;
import com.example.aryoulearning.model.Model;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HintAdapter extends RecyclerView.Adapter<HintAdapter.HintViewHolder> {
    private List<Model> modelList;
    private PronunciationUtil pronunciationUtil;
    private TextToSpeech textToSpeech;

    public HintAdapter(List<Model> modelList, PronunciationUtil pronunciationUtil, TextToSpeech textToSpeech) {
        this.modelList = modelList;
        this.pronunciationUtil = pronunciationUtil;
        this.textToSpeech = textToSpeech;
    }

    @NonNull
    @Override
    public HintViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new HintViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.hint_item_view, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HintViewHolder hintViewHolder, int i) {
        hintViewHolder.onBind(modelList.get(i), pronunciationUtil, textToSpeech);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    class HintViewHolder extends RecyclerView.ViewHolder {
        public HintViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void onBind(Model model, final PronunciationUtil pronunciationUtil, final TextToSpeech textToSpeech) {
            ImageView imageView = itemView.findViewById(R.id.hint_fragment_image_view);
            TextView textView = itemView.findViewById(R.id.hint_fragment_textview);
            textView.setTextColor(Color.DKGRAY);
            Picasso.get().load(model.getImage()).into(imageView);
            textView.setText(model.getName());
            itemView.setOnClickListener(v -> {
                pronunciationUtil.textToSpeechAnnouncer(model.getName(), textToSpeech);
                Animation vibrateHintCard = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.vibrate);
                itemView.startAnimation(vibrateHintCard);
            });
        }
    }
}
