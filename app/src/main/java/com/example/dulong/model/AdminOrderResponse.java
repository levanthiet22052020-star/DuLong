package com.example.dulong.model;

import java.util.List;

public class AdminOrderResponse {
    private boolean status;
    private String message;
    private AdminOrderData data;

    public static class AdminOrderData {
        private List<OrderHistoryModel> orders;
        private Pagination pagination;

        public List<OrderHistoryModel> getOrders() {
            return orders;
        }

        public void setOrders(List<OrderHistoryModel> orders) {
            this.orders = orders;
        }

        public Pagination getPagination() {
            return pagination;
        }

        public void setPagination(Pagination pagination) {
            this.pagination = pagination;
        }
    }

    public static class Pagination {
        private int currentPage;
        private int totalPages;
        private int totalOrders;
        private boolean hasNext;
        private boolean hasPrev;

        // Getters and setters
        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public int getTotalOrders() {
            return totalOrders;
        }

        public void setTotalOrders(int totalOrders) {
            this.totalOrders = totalOrders;
        }

        public boolean isHasNext() {
            return hasNext;
        }

        public void setHasNext(boolean hasNext) {
            this.hasNext = hasNext;
        }

        public boolean isHasPrev() {
            return hasPrev;
        }

        public void setHasPrev(boolean hasPrev) {
            this.hasPrev = hasPrev;
        }
    }

    // Main getters and setters
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

    public AdminOrderData getData() {
        return data;
    }

    public void setData(AdminOrderData data) {
        this.data = data;
    }
}