package com.example.myapplication;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitConnection {
    //String BASE_URL = "http://10.210.97.125:8000/";
    String BASE_URL = "https://e70c-121-129-200-106.jp.ngrok.io/";
//    @POST("bluetooth/")
//    Call<Coordinate> getCoordinate(@Body Rssi rssi);

    @POST("bluetooth2/")
    Call<Coordinate> getCoordinate(@Body Rssi rssi);

}
