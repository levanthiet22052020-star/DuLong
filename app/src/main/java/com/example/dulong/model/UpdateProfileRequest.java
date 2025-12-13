package com.example.dulong.model;

public class UpdateProfileRequest {
    private String username;

    public UpdateProfileRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}