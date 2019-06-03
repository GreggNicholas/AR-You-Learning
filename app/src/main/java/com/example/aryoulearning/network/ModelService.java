package com.example.aryoulearning.network;

import com.example.aryoulearning.model.ModelResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ModelService {
    String ENDPOINT = "kelveenfabian/75380ae0e467f513762454bbe49a6c2e/raw/ed81a3b2e78f71540a3c87b4d64b0ec1324bf636/category.json";

    @GET(ENDPOINT)
    Call<List<ModelResponse>> getModels();
}
