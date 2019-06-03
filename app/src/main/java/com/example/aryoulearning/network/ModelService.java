package com.example.aryoulearning.network;

import com.example.aryoulearning.model.ModelResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ModelService {
    String ENDPOINT = "kelveenfabian/75380ae0e467f513762454bbe49a6c2e/raw/e6a72245223fec2fc196f1cbdc5384fa73bc820c/category.json";

    @GET(ENDPOINT)
    Call<List<ModelResponse>> getModels();
}
