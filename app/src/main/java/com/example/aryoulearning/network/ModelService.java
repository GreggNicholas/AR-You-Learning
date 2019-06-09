package com.example.aryoulearning.network;

import com.example.aryoulearning.model.ModelResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ModelService {
    String ENDPOINT = "kelveenfabian/75380ae0e467f513762454bbe49a6c2e/raw/94e0e678c0c5eeb0b32b3edc9a35fc090b93935b/category.json";

    @GET(ENDPOINT)
    Call<List<ModelResponse>> getModels();
}
