package com.example.dulong.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dulong.R;
import com.example.dulong.api.RetrofitClient;
import com.example.dulong.model.LoginResponse;
import com.example.dulong.model.ResetPasswordRequest;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private EditText etNewPass;
    private EditText etConfirmPass;
    private ImageView ivNewPassToggle;
    private ImageView ivConfirmPassToggle;
    private MaterialButton btnConfirm;

    private String userPhone = "";
    private boolean isNewPassVisible = false;
    private boolean isConfirmPassVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Nhận SĐT từ màn hình VerifyOtp truyền sang
        userPhone = getIntent().getStringExtra("phone_number");
        if (userPhone == null) userPhone = "";

        initViews();
        addEvents();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_reset_password);
        etNewPass = findViewById(R.id.et_new_password);
        etConfirmPass = findViewById(R.id.et_confirm_password);
        ivNewPassToggle = findViewById(R.id.iv_pass_toggle_new);
        ivConfirmPassToggle = findViewById(R.id.iv_pass_toggle_confirm);
        btnConfirm = findViewById(R.id.btn_confirm_reset);
    }

    private void addEvents() {
        // Nút Back
        toolbar.setNavigationOnClickListener(v -> finish());

        // Nút xác nhận đổi mật khẩu
        btnConfirm.setOnClickListener(v -> handleResetPassword());

        // Toggle ẩn/hiện mật khẩu mới
        ivNewPassToggle.setOnClickListener(v -> {
            if (isNewPassVisible) {
                etNewPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivNewPassToggle.setImageResource(R.drawable.ic_eye_24);
                isNewPassVisible = false;
            } else {
                etNewPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                isNewPassVisible = true;
            }
            etNewPass.setSelection(etNewPass.getText().length());
        });

        // Toggle ẩn/hiện mật khẩu xác nhận
        ivConfirmPassToggle.setOnClickListener(v -> {
            if (isConfirmPassVisible) {
                etConfirmPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivConfirmPassToggle.setImageResource(R.drawable.ic_eye_24);
                isConfirmPassVisible = false;
            } else {
                etConfirmPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                isConfirmPassVisible = true;
            }
            etConfirmPass.setSelection(etConfirmPass.getText().length());
        });
    }

    private void handleResetPassword() {
        String newPass = etNewPass.getText().toString().trim();
        String confirmPass = etConfirmPass.getText().toString().trim();

        // 1. Validate
        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userPhone.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Gọi API
        Toast.makeText(this, "Đang cập nhật mật khẩu...", Toast.LENGTH_SHORT).show();

        ResetPasswordRequest request = new ResetPasswordRequest(userPhone, newPass);

        RetrofitClient.getInstance().resetPassword(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    Toast.makeText(ResetPasswordActivity.this, "Đổi mật khẩu thành công!", Toast.LENGTH_LONG).show();

                    // 3. Chuyển về màn hình Login để đăng nhập lại
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    // Xóa sạch back stack để không back lại được màn đổi pass
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("register_phone", userPhone); // Điền sẵn SĐT cho tiện
                    startActivity(intent);
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Cập nhật thất bại";
                    Toast.makeText(ResetPasswordActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}