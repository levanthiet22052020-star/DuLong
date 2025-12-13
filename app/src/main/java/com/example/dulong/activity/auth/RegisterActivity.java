package com.example.dulong.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dulong.R;
import com.example.dulong.api.RetrofitClient;
import com.example.dulong.model.LoginResponse;
import com.example.dulong.model.RegisterRequest;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etPhone;
    private EditText etPass;
    private EditText etConfirmPass;
    private MaterialButton btnRegister;
    private TextView tvLoginLink;
    private ImageView ivPassToggle;
    private ImageView ivConfirmPassToggle;

    private boolean isPassVisible = false;
    private boolean isConfirmPassVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Phải ánh xạ (initViews) xong hết các view...
        initViews();
        // 2. ...thì mới được gán sự kiện (addEvents)
        addEvents();
    }

    private void initViews() {
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etPass = findViewById(R.id.et_password);
        etConfirmPass = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLoginLink = findViewById(R.id.tv_login_link);

        ivPassToggle = findViewById(R.id.iv_pass_toggle);
        ivConfirmPassToggle = findViewById(R.id.iv_confirm_pass_toggle);
    }

    private void addEvents() {
        // Quay lại màn đăng nhập
        tvLoginLink.setOnClickListener(v -> finish());

        // Xử lý đăng ký
        btnRegister.setOnClickListener(v -> handleRegister());

        // Xử lý ẩn/hiện pass
        ivPassToggle.setOnClickListener(v -> {
            if (isPassVisible) {
                etPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivPassToggle.setImageResource(R.drawable.ic_eye_24);
                isPassVisible = false;
            } else {
                etPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                // ivPassToggle.setImageResource(R.drawable.ic_eye_off_24); // Mở comment nếu có icon
                isPassVisible = true;
            }
            etPass.setSelection(etPass.getText().length());
        });

        // Xử lý ẩn/hiện confirm pass
        ivConfirmPassToggle.setOnClickListener(v -> {
            if (isConfirmPassVisible) {
                etConfirmPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivConfirmPassToggle.setImageResource(R.drawable.ic_eye_24);
                isConfirmPassVisible = false;
            } else {
                etConfirmPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                // ivConfirmPassToggle.setImageResource(R.drawable.ic_eye_off_24); // Mở comment nếu có icon
                isConfirmPassVisible = true;
            }
            etConfirmPass.setSelection(etConfirmPass.getText().length());
        });
    }

    private void handleRegister() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String pass = etPass.getText().toString().trim();
        String confirmPass = etConfirmPass.getText().toString().trim();

        // 1. Validate dữ liệu cơ bản
        if (name.isEmpty() || phone.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(confirmPass)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Gọi API
        RegisterRequest request = new RegisterRequest(name, phone, pass, confirmPass);

        Toast.makeText(this, "Đang đăng ký...", Toast.LENGTH_SHORT).show();

        RetrofitClient.getInstance().register(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse apiResponse = response.body();
                    if (apiResponse != null && apiResponse.isStatus()) {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.putExtra("register_phone", phone);
                        startActivity(intent);
                        finishAffinity();
                    } else {
                        String msg = apiResponse != null ? apiResponse.getMessage() : "Đăng ký thất bại";
                        Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Tên đã có người sử dụng " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("RegisterError", t.getMessage() != null ? t.getMessage() : "Unknown error");
            }
        });
    }
}