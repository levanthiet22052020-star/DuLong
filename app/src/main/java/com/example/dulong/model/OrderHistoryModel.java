package com.example.dulong.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class OrderHistoryModel implements Serializable {
    @SerializedName("_id")
    private String id;

    @SerializedName("orderCode")
    private String orderCode;

    @SerializedName("date")
    private String date;

    @SerializedName("status")
    private String status;

    @SerializedName("totalPrice")
    private String totalPrice;

    @SerializedName("totalAmount")
    private double totalAmount;

    @SerializedName("productName") // Backward compatibility
    private String productName;

    @SerializedName("quantity") // Backward compatibility
    private int quantity;

    @SerializedName("items")
    private List<OrderItem> items;

    @SerializedName("shippingAddress")
    private ShippingAddress shippingAddress;

    @SerializedName("paymentMethod")
    private String paymentMethod;

    @SerializedName("createdAt")
    private String createdAt;

    // Inner classes
    public static class OrderItem {
        @SerializedName("productId")
        private String productId;

        @SerializedName("name")
        private String name;

        @SerializedName("price")
        private double price;

        @SerializedName("quantity")
        private int quantity;

        @SerializedName("image")
        private String image;

        // Getters and setters
        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public String getImage() { return image; }
        public void setImage(String image) { this.image = image; }
    }

    public static class ShippingAddress {
        @SerializedName("fullName")
        private String fullName;

        @SerializedName("phone")
        private String phone;

        @SerializedName("address")
        private String address;

        @SerializedName("ward")
        private String ward;

        @SerializedName("district")
        private String district;

        @SerializedName("province")
        private String province;

        // Getters and setters
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getWard() { return ward; }
        public void setWard(String ward) { this.ward = ward; }
        public String getDistrict() { return district; }
        public void setDistrict(String district) { this.district = district; }
        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }

        public String getFullAddress() {
            StringBuilder sb = new StringBuilder();
            if (address != null) sb.append(address);
            if (ward != null) sb.append(", ").append(ward);
            if (district != null) sb.append(", ").append(district);
            if (province != null) sb.append(", ").append(province);
            return sb.toString();
        }
    }

    // Constructors
    public OrderHistoryModel() {}

    public OrderHistoryModel(String id, String date, String status, String totalPrice, String productName, int quantity) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.totalPrice = totalPrice;
        this.productName = productName;
        this.quantity = quantity;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getProductName() {
        // Nếu có items, lấy tên sản phẩm đầu tiên
        if (items != null && !items.isEmpty()) {
            if (items.size() == 1) {
                return items.get(0).getName();
            } else {
                return items.get(0).getName() + " và " + (items.size() - 1) + " sản phẩm khác";
            }
        }
        return productName != null ? productName : "Không có sản phẩm";
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        // Nếu có items, tính tổng quantity
        if (items != null && !items.isEmpty()) {
            return items.stream().mapToInt(OrderItem::getQuantity).sum();
        }
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // Helper methods
    public String getDisplayOrderId() {
        return orderCode != null ? orderCode : (id != null ? "#" + id.substring(Math.max(0, id.length() - 8)) : "N/A");
    }

    public String getStatusDisplay() {
        if (status == null) return "Không xác định";
        
        switch (status.toLowerCase()) {
            case "pending": return "Chờ xác nhận";
            case "confirmed": return "Đã xác nhận";
            case "preparing": return "Đang chuẩn bị";
            case "shipping": return "Đang giao";
            case "delivered": return "Hoàn thành";
            case "cancelled": return "Đã hủy";
            default: return status;
        }
    }
}