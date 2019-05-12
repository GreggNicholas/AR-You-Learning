package com.example.aryoulearning.Model;

import java.util.List;

public final class AnimalList {
    private List<AnimalModel> animals;

    public AnimalList(List<AnimalModel> animals) {
        this.animals = animals;
    }

    public List<AnimalModel> getAnimals() {
        return animals;
    }
}
