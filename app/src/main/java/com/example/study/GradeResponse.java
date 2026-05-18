package com.example.study;

import com.google.gson.annotations.SerializedName;

public class GradeResponse {

    @SerializedName("value")
    private int value;

    @SerializedName("date")
    private String date;

    public GradeResponse() {
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}