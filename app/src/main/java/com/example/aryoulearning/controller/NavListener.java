package com.example.aryoulearning.controller;

import com.example.aryoulearning.model.AnimalModel;

import java.util.List;

public interface NavListener {
    void moveToListFragment(List<List<AnimalModel>> animalResponseList, List<String> categoryName);
    void moveToGameFragment(List<AnimalModel> animalResponseList);
}
