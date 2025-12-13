package com.example.dulong.activity.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dulong.R;
import com.example.dulong.adapter.AdminCategoryAdapter;
import com.example.dulong.api.RetrofitClient;
import com.example.dulong.model.Category;
import com.example.dulong.model.CategoryResponse;
import com.example.dulong.model.GeneralResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCategoryActivity extends AppCompatActivity {

    private RecyclerView rcvCategories;
    private Button btnAddCategory;
    private ImageView btnBack;
    private List<Category> categoryList = new ArrayList<>();
    private AdminCategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        initViews();
        setupEvents();
        loadCategories();
    }

    private void initViews() {
        rcvCategories = findViewById(R.id.rcvCategories);
        btnAddCategory = findViewById(R.id.btnAddCategory);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
        
        btnAddCategory.setOnClickListener(v -> showAddCategoryDialog());
    }

    private void loadCategories() {
        RetrofitClient.getInstance().getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    categoryList = response.body().getData();
                    setupRecyclerView();
                } else {
                    Toast.makeText(AdminCategoryActivity.this, "Không tải được danh mục", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Toast.makeText(AdminCategoryActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new AdminCategoryAdapter(
                categoryList,
                this::handleEditCategory,
                this::confirmDeleteCategory
        );
        rcvCategories.setLayoutManager(new LinearLayoutManager(this));
        rcvCategories.setAdapter(adapter);
    }

    private void showAddCategoryDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_category, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(dialogView);
        AlertDialog dialog = builder.create();

        EditText edtCategoryName = dialogView.findViewById(R.id.edtCategoryName);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> {
            String name = edtCategoryName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
                return;
            }
            
            createCategoryAPI(name, dialog);
        });

        dialog.show();
    }

    private void createCategoryAPI(String name, Dialog dialog) {
        // Tạo request body cho API
        java.util.Map<String, String> requestBody = new java.util.HashMap<>();
        requestBody.put("name", name);

        RetrofitClient.getInstance().addAdminCategory(requestBody).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    Toast.makeText(AdminCategoryActivity.this, "Thêm danh mục thành công!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadCategories(); // Reload danh sách
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Thất bại";
                    Toast.makeText(AdminCategoryActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(AdminCategoryActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleEditCategory(Category category) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_category, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(dialogView);
        AlertDialog dialog = builder.create();

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        EditText edtCategoryName = dialogView.findViewById(R.id.edtCategoryName);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        tvTitle.setText("Cập Nhật Danh Mục");
        btnSave.setText("Cập nhật");
        edtCategoryName.setText(category.getName());

        btnSave.setOnClickListener(v -> {
            String name = edtCategoryName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Tên danh mục không được trống", Toast.LENGTH_SHORT).show();
                return;
            }
            
            updateCategoryAPI(category.get_id(), name, dialog);
        });

        dialog.show();
    }

    private void updateCategoryAPI(String id, String name, Dialog dialog) {
        java.util.Map<String, String> requestBody = new java.util.HashMap<>();
        requestBody.put("name", name);

        RetrofitClient.getInstance().updateAdminCategory(id, requestBody).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    Toast.makeText(AdminCategoryActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadCategories();
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Lỗi";
                    Toast.makeText(AdminCategoryActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(AdminCategoryActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDeleteCategory(Category category) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa danh mục '" + category.getName() + "'?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteCategoryAPI(category))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteCategoryAPI(Category category) {
        RetrofitClient.getInstance().deleteCategory(category.get_id()).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    Toast.makeText(AdminCategoryActivity.this, "Đã xóa danh mục", Toast.LENGTH_SHORT).show();
                    loadCategories();
                } else {
                    Toast.makeText(AdminCategoryActivity.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(AdminCategoryActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}