package com.example.dulong.activity.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dulong.R;
import com.example.dulong.adapter.AddressAdapter;
import com.example.dulong.api.RetrofitClient;
import com.example.dulong.model.AddressListResponse;
import com.example.dulong.model.AddressModel;
import com.example.dulong.model.GeneralResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressListActivity extends AppCompatActivity {

    RecyclerView recycler;
    AddressAdapter adapter;
    List<AddressModel> list;
    LinearLayout btnAddNew;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        // 1. Ánh xạ View
        recycler = findViewById(R.id.recyclerAddress);
        btnAddNew = findViewById(R.id.btnAddNew);
        ImageView btnBack = findViewById(R.id.btnBack);

        // 2. Cấu hình RecyclerView
        recycler.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();

        // Adapter với các chức năng sửa, xóa, đặt mặc định
        adapter = new AddressAdapter(list, new AddressAdapter.OnAddressActionListener() {
            @Override
            public void onEditAddress(AddressModel model) {
                Intent i = new Intent(AddressListActivity.this, EditAddressActivity.class);
                i.putExtra("mode", "edit");
                i.putExtra("id", model.getId());
                i.putExtra("name", model.getFullName() != null ? model.getFullName() : model.getName());
                i.putExtra("phone", model.getPhone());
                i.putExtra("address", model.getAddress());
                i.putExtra("ward", model.getWard());
                i.putExtra("district", model.getDistrict());
                i.putExtra("province", model.getProvince());
                i.putExtra("isDefault", model.isDefault());
                startActivity(i);
            }

            @Override
            public void onDeleteAddress(AddressModel model) {
                showDeleteConfirmDialog(model);
            }

            @Override
            public void onSetDefaultAddress(AddressModel model) {
                setDefaultAddress(model.getId());
            }
        });
        recycler.setAdapter(adapter);

        // 3. Sự kiện Click nút Thêm Mới
        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddressListActivity.this, EditAddressActivity.class);
                i.putExtra("mode", "add");
                startActivity(i);
            }
        });

        // 4. Sự kiện nút Back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Mỗi khi quay lại màn hình này (ví dụ sau khi thêm/sửa xong), tải lại danh sách
        getListAddress();
    }

    private void getListAddress() {
        sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("USER_ID", "");

        if (userId.isEmpty()) {
            Toast.makeText(this, "Chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi API lấy danh sách địa chỉ mới
        RetrofitClient.getInstance().getUserAddresses(userId).enqueue(new Callback<AddressListResponse>() {
            @Override
            public void onResponse(Call<AddressListResponse> call, Response<AddressListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    list.clear();
                    // Thêm dữ liệu mới từ Server vào List
                    if (response.body().getData() != null) {
                        list.addAll(response.body().getData());
                    }
                    // Cập nhật giao diện
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<AddressListResponse> call, Throwable t) {
                Toast.makeText(AddressListActivity.this, "Lỗi tải dữ liệu: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmDialog(AddressModel model) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa địa chỉ này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteAddress(model.getId());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteAddress(String addressId) {
        RetrofitClient.getInstance().deleteUserAddress(addressId).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    Toast.makeText(AddressListActivity.this, "Đã xóa địa chỉ!", Toast.LENGTH_SHORT).show();
                    getListAddress(); // Reload danh sách
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Xóa thất bại!";
                    Toast.makeText(AddressListActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(AddressListActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDefaultAddress(String addressId) {
        RetrofitClient.getInstance().setDefaultAddress(addressId).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    Toast.makeText(AddressListActivity.this, "Đã đặt làm địa chỉ mặc định!", Toast.LENGTH_SHORT).show();
                    getListAddress(); // Reload danh sách
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Cập nhật thất bại!";
                    Toast.makeText(AddressListActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(AddressListActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}