package com.example.dulong.activity.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.dulong.R;
import com.example.dulong.api.RetrofitClient;
import com.example.dulong.model.DashboardResponse;
import com.example.dulong.model.DashboardStats;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RevenueStatsActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvTotalRevenue, tvTotalOrders, tvDeliveredOrders, tvPendingOrders;
    private TextView tvTodayRevenue, tvTodayOrders;
    private CardView cardTotalStats, cardTodayStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue_stats);

        initViews();
        setupEvents();
        loadRevenueStats();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        
        // Thống kê tổng
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvTotalOrders = findViewById(R.id.tvTotalOrders);
        tvDeliveredOrders = findViewById(R.id.tvDeliveredOrders);
        tvPendingOrders = findViewById(R.id.tvPendingOrders);
        
        // Thống kê hôm nay
        tvTodayRevenue = findViewById(R.id.tvTodayRevenue);
        tvTodayOrders = findViewById(R.id.tvTodayOrders);
        
        cardTotalStats = findViewById(R.id.cardTotalStats);
        cardTodayStats = findViewById(R.id.cardTodayStats);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadRevenueStats() {
        // Hiển thị loading
        cardTotalStats.setVisibility(View.GONE);
        cardTodayStats.setVisibility(View.GONE);

        RetrofitClient.getInstance().getDashboardStats().enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    DashboardStats data = response.body().getData();
                    if (data != null) {
                        displayStats(data);
                    }
                } else {
                    Toast.makeText(RevenueStatsActivity.this, "Không thể tải thống kê", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                Toast.makeText(RevenueStatsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayStats(DashboardStats data) {
        DecimalFormat formatter = new DecimalFormat("#,###");

        // Thống kê hôm nay
        if (data.getToday() != null) {
            tvTodayRevenue.setText(formatter.format(data.getToday().getRevenue()) + "đ");
            tvTodayOrders.setText(String.valueOf(data.getToday().getOrders()));
        }

        // Thống kê tổng
        if (data.getOverview() != null) {
            // Tính tổng doanh thu từ revenueChart
            double totalRevenue = 0;
            if (data.getRevenueChart() != null && !data.getRevenueChart().isEmpty()) {
                for (DashboardStats.RevenueChart chart : data.getRevenueChart()) {
                    totalRevenue += chart.getRevenue();
                }
            } else {
                // Ước tính từ doanh thu hôm nay
                totalRevenue = data.getToday() != null ? data.getToday().getRevenue() * 30 : 0;
            }
            
            tvTotalRevenue.setText(formatter.format(totalRevenue) + "đ");
            tvTotalOrders.setText(String.valueOf(data.getOverview().getTotalOrders()));
            
            // Lấy số đơn hàng theo trạng thái
            if (data.getOrdersByStatus() != null) {
                Integer delivered = data.getOrdersByStatus().get("delivered");
                Integer pending = data.getOrdersByStatus().get("pending");
                
                tvDeliveredOrders.setText(String.valueOf(delivered != null ? delivered : 0));
                tvPendingOrders.setText(String.valueOf(pending != null ? pending : 0));
            }
        }

        // Hiển thị cards
        cardTotalStats.setVisibility(View.VISIBLE);
        cardTodayStats.setVisibility(View.VISIBLE);
    }
}