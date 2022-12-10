package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

public class Rssi {
    @SerializedName("d1")
    private int d1;

    @SerializedName("d2")
    private int d2;

    Rssi(int d1, int d2){
        this.d1 = d1;
        this.d2 = d2;
    }
}
