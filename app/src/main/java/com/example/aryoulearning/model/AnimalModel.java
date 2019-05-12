package com.example.aryoulearning.Model;

public class AnimalModel {
    private String name;
    private String image;

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public AnimalModel(String name, String image) {
        this.name = name;
        this.image = image;
    }
}
