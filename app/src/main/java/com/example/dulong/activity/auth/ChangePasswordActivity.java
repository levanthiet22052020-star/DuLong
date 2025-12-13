package com.example.dulong.activity.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dulong.R;
import com.example.dulong.api.RetrofitClient;
import com.example.dulong.model.ChangePasswordRequest;
import com.example.dulong.model.GeneralResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Xử lý Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ
        EditText etOldPass = findViewById(R.id.etOldPass);
        EditText etNewPass = findViewById(R.id.etNewPass);
        EditText etConfirmPass = findViewById(R.id.etConfirmPass);
        Button btnSave = findViewById(R.id.btnSave);

        // Sự kiện nút Lưu
        btnSave.setOnClickListener(v -> {
            String oldPass = etOldPass.getText().toString().trim();
            String newPass = etNewPass.getText().toString().trim();
            String confirmPass = etConfirmPass.getText().toString().trim();

            // 1. Validate dữ liệu
            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPass.length() < 6) {
                Toast.makeText(this, "Mật khẩu mới phải từ 6 ký tự trở lên", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Lấy UserID từ SharedPreferences (đã lưu lúc đăng nhập)
            SharedPreferences sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
            String userId = sharedPreferences.getString("USER_ID", "");

            if (userId == null || userId.isEmpty()) {
                Toast.makeText(this, "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. Gọi API đổi mật khẩu
            ChangePasswordRequest requestBody = new ChangePasswordRequest(userId, oldPass, newPass);

            RetrofitClient.getInstance().changePassword(requestBody).enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                        Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                        finish(); // Đóng màn hình
                    } else {
                        // Lấy thông báo lỗi từ Server (ví dụ: "Mật khẩu cũ không đúng")
                        String msg = response.body() != null ? response.body().getMessage() : "Đổi mật khẩu thất bại";
                        Toast.makeText(ChangePasswordActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Toast.makeText(ChangePasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}