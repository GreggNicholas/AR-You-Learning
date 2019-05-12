package com.example.aryoulearning.Network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitSingleton {
    private static final String BASEURL = "https://gist.githubusercontent.com/";
    public static Retrofit instance;

    private RetrofitSingleton(Retrofit instance) {
        this.instance = instance;
    }

    public static Retrofit getInstance() {
        if (instance == null) {
            instance = new Retrofit.Builder()
                    .baseUrl(BASEURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return instance;
    }
}
