package com.example.aryoulearning.network;

import com.example.aryoulearning.model.ModelResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ModelService {
    String ENDPOINT = "kelveenfabian/75380ae0e467f513762454bbe49a6c2e/raw/5f552b935eb97fb458489c83a2d3421a1f5d0590/category.json";

    @GET(ENDPOINT)
    Call<List<ModelResponse>> getModels();
}
