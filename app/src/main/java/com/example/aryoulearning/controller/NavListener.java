package com.example.aryoulearning.controller;

import com.example.aryoulearning.model.Model;

import java.util.List;

public interface NavListener {
    void moveToListFragment(List<List<Model>> animalResponseList, List<String> categoryName, List<String> categoryImage);

    void moveToGameOrARFragment(List<Model> animalResponseList, boolean isAR_on);

    void moveToResultsFragment(List<Model> categoryList);

    void moveToHintFragment(List<Model> animalResponseList);

    void moveToReplayFragment(List<Model> modelList);
}
