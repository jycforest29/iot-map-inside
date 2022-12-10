package com.example.myapplication;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static RetrofitClient instance = null;
    private RetrofitConnection connectionObj;

    private RetrofitClient() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(RetrofitConnection.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        connectionObj = retrofit.create(RetrofitConnection.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public RetrofitConnection getConnectionObj() {
        return connectionObj;
    }
}
