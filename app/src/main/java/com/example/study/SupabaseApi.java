package com.example.study;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseApi {
    // для регистрации
    @POST("rest/v1/people")
    Call<Void> registerUser(
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth,
            @Header("Content-Type") String contentType,
            @Header("Prefer") String prefer,
            @Body User user
    );
    
    // для входа

    @GET("rest/v1/people")
    Call<List<User>> loginUser(
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth,
            @Query("username") String eqName,
            @Query("password") String eqPass
    );
}