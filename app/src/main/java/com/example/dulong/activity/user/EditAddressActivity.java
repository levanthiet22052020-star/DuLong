package com.example.dulong.activity.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dulong.R;
import com.example.dulong.api.RetrofitClient;
import com.example.dulong.model.AddressModel;
import com.example.dulong.model.GeneralResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAddressActivity extends AppCompatActivity {

    EditText edtName, edtPhone, edtAddress, edtWard, edtDistrict, edtProvince;
    Switch switchDefault;
    Button btnDelete, btnDone;
    ImageView btnBack;
    TextView tvTitle;

    String mode = "add";
    String addressId;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);

        // 1. Ánh xạ View
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        edtWard = findViewById(R.id.edtWard);
        edtDistrict = findViewById(R.id.edtDistrict);
        edtProvince = findViewById(R.id.edtProvince);
        switchDefault = findViewById(R.id.switchDefault);
        btnDelete = findViewById(R.id.btnDelete);
        btnDone = findViewById(R.id.btnDone);
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);

        // 2. Lấy dữ liệu User đã lưu (để lấy SĐT mặc định)
        sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
        String savedPhone = sharedPreferences.getString("USER_PHONE", "");

        // 3. Kiểm tra chế độ (Thêm hay Sửa)
        mode = getIntent().getStringExtra("mode");

        if ("edit".equals(mode)) {
            // --- CHẾ ĐỘ SỬA ---
            tvTitle.setText("Sửa Địa Chỉ");
            btnDelete.setVisibility(View.VISIBLE);

            // Lấy dữ liệu từ màn hình danh sách truyền qua
            addressId = getIntent().getStringExtra("id");
            String oldName = getIntent().getStringExtra("name");
            String oldPhone = getIntent().getStringExtra("phone");
            String oldAddress = getIntent().getStringExtra("address");
            String oldWard = getIntent().getStringExtra("ward");
            String oldDistrict = getIntent().getStringExtra("district");
            String oldProvince = getIntent().getStringExtra("province");
            boolean oldIsDefault = getIntent().getBooleanExtra("isDefault", false);

            // Điền dữ liệu cũ vào ô nhập
            edtName.setText(oldName);
            edtAddress.setText(oldAddress);
            edtWard.setText(oldWard);
            edtDistrict.setText(oldDistrict);
            edtProvince.setText(oldProvince);
            switchDefault.setChecked(oldIsDefault);

            // Logic hiển thị SĐT
            if (oldPhone != null && !oldPhone.isEmpty()) {
                edtPhone.setText(oldPhone);
            } else {
                edtPhone.setText(savedPhone);
            }

        } else {
            // --- CHẾ ĐỘ THÊM MỚI ---
            tvTitle.setText("Thêm Địa Chỉ");
            btnDelete.setVisibility(View.GONE);

            // Tự động điền SĐT đăng nhập vào ô nhập liệu
            edtPhone.setText(savedPhone);
        }

        // 4. Các sự kiện Click
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSaveAddress();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDeleteAddress();
            }
        });
    }

    private void handleSaveAddress() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String ward = edtWard.getText().toString().trim();
        String district = edtDistrict.getText().toString().trim();
        String province = edtProvince.getText().toString().trim();
        boolean isDefault = switchDefault.isChecked();

        String userId = sharedPreferences.getString("USER_ID", "");

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty() || 
            ward.isEmpty() || district.isEmpty() || province.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validation số điện thoại
        if (phone.length() < 3 || phone.length() > 11 || !phone.matches("\\d+")) {
            Toast.makeText(this, "Số điện thoại phải từ 3-11 chữ số!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo AddressModel với đầy đủ thông tin
        AddressModel addressBody = new AddressModel();
        addressBody.setUserId(userId);
        addressBody.setFullName(name);
        addressBody.setPhone(phone);
        addressBody.setAddress(address);
        addressBody.setWard(ward);
        addressBody.setDistrict(district);
        addressBody.setProvince(province);
        addressBody.setDefault(isDefault);

        if ("add".equals(mode)) {
            RetrofitClient.getInstance().addUserAddress(addressBody).enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                        Toast.makeText(EditAddressActivity.this, "Thêm địa chỉ thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String message = response.body() != null ? response.body().getMessage() : "Thêm thất bại!";
                        Toast.makeText(EditAddressActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Toast.makeText(EditAddressActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            RetrofitClient.getInstance().updateUserAddress(addressId, addressBody).enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                        Toast.makeText(EditAddressActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String message = response.body() != null ? response.body().getMessage() : "Cập nhật thất bại!";
                        Toast.makeText(EditAddressActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Toast.makeText(EditAddressActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void handleDeleteAddress() {
        if (addressId == null) return;

        RetrofitClient.getInstance().deleteUserAddress(addressId).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    Toast.makeText(EditAddressActivity.this, "Đã xóa địa chỉ!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Xóa thất bại!";
                    Toast.makeText(EditAddressActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(EditAddressActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}