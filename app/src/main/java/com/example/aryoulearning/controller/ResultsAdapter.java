package com.example.aryoulearning.controller;

import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aryoulearning.R;
import com.example.aryoulearning.audio.PronunciationUtil;
import com.example.aryoulearning.model.Model;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder> {
    private List<Model> modelList;
    private PronunciationUtil pronunUtil;
    private TextToSpeech TTS;


    public ResultsAdapter(List<Model> modelList, PronunciationUtil pronunciationUtil, TextToSpeech TTS) {
        this.modelList = modelList;
        this.pronunUtil = pronunciationUtil;
        this.TTS = TTS;
    }

    @NonNull
    @Override
    public ResultsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        return new ResultsViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.resultmodel_item,
                        viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ResultsViewHolder resultsViewHolder, int position) {
        resultsViewHolder.onBind(modelList.get(position), pronunUtil, TTS);
    }


    @Override
    public int getItemCount() {
        return modelList.size();
    }

    class ResultsViewHolder extends RecyclerView.ViewHolder {
        private TextView modelTextView;
        private ImageView modelImageview;


        ResultsViewHolder(@NonNull View itemView) {
            super(itemView);
            modelTextView = itemView.findViewById(R.id.correctmodel_name);
            modelImageview = itemView.findViewById(R.id.correctmodel_image);
        }

        void onBind(final Model model, final PronunciationUtil pronunUtil, final TextToSpeech TTS) {
            modelTextView = itemView.findViewById(R.id.correctmodel_name);
            modelImageview = itemView.findViewById(R.id.correctmodel_image);
            Picasso.get().load(model.getImage()).into(modelImageview);
            modelTextView.setText(model.getName());
        }
    }
}
