package com.example.study;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("role")
    private String role;

    @SerializedName("id")
    private long id;

    public long getId() {
        return id;
    }
    @SerializedName("student_id")
    private long studentId;
    @SerializedName("people")
    private User studentData;

    public User getStudentData() {
        return studentData;
    }

    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public String getUsername() { return username; }
    public String getRole() { return role; }

}