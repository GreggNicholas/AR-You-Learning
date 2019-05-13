package com.example.aryoulearning.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitSingleton {
    private static final String BASEURL = "https://gist.githubusercontent.com/";
    private static Retrofit instance;


    private static Retrofit getInstance() {
        if (instance == null) {
            instance = new Retrofit.Builder()
                    .baseUrl(BASEURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return instance;
    }

    public static AnimalService getService(){
        return getInstance().create(AnimalService.class);
    }
}
