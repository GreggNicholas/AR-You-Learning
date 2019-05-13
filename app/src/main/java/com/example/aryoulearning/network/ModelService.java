package com.example.aryoulearning.network;

import com.example.aryoulearning.model.ModelResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ModelService {
    String ENDPOINT = "kelveenfabian/75380ae0e467f513762454bbe49a6c2e/raw/7fb4b57514d2051d7e024c8e4e00f12365067bfe/category.json";

    @GET(ENDPOINT)
    Call<List<ModelResponse>> getModels();
}
