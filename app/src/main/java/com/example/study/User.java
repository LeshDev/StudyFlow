package com.example.study;

public class User {
    private String password;

    private int id;
    private String username;
    private String role;
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.role = "student";
    }

    public String getUsername() { return username; }
    public String getRole() { return role; }

    public int getId() { return id; }
}