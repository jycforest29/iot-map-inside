package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

public class Coordinate {
    @SerializedName("x")
    private double x;
    @SerializedName("y")
    private double y;

    Coordinate(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }
}
