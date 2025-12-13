package com.example.dulong.model;

import java.util.List;
import java.util.Map;

public class DashboardStats {
    private TodayStats today;
    private OverviewStats overview;
    private Map<String, Integer> ordersByStatus;
    private List<RevenueChart> revenueChart;
    private List<TopProduct> topProducts;

    public static class TodayStats {
        private int orders;
        private double revenue;

        public TodayStats() {}

        public int getOrders() { return orders; }
        public void setOrders(int orders) { this.orders = orders; }

        public double getRevenue() { return revenue; }
        public void setRevenue(double revenue) { this.revenue = revenue; }
    }

    public static class OverviewStats {
        private int totalUsers;
        private int totalProducts;
        private int totalCategories;
        private int totalOrders;

        public OverviewStats() {}

        public int getTotalUsers() { return totalUsers; }
        public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }

        public int getTotalProducts() { return totalProducts; }
        public void setTotalProducts(int totalProducts) { this.totalProducts = totalProducts; }

        public int getTotalCategories() { return totalCategories; }
        public void setTotalCategories(int totalCategories) { this.totalCategories = totalCategories; }

        public int getTotalOrders() { return totalOrders; }
        public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }
    }

    public static class RevenueChart {
        private String _id;
        private double revenue;
        private int orders;

        public RevenueChart() {}

        public String get_id() { return _id; }
        public void set_id(String _id) { this._id = _id; }

        public double getRevenue() { return revenue; }
        public void setRevenue(double revenue) { this.revenue = revenue; }

        public int getOrders() { return orders; }
        public void setOrders(int orders) { this.orders = orders; }
    }

    public static class TopProduct {
        private String _id;
        private String productName;
        private int totalSold;
        private double revenue;

        public TopProduct() {}

        public String get_id() { return _id; }
        public void set_id(String _id) { this._id = _id; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public int getTotalSold() { return totalSold; }
        public void setTotalSold(int totalSold) { this.totalSold = totalSold; }

        public double getRevenue() { return revenue; }
        public void setRevenue(double revenue) { this.revenue = revenue; }
    }

    // Getters and Setters
    public TodayStats getToday() { return today; }
    public void setToday(TodayStats today) { this.today = today; }

    public OverviewStats getOverview() { return overview; }
    public void setOverview(OverviewStats overview) { this.overview = overview; }

    public Map<String, Integer> getOrdersByStatus() { return ordersByStatus; }
    public void setOrdersByStatus(Map<String, Integer> ordersByStatus) { this.ordersByStatus = ordersByStatus; }

    public List<RevenueChart> getRevenueChart() { return revenueChart; }
    public void setRevenueChart(List<RevenueChart> revenueChart) { this.revenueChart = revenueChart; }

    public List<TopProduct> getTopProducts() { return topProducts; }
    public void setTopProducts(List<TopProduct> topProducts) { this.topProducts = topProducts; }
}