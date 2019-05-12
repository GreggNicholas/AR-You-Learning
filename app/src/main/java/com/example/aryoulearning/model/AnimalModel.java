package com.example.aryoulearning.Model;

public final class AnimalModel {
    private String name;
    private String image;

    public AnimalModel(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getAnimalName() {
        return name;
    }

    public String getAnimalImage() {
        return image;
    }
}
