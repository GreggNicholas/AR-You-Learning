package com.example.aryoulearning.model;

import java.util.ArrayList;
import java.util.List;

public final class AnimalResponse {
    private ArrayList<AnimalModel> animals;

    public AnimalResponse(ArrayList<AnimalModel> animals) {
        this.animals = animals;
    }

    public ArrayList<AnimalModel> getAnimals() {
        return animals;
    }
}
