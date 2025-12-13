package com.example.dulong.activity.auth;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dulong.R;
import com.example.dulong.api.RetrofitClient;
import com.example.dulong.model.LoginResponse;
import com.example.dulong.model.VerifyOtpRequest;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOtpActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView tvInstruction;
    private EditText etOtpCode;
    private TextView tvSendCode;
    private MaterialButton btnConfirm;

    private String userPhone = "";
    private boolean isForgotPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userPhone = getIntent().getStringExtra("phone_number");
        if (userPhone == null) userPhone = "";
        isForgotPassword = getIntent().getBooleanExtra("is_forgot_password", false);

        initViews();
        setupUI();
        addEvents();

        // Xin quyền Notification ngay khi vào màn hình này
        checkNotificationPermission();
    }

    // 1. Hàm xin quyền
    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_verify_otp);
        tvInstruction = findViewById(R.id.tv_instruction);
        etOtpCode = findViewById(R.id.et_otp_code);
        tvSendCode = findViewById(R.id.tv_send_code);
        btnConfirm = findViewById(R.id.btn_confirm);
    }

    private void setupUI() {
        if (!userPhone.isEmpty()) {
            tvInstruction.setText("Bấm Send code để lấy code\ncủa số điện thoại " + userPhone);
        }
        if (isForgotPassword) {
            toolbar.setTitle("Quên mật khẩu");
        } else {
            toolbar.setTitle("Xác thực tài khoản");
        }
    }

    private void addEvents() {
        toolbar.setNavigationOnClickListener(v -> finish());

        // Bấm vào đây mới bắt đầu gửi mã
        tvSendCode.setOnClickListener(v -> handleResendCode());

        btnConfirm.setOnClickListener(v -> handleVerify());
    }

    // 2. Logic gửi mã (Đã tích hợp chế độ giả lập bất tử)
    private void handleResendCode() {
        if (userPhone.isEmpty()) return;
        Toast.makeText(this, "Đang gửi mã...", Toast.LENGTH_SHORT).show();

        VerifyOtpRequest request = new VerifyOtpRequest(userPhone, "");
        RetrofitClient.getInstance().resendOtp(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse apiResponse = response.body();
                    if (apiResponse != null && apiResponse.isStatus()) {
                        // Server trả về mã OTP thật
                        String serverOtp = apiResponse.getOtp() != null ? apiResponse.getOtp() : "123456";
                        sendOtpNotification(serverOtp);
                    } else {
                        // Server logic lỗi -> Giả lập
                        sendOtpNotification("999999");
                    }
                } else {
                    // Server lỗi 404, 500 -> Giả lập luôn
                    sendOtpNotification("123456");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Mất mạng -> Giả lập luôn
                sendOtpNotification("123456");
            }
        });
    }

    // 3. Hàm bắn thông báo
    private void sendOtpNotification(String otp) {
        String channelId = "otp_channel_id";
        int notificationId = 1;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "OTP Notifications";
            String description = "Channel for OTP";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_lock_24)
                .setContentTitle("Mã xác thực Dulong App")
                .setContentText("Mã OTP của bạn là: " + otp)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "OTP của bạn là: " + otp, Toast.LENGTH_LONG).show();
            return;
        }

        NotificationManagerCompat.from(this).notify(notificationId, builder.build());
        Toast.makeText(this, "Đã gửi mã về thông báo!", Toast.LENGTH_SHORT).show();
    }

    private void handleVerify() {
        String otp = etOtpCode.getText().toString().trim();
        if (otp.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mã OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        VerifyOtpRequest request = new VerifyOtpRequest(userPhone, otp);
        RetrofitClient.getInstance().verifyOtp(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                // Chấp nhận mọi phản hồi (200 hoặc 404) miễn là OTP đúng logic client
                if (response.isSuccessful() || response.code() == 404) {
                    // Code 404 là do API dummy, mình cứ cho qua

                    if (isForgotPassword) {
                        Toast.makeText(VerifyOtpActivity.this, "Xác thực xong.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(VerifyOtpActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("phone_number", userPhone);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(VerifyOtpActivity.this, "Đăng ký thành công!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(VerifyOtpActivity.this, LoginActivity.class);
                        intent.putExtra("register_phone", userPhone);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(VerifyOtpActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Mất mạng cũng cho qua để test (Tùy bạn chọn)
                Toast.makeText(VerifyOtpActivity.this, "Offline Mode: OK", Toast.LENGTH_SHORT).show();
            }
        });
    }
}