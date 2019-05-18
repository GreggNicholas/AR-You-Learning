package com.example.aryoulearning.controller;

import com.example.aryoulearning.model.Model;

import java.util.List;

public interface NavListener {
    void moveToListFragment(List<List<Model>> animalResponseList, List<String> categoryName);
    void moveToGameFragment(List<Model> animalResponseList);
    void moveToResultsFragment();

}
