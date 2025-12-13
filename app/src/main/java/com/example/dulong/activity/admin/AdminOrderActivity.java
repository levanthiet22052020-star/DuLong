package com.example.dulong.activity.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dulong.R;
import com.example.dulong.adapter.AdminOrderAdapter;
import com.example.dulong.api.RetrofitClient;
import com.example.dulong.model.GeneralResponse;
import com.example.dulong.model.OrderHistoryModel;
import com.example.dulong.model.AdminOrderResponse;
import com.example.dulong.model.OrderResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminOrderActivity extends AppCompatActivity {

    private RecyclerView rcvOrders;
    private ImageView btnBack;
    private List<OrderHistoryModel> orderList = new ArrayList<>();
    private AdminOrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order);

        initViews();
        setupEvents();
        loadAllOrders();
    }

    private void initViews() {
        rcvOrders = findViewById(R.id.rcvOrders);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadAllOrders() {
        // Gọi API lấy tất cả đơn hàng
        RetrofitClient.getInstance().getAdminOrders(null, null, null, null).enqueue(new Callback<AdminOrderResponse>() {
            @Override
            public void onResponse(Call<AdminOrderResponse> call, Response<AdminOrderResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    // API trả về format: { data: { orders: [...], pagination: {...} } }
                    AdminOrderResponse.AdminOrderData data = response.body().getData();
                    if (data != null && data.getOrders() != null) {
                        orderList = data.getOrders();
                    } else {
                        orderList = new ArrayList<>();
                    }
                    setupRecyclerView();
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Không tải được đơn hàng";
                    Toast.makeText(AdminOrderActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminOrderResponse> call, Throwable t) {
                Toast.makeText(AdminOrderActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new AdminOrderAdapter(
                orderList,
                this::showOrderStatusDialog
        );
        rcvOrders.setLayoutManager(new LinearLayoutManager(this));
        rcvOrders.setAdapter(adapter);
    }

    private void showOrderStatusDialog(OrderHistoryModel order) {
        String[] statusOptions = {"Đang xử lý", "Đang giao hàng", "Hoàn thành", "Đã hủy"};
        
        new AlertDialog.Builder(this)
                .setTitle("Cập nhật trạng thái đơn hàng")
                .setItems(statusOptions, (dialog, which) -> {
                    String newStatus = statusOptions[which];
                    updateOrderStatus(order.getId(), newStatus);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void updateOrderStatus(String orderId, String newStatus) {
        // Chuyển đổi trạng thái từ tiếng Việt sang tiếng Anh
        String englishStatus;
        switch (newStatus) {
            case "Đang xử lý":
                englishStatus = "pending";
                break;
            case "Đang giao hàng":
                englishStatus = "shipping";
                break;
            case "Hoàn thành":
                englishStatus = "delivered";
                break;
            case "Đã hủy":
                englishStatus = "cancelled";
                break;
            default:
                englishStatus = "pending";
                break;
        }

        java.util.Map<String, String> requestBody = new java.util.HashMap<>();
        requestBody.put("status", englishStatus);

        RetrofitClient.getInstance().updateAdminOrderStatus(orderId, requestBody).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    Toast.makeText(AdminOrderActivity.this, "Cập nhật trạng thái thành công!", Toast.LENGTH_SHORT).show();
                    loadAllOrders(); // Reload danh sách
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Cập nhật thất bại";
                    Toast.makeText(AdminOrderActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(AdminOrderActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}