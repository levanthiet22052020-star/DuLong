package com.example.dulong.model;

public class LoginResponse {
    private boolean status;
    private String message;
    private User user;
    private String otp;

    public LoginResponse(boolean status, String message, User user, String otp) {
        this.status = status;
        this.message = message;
        this.user = user;
        this.otp = otp;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public static class User {
        private String _id;
        private String username;
        private String phone;
        private String email;
        private String avatar;
        private String role;
        private String createdAt;

        public User() {
            this.role = "user";
        }

        public User(String _id, String username, String phone, String email, String avatar, String role, String createdAt) {
            this._id = _id;
            this.username = username;
            this.phone = phone;
            this.email = email;
            this.avatar = avatar;
            this.role = role != null ? role : "user";
            this.createdAt = createdAt;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }
}