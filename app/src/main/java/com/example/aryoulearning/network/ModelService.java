package com.example.aryoulearning.network;

import com.example.aryoulearning.model.ModelResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ModelService {
    String ENDPOINT = "kelveenfabian/75380ae0e467f513762454bbe49a6c2e/raw/8eae5e1b67f7a085aa29594150cebfa4bacc58e3/category.json";

    @GET(ENDPOINT)
    Call<List<ModelResponse>> getModels();
}
