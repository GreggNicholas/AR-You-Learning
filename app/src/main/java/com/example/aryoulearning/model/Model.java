package com.example.aryoulearning.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Model implements Parcelable {
    private String name;
    private String image;

    protected Model(Parcel in) {
        name = in.readString();
        image = in.readString();
    }

    public static final Creator<Model> CREATOR = new Creator<Model>() {
        @Override
        public Model createFromParcel(Parcel in) {
            return new Model(in);
        }

        @Override
        public Model[] newArray(int size) {
            return new Model[size];
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

    public Model(String name, String image) {
        this.name = name;
        this.image = image;
    }
}
