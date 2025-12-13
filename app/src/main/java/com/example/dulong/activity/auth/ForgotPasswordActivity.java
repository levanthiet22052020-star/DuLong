package com.example.dulong.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dulong.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class ForgotPasswordActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private EditText etPhone;
    private MaterialButton btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        addEvents();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_forgot_password);
        etPhone = findViewById(R.id.et_phone);
        btnNext = findViewById(R.id.btn_next);
    }

    private void addEvents() {
        toolbar.setNavigationOnClickListener(v -> finish());

        btnNext.setOnClickListener(v -> {
            // Chỉ chuyển trang, không gửi mã ở đây nữa
            handleNextStep();
        });
    }

    private void handleNextStep() {
        String phone = etPhone.getText().toString().trim();

        if (phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        }

        // Chuyển ngay sang màn VerifyOtpActivity
        Intent intent = new Intent(this, VerifyOtpActivity.class);
        intent.putExtra("phone_number", phone);
        intent.putExtra("is_forgot_password", true);
        startActivity(intent);
    }
}