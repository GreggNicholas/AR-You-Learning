package com.capstone.aryoulearning.model;

public class HintObjectModel {
    private int objectImageResource;
    private String objectName;

    public int getObjectImageResource() {
        return objectImageResource;
    }

    public String getObjectName() {
        return objectName;
    }

    public HintObjectModel(int objectImageResource, String objectName) {
        this.objectImageResource = objectImageResource;
        this.objectName = objectName;
    }
}
