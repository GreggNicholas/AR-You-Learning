package com.example.aryoulearning.network;

import com.example.aryoulearning.model.ModelResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ModelService {
    String ENDPOINT = "kelveenfabian/75380ae0e467f513762454bbe49a6c2e/raw/7bd7f05a545782bbf7a415c7a653efcf5da0d31f/category.json";

    @GET(ENDPOINT)
    Call<List<ModelResponse>> getModels();
}
