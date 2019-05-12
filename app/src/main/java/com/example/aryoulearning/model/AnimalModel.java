package com.example.aryoulearning.model;


import android.os.Parcelable;

public class AnimalModel extends Model implements Parcelable {

    public String getName() {
        return super.getName();
    }

    public String getImage() {
        return super.getImage();
    }

    public AnimalModel(String name, String image) {
        super(name,image);
    }
}
