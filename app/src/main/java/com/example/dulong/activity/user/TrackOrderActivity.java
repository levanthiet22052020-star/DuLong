package com.example.dulong.activity.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dulong.R;

public class TrackOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);

        // 1. Ánh xạ View
        ImageView btnBack = findViewById(R.id.btnBack);

        // --- SỬA LỖI Ở ĐÂY ---
        // Trong XML id là tvEstimateDate, nên ở đây phải gọi đúng tvEstimateDate
        TextView tvEstimateDate = findViewById(R.id.tvEstimateDate);

        TextView tvTrackingCode = findViewById(R.id.tvTrackingCode);
        TextView tvOrderStatus = findViewById(R.id.tvOrderStatus);

        // 2. Xử lý nút Back
        btnBack.setOnClickListener(v -> finish());

        // 3. ĐỌC DỮ LIỆU ĐƠN HÀNG TỪ SHARED PREFERENCES
        SharedPreferences prefs = getSharedPreferences("ORDER_INFO", Context.MODE_PRIVATE);

        String orderId = prefs.getString("ORDER_ID", "");
        String status = prefs.getString("ORDER_STATUS", "");
        // Nếu bên CheckoutActivity bạn có lưu ngày thì lấy ra, ko thì để mặc định
        String orderDate = prefs.getString("ORDER_DATE", "");

        if (!orderId.isEmpty()) {
            // Hiển thị mã vận đơn
            if (tvTrackingCode != null) {
                tvTrackingCode.setText(orderId);
            }

            // Hiển thị trạng thái (nếu có View và có dữ liệu)
            if (tvOrderStatus != null && !status.isEmpty()) {
                tvOrderStatus.setText(status);
            }

            // Hiển thị ngày dự kiến (nếu cần thay đổi text mặc định)
            // if (tvEstimateDate != null && !orderDate.isEmpty()) {
            //    tvEstimateDate.setText("Ngày giao hàng dự kiến: " + orderDate);
            // }

        } else {
            // Nếu chưa có đơn nào
            Toast.makeText(this, "Bạn chưa có đơn hàng nào", Toast.LENGTH_SHORT).show();
            if (tvTrackingCode != null) {
                tvTrackingCode.setText("---");
            }
        }
    }
}