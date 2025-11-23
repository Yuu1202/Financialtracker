package com.example.financialtracker;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;

public interface AiApiService {

    @POST("generate")
    Call<AiGenerateResponse> generate(@Body AiGenerateRequest request);

    @POST("chat")
    Call<AiChatResponse> chat(@Body AiChatRequest request);

    @GET("models")
    Call<ModelsResponse> listModels();
}
