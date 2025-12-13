package com.example.dulong.model;

public class DashboardResponse {
    private boolean status;
    private String message;
    private DashboardStats data;

    public DashboardResponse() {}

    public DashboardResponse(boolean status, String message, DashboardStats data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public DashboardStats getData() { return data; }
    public void setData(DashboardStats data) { this.data = data; }
}