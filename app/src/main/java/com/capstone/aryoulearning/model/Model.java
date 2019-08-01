package com.capstone.aryoulearning.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Model implements Parcelable {
    private String name;
    private String image;
    private boolean isCorrect;
    private ArrayList<String> wrongAnswerSet;

    public void setWrongAnswerSet(ArrayList<String> wrongAnswerSetEntry) {
        wrongAnswerSet = new ArrayList<>();
        this.wrongAnswerSet.addAll(wrongAnswerSetEntry);
    }

    protected Model(Parcel in) {
        name = in.readString();
        image = in.readString();
        isCorrect = in.readByte() != 0;
        wrongAnswerSet = in.createStringArrayList();
    }

    public Model(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public ArrayList<String> getWrongAnswerSet() {
        return wrongAnswerSet;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
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
        dest.writeByte((byte) (isCorrect ? 1 : 0));
        dest.writeStringList(wrongAnswerSet);
    }
}
