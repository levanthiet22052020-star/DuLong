package com.example.dulong.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderResponse {
    @SerializedName("status")
    private boolean status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<OrderHistoryModel> data; // Danh sách đơn hàng

    public OrderResponse(boolean status, String message, List<OrderHistoryModel> data) {
        this.status = status;
        this.message = message;
        this.data = data;
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

    public List<OrderHistoryModel> getData() {
        return data;
    }

    public void setData(List<OrderHistoryModel> data) {
        this.data = data;
    }
}