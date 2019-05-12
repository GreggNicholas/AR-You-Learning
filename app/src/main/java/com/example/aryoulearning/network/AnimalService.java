package com.example.aryoulearning.network;

import com.example.aryoulearning.model.AnimalResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AnimalService {
    String ENDPOINT = "kelveenfabian/75380ae0e467f513762454bbe49a6c2e/" +
            "raw/c58b558a32c656abec58575f90d2fcb9eda2d65b/category.json";

    @GET(ENDPOINT)
    Call<AnimalResponse> getAnimals();
}
