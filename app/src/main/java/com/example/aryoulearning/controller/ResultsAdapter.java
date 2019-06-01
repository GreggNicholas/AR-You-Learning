package com.example.aryoulearning.controller;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aryoulearning.model.Model;

import java.util.List;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder> {
    private List<Model> modelList;
    private List<String> modelName;
    private List<String> resultsName;


    public ResultsAdapter(List<Model> modelList, List<String> modelName, List<String> resultsName) {
        this.modelList = modelList;
        this.modelName = modelName;
        this.resultsName = resultsName;
    }

    @NonNull
    @Override
    public ResultsAdapter.ResultsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ResultsAdapter.ResultsViewHolder resultsViewHolder, int i) {
        resultsViewHolder.onBind();
    }


    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ResultsViewHolder extends RecyclerView.ViewHolder {
        private TextView modelTextView;
        private ImageView modeImageview;


        public ResultsViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void onBind() {

        }
    }
}
