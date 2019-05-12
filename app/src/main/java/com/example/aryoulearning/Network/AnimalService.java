package com.example.aryoulearning.Network;

import com.example.aryoulearning.AnimalModel;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AnimalService {
    String ENDPOINT = "kelveenfabian/75380ae0e467f513762454bbe49a6c2e/" +
            "raw/b255a2a1b43130439a499c729111b3a14ed0cb41/category.json";

    @GET(ENDPOINT)
    Call<AnimalModel> getAnimals();
}
