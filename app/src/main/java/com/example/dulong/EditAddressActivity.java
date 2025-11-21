package com.example.dulong;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditAddressActivity extends AppCompatActivity {

    EditText edtName, edtPhone, edtAddress;
    Switch switchDefault;
    Button btnDelete, btnDone;
    ImageView btnBack;

    String mode = "add"; // mặc định là thêm
    String oldName, oldPhone, oldAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);

        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        switchDefault = findViewById(R.id.switchDefault);

        btnDelete = findViewById(R.id.btnDelete);
        btnDone = findViewById(R.id.btnDone);
        btnBack = findViewById(R.id.btnBack);
        TextView tvTitle = findViewById(R.id.tvTitle);
        mode = getIntent().getStringExtra("mode");

        if ("edit".equals(mode)) {
            oldName = getIntent().getStringExtra("name");
            oldPhone = getIntent().getStringExtra("phone");
            oldAddress = getIntent().getStringExtra("address");

            edtName.setText(oldName);
            edtPhone.setText(oldPhone);
            edtAddress.setText(oldAddress);
            tvTitle.setText("Sửa Địa Chỉ");
            btnDelete.setVisibility(Button.VISIBLE);
        } else {
            tvTitle.setText("Thêm Địa Chỉ");
            btnDelete.setVisibility(Button.GONE); // không cho xóa khi thêm mới
        }

        btnBack.setOnClickListener(v -> finish());

        btnDone.setOnClickListener(v -> saveAddress());
        btnDelete.setOnClickListener(v -> deleteAddress());
    }

    private void saveAddress() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String addr = edtAddress.getText().toString().trim();
        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressedCustom());
        if (name.isEmpty() || phone.isEmpty() || addr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        if ("add".equals(mode)) {
            Toast.makeText(this, "Đã thêm địa chỉ mới!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Đã cập nhật địa chỉ!", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private void deleteAddress() {
        Toast.makeText(this, "Đã xóa địa chỉ!", Toast.LENGTH_SHORT).show();
        finish();
    }
    private void onBackPressedCustom() {
        finish();
    }
}
