package com.example.dulong.activity.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dulong.R;
import com.example.dulong.activity.HomeActivity;
import com.example.dulong.activity.cart.CardActivity;

public class UserProfileActivity extends AppCompatActivity {

    // Khai báo SharedPreferences
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // 1. Ánh xạ View
        TextView tvUsername = findViewById(R.id.tvUsername);
        TextView tvPhone = findViewById(R.id.tvPhone);

        // Nút xem lịch sử đơn hàng (Cả hàng và nút mũi tên)
        ImageView btnDetail = findViewById(R.id.btnDetail);
        LinearLayout containerOrder = findViewById(R.id.btn_order_history_container);

        // Các nút điều hướng khác
        ImageView btnCart = findViewById(R.id.iv_cart);
        ImageView btnSettings = findViewById(R.id.iv_settings);
        ImageView btnHome = findViewById(R.id.nav_home);

        // Xử lý giao diện Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 2. Lấy thông tin User từ SharedPreferences để hiển thị
        sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
        String savedName = sharedPreferences.getString("USER_NAME", "Người dùng");
        String savedPhone = sharedPreferences.getString("USER_PHONE", "09xxxxxxxx");

        tvUsername.setText(savedName);
        tvPhone.setText(savedPhone);

        // 3. XỬ LÝ SỰ KIỆN: XEM LẠI ĐƠN HÀNG
        // Hàm dùng chung để mở màn hình OrderHistoryActivity
        Runnable openOrderHistory = () -> {
            // Chuyển sang màn hình Theo dõi đơn hàng (Nơi đọc dữ liệu từ SharedPreferences)
            Intent intent = new Intent(this, OrderHistoryActivity.class);
            startActivity(intent);
        };

        // Bấm vào mũi tên hoặc cả dòng đều mở
        btnDetail.setOnClickListener(v -> openOrderHistory.run());
        containerOrder.setOnClickListener(v -> openOrderHistory.run());

        // 4. Các sự kiện điều hướng khác
        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(this, CardActivity.class);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            // Xóa các activity cũ để quay về Home sạch sẽ
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}