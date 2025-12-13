package com.example.dulong.model;

import java.util.List;

public class NotificationResponse {
    private boolean status;
    private String message;
    private List<NotificationModel> data;

    public NotificationResponse() {}

    public NotificationResponse(boolean status, String message, List<NotificationModel> data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<NotificationModel> getData() { return data; }
    public void setData(List<NotificationModel> data) { this.data = data; }
}