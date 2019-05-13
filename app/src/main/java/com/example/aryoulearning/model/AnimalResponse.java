package com.example.aryoulearning.model;

import java.util.ArrayList;

public final class AnimalResponse {
    private String category;
    private ArrayList<AnimalModel> list;

    public String getCategory() {
        return category;
    }

    public AnimalResponse(ArrayList<AnimalModel> list) {
        this.list = list;
    }

    public ArrayList<AnimalModel> getList() {
        return list;
    }
}
