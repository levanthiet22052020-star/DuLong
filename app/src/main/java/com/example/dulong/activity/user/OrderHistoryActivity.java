package com.example.dulong.activity.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dulong.R;
import com.example.dulong.adapter.OrderHistoryAdapter;
import com.example.dulong.api.RetrofitClient;
import com.example.dulong.model.OrderHistoryModel;
import com.example.dulong.model.OrderResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private OrderHistoryAdapter adapter;
    private List<OrderHistoryModel> historyList = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        rvHistory = findViewById(R.id.recyclerViewOrders);
        
        // Setup toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Setup RecyclerView
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderHistoryAdapter(historyList, order -> {
            // Sự kiện khi bấm vào 1 đơn hàng -> Qua màn hình chi tiết
            Intent intent = new Intent(this, TrackOrderActivity.class);
            // Truyền dữ liệu đơn hàng sang để hiển thị
            intent.putExtra("ORDER_ID_INTENT", order.getId());
            intent.putExtra("ORDER_STATUS_INTENT", order.getStatus());
            // Có thể truyền thêm ngày, giá... nếu TrackOrderActivity cần
            startActivity(intent);
        });
        rvHistory.setAdapter(adapter);

        // Gọi API lấy dữ liệu
        getOrderHistoryFromApi();
    }

    private void getOrderHistoryFromApi() {
        // 1. Lấy UserId từ SharedPreferences (Đã lưu lúc Đăng nhập)
        sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("USER_ID", "");

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Gọi API thông qua Retrofit
        RetrofitClient.getInstance().getOrderHistory(userId).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OrderResponse apiResponse = response.body();

                    if (apiResponse.isStatus()) {
                        // Xóa dữ liệu cũ
                        historyList.clear();

                        // Thêm dữ liệu mới từ Server
                        historyList.addAll(apiResponse.getData());

                        // Cập nhật giao diện
                        adapter.notifyDataSetChanged();

                        if (historyList.isEmpty()) {
                            Toast.makeText(OrderHistoryActivity.this, "Bạn chưa có đơn hàng nào", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(OrderHistoryActivity.this, "Lỗi: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OrderHistoryActivity.this, "Không lấy được dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage() != null ? t.getMessage() : "Unknown error");
                Toast.makeText(OrderHistoryActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}