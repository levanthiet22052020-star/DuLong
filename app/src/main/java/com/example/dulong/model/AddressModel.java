package com.example.dulong.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class AddressModel implements Serializable {
    @SerializedName("_id")
    private String _id;

    @SerializedName("userId")
    private String userId;

    @SerializedName("name")
    private String name;

    @SerializedName("phone")
    private String phone;

    @SerializedName("address")
    private String address;

    @SerializedName("isDefault")
    private boolean isDefault;

    // Constructor rỗng
    public AddressModel() {
    }

    // Constructor đầy đủ để gửi lên Server
    public AddressModel(String userId, String name, String phone, String address, boolean isDefault) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.isDefault = isDefault;
    }

    // Getters và Setters
    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}