package com.example.study;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface SupabaseApi {
    @POST("rest/v1/people")
    Call<Void> registerUser(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Header("Content-Type") String contentType,
            @Header("Prefer") String prefer,
            @Body User user
    );
}