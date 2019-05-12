package com.example.aryoulearning.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ModelList<T extends Model> extends ArrayList<Parcelable> implements Parcelable {
    private ArrayList<T> modelList;

    public ModelList() {
        modelList = new ArrayList<T>();
    }

    protected ModelList(Parcel in) {
        modelList = (ArrayList<T>) in.createTypedArrayList(T.CREATOR);
    }

    public static final Creator<ModelList> CREATOR = new Creator<ModelList>() {
        @Override
        public ModelList createFromParcel(Parcel in) {
            return new ModelList(in);
        }

        @Override
        public ModelList[] newArray(int size) {
            return new ModelList[size];
        }
    };

    public void addModel(T model) {
        modelList.add(model);
    }

    public ArrayList<T> getModelList() {
        return modelList;
    }

    public void setModelList(ArrayList<T> modelList) {
        this.modelList = modelList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(modelList);
    }
}
