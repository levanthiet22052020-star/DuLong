package com.example.dulong.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AddressListResponse {
    @SerializedName("status")
    private boolean status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<AddressModel> data;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<AddressModel> getData() {
        return data;
    }
}