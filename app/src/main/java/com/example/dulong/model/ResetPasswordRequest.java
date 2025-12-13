package com.example.dulong.model;

public class ResetPasswordRequest {
    private String phone;
    private String newPassword;

    public ResetPasswordRequest(String phone, String newPassword) {
        this.phone = phone;
        this.newPassword = newPassword;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}