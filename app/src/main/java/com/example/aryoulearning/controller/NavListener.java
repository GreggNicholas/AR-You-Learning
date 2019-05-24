package com.example.aryoulearning.controller;

import com.example.aryoulearning.model.Model;

import java.util.List;

public interface NavListener {
    void moveToListFragment(List<List<Model>> animalResponseList, List<String> categoryName);

    void moveToGameFragment(List<Model> animalResponseList, boolean isAR_on);

    void moveToResultsFragment();

    void moveToHintFragment(List<Model> animalResponseList, boolean isAR_on);

}
