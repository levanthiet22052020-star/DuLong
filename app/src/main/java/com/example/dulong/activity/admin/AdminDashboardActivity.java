package com.example.dulong.activity.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dulong.R;
import com.example.dulong.activity.admin.AdminProductActivity;
import com.example.dulong.activity.admin.AdminCategoryActivity;
import com.example.dulong.activity.admin.AdminOrderActivity;
import com.example.dulong.api.RetrofitClient;
import com.example.dulong.model.DashboardResponse;
import com.example.dulong.model.DashboardStats;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvRevenue, tvOrders, tvProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupClickListeners();
        loadDashboardStats();
    }

    private void initViews() {
        tvRevenue = findViewById(R.id.tvRevenue);
        tvOrders = findViewById(R.id.tvOrders);
        tvProducts = findViewById(R.id.tvProducts);
    }

    private void setupClickListeners() {
        findViewById(R.id.cardProductManagement).setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminProductActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.cardCategoryManagement).setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminCategoryActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.cardOrderManagement).setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminOrderActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.cardRevenueStats).setOnClickListener(v -> {
            Intent intent = new Intent(this, RevenueStatsActivity.class);
            startActivity(intent);
        });
    }

    private void loadDashboardStats() {
        RetrofitClient.getInstance().getDashboardStats().enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    DashboardStats data = response.body().getData();
                    if (data != null) {
                        updateDashboardUI(data);
                    }
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                // Không hiển thị lỗi để không làm phiền user
            }
        });
    }

    private void updateDashboardUI(DashboardStats data) {
        DecimalFormat formatter = new DecimalFormat("#,###");

        // Cập nhật doanh thu hôm nay
        if (data.getToday() != null && tvRevenue != null) {
            tvRevenue.setText(formatter.format(data.getToday().getRevenue()) + "đ");
        }

        // Cập nhật số đơn hàng hôm nay
        if (data.getToday() != null && tvOrders != null) {
            tvOrders.setText(String.valueOf(data.getToday().getOrders()));
        }

        // Cập nhật tổng số sản phẩm
        if (data.getOverview() != null && tvProducts != null) {
            tvProducts.setText(String.valueOf(data.getOverview().getTotalProducts()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh stats khi quay lại dashboard
        loadDashboardStats();
    }
}