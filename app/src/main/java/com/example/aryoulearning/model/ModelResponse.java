package com.example.aryoulearning.model;

import java.util.ArrayList;

public final class ModelResponse {
    private String category;
    private ArrayList<Model> list;

    public String getCategory() {
        return category;
    }

    public ModelResponse(ArrayList<Model> list) {
        this.list = list;
    }

    public ArrayList<Model> getList() {
        return list;
    }
}
