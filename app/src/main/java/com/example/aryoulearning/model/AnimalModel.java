package com.example.aryoulearning.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AnimalModel implements Parcelable {
    private String name;
    private String image;

    protected AnimalModel(Parcel in) {
        name = in.readString();
        image = in.readString();
    }

    public static final Creator<AnimalModel> CREATOR = new Creator<AnimalModel>() {
        @Override
        public AnimalModel createFromParcel(Parcel in) {
            return new AnimalModel(in);
        }

        @Override
        public AnimalModel[] newArray(int size) {
            return new AnimalModel[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(image);
    }

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
