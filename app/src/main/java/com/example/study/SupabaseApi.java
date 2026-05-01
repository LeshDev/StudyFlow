package com.example.study;

import java.util.List;

import okhttp3.ResponseBody;
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

    @POST("rest/v1/class_members")
    Call<ResponseBody> addStudentToTeacher(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Body ClassMember classMember
    );

    // поиск ученика
    @GET("rest/v1/people")
    Call<List<User>> findStudentByNickname(
            @Header("apikey") String apiKey,
            @Query("username") String nickname,
            @Query("role") String role // передадим "student"
    );

    @GET("rest/v1/class_members?select=student_id,people:student_id(id,username)")
    Call<List<User>> getMyStudents(
            @Header("apikey") String apiKey,
            @Query("teacher_id") String eqTeacherId
    );
}