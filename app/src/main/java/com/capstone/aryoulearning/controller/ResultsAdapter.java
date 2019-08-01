package com.capstone.aryoulearning.controller;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.aryoulearning.R;
import com.capstone.aryoulearning.audio.PronunciationUtil;
import com.capstone.aryoulearning.model.Model;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder> {
    private List<Model> modelList;
    private int listSize;
    private PronunciationUtil pronunUtil;
    private TextToSpeech TTS;


    public ResultsAdapter(List<Model> modelList, PronunciationUtil pronunciationUtil, TextToSpeech TTS, int listSize) {
        this.modelList = modelList;
        this.pronunUtil = pronunciationUtil;
        this.TTS = TTS;
        this.listSize = listSize;
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
        return listSize;
    }

    class ResultsViewHolder extends RecyclerView.ViewHolder {
        private TextView modelTextView;
        private ImageView modelImageview;
        private TextView modelAnswer;
        private ImageView resultImage;
        private TextView promptText;

        ResultsViewHolder(@NonNull View itemView) {
            super(itemView);
            modelTextView = itemView.findViewById(R.id.correctmodel_name);
            promptText = itemView.findViewById(R.id.result_prompt_textView);
            modelImageview = itemView.findViewById(R.id.correctmodel_image);
            modelAnswer = itemView.findViewById(R.id.correctmodel_answer);
            resultImage = itemView.findViewById(R.id.result_imageView);
        }

        @SuppressLint("ResourceAsColor")
        void onBind(final Model model, final PronunciationUtil pronunUtil, final TextToSpeech TTS) {
            String correct = "Correct";

            String wrong = "";
            for (String s:model.getWrongAnswerSet()) {
                wrong += s +", ";
            }

            String name = model.getName().toUpperCase().charAt(0) + model.getName().toLowerCase().substring(1);
            CardView cardView = itemView.findViewById(R.id.cardView4);

            modelTextView.setText(name);

            Picasso.get().load(model.getImage()).into(modelImageview);

            if (model.isCorrect()) {
                resultImage.setImageResource(R.drawable.star);
                modelAnswer.setText(correct);
                promptText.setVisibility(View.INVISIBLE);
            } else {
                cardView.setCardBackgroundColor(Color.parseColor("#D81B60"));
                resultImage.setImageResource(R.drawable.error);
                modelAnswer.setText(wrong.substring(0,wrong.length()-2));
            }
            cardView.setOnClickListener(v -> pronunUtil.textToSpeechAnnouncer(name,TTS));
        }
    }
}
