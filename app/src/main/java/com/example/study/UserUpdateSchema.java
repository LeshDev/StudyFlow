package com.example.study;

public class UserUpdateSchema {
    private long user_id;
    private String new_username;
    private String new_password;

    public UserUpdateSchema(long userId, String newUsername, String newPassword) {
        this.user_id = userId;
        this.new_username = newUsername;
        this.new_password = newPassword;
    }
}