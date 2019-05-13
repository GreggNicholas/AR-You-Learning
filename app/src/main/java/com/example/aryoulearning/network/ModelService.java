package com.example.aryoulearning.network;

import com.example.aryoulearning.model.ModelResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ModelService {
    String ENDPOINT = "kelveenfabian/75380ae0e467f513762454bbe49a6c2e/raw/43775bd9de7e5a99c3eee713458e8034efce66f3/category.json";

    @GET(ENDPOINT)
    Call<List<ModelResponse>> getModels();
}
