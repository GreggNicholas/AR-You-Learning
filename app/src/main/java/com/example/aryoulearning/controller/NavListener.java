package com.example.aryoulearning.controller;

import com.example.aryoulearning.model.Model;
import com.example.aryoulearning.model.ModelList;

import java.util.List;

public interface NavListener {
    void moveToListFragment(List<ModelList> categoryList);
    void moveToGameFragment(ModelList<Model> modelList);
}
