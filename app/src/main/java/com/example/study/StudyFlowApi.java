package com.example.study;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface StudyFlowApi {
    @POST("/register")
    Call<User> registerUser(@Body User user);

    @POST("/login")
    Call<User> loginUser(@Body User user);

    @GET("/students/find")
    Call<List<User>> findStudentByNickname(
            @Query("username") String nickname
    );

    @GET("/students/my")
    Call<List<User>> getMyStudents(
            @Query("teacher_id") long teacherId
    );

    @POST("/students/add")
    Call<ResponseBody> addStudentToTeacher(
            @Body ClassMember classMember
    );

    @GET("/students/my_teacher")
    Call<User> getMyTeacher(
            @Query("student_id") long studentId
    );
}