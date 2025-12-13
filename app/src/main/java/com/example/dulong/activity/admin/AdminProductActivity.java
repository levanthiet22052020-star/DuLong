package com.example.dulong.activity.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dulong.R;
import com.example.dulong.adapter.AdminProductAdapter;
import com.example.dulong.adapter.CategoryAdapter;
import com.example.dulong.api.RetrofitClient;
import com.example.dulong.model.Category;
import com.example.dulong.model.CategoryResponse;
import com.example.dulong.model.GeneralResponse;
import com.example.dulong.model.Product;
import com.example.dulong.model.ProductBody;
import com.example.dulong.model.ProductResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProductActivity extends AppCompatActivity {

    // --- Khai báo View ---
    private ImageView btnMenu;
    private EditText edtSearch;
    private ImageView btnSearch;
    private Button btnAddProduct;
    private Button btnManageCategory; // 1. Khai báo nút mới
    private Button btnManageOrder; // 2. Khai báo nút quản lý đơn hàng
    private RecyclerView rcvProducts;

    // Phần Menu Dropdown
    private CardView menuDropdown;
    private RecyclerView rcvCategories;
    private LinearLayout bottomNav;

    private List<Product> productList = new ArrayList<>();
    private AdminProductAdapter adminAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product);

        initViews();
        setupEvents();

        loadAllProducts();
        loadCategories();
    }

    private void initViews() {
        btnMenu = findViewById(R.id.adminBtnMenu);
        edtSearch = findViewById(R.id.adminSearchBar);
        btnSearch = findViewById(R.id.adminBtnSearch);
        btnAddProduct = findViewById(R.id.adminBtnAddProduct);
        rcvProducts = findViewById(R.id.rcvProducts);
        menuDropdown = findViewById(R.id.adminMenuDropdown);
        rcvCategories = findViewById(R.id.rcvCategories);
        bottomNav = findViewById(R.id.adminBottomNav);
    }

    private void setupEvents() {
        // 1. Menu Toggle
        btnMenu.setOnClickListener(v -> {
            if (menuDropdown.getVisibility() == View.VISIBLE) {
                menuDropdown.setVisibility(View.GONE);
            } else {
                menuDropdown.setVisibility(View.VISIBLE);
                menuDropdown.bringToFront();
            }
        });

        // 2. Tìm kiếm
        btnSearch.setOnClickListener(v -> {
            String keyword = edtSearch.getText().toString().trim();
            performSearch(keyword, null);
            hideKeyboard();
            menuDropdown.setVisibility(View.GONE);
        });

        // 3. Thêm sản phẩm
        btnAddProduct.setOnClickListener(v -> showAddProductDialog());

        rcvProducts.setOnTouchListener((v, event) -> {
            if (menuDropdown.getVisibility() == View.VISIBLE) {
                menuDropdown.setVisibility(View.GONE);
            }
            return false;
        });
    }

    // --- CÁC HÀM XỬ LÝ DỮ LIỆU ---

    private void loadCategories() {
        RetrofitClient.getInstance().getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    List<Category> listCat = response.body().getData();
                    CategoryAdapter adapter = new CategoryAdapter(listCat, category -> {
                        edtSearch.setText(category.getName());
                        menuDropdown.setVisibility(View.GONE);
                        hideKeyboard();
                        performSearch(category.getName(), category.get_id());
                    });
                    rcvCategories.setLayoutManager(new LinearLayoutManager(AdminProductActivity.this));
                    rcvCategories.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void loadAllProducts() {
        performSearch(null, null);
    }

    private void performSearch(String keyword, String categoryId) {
        RetrofitClient.getInstance().getListProduct(null, keyword, categoryId).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    productList = new ArrayList<>(response.body().getData());
                    setupRecyclerView(productList);
                } else {
                    Toast.makeText(AdminProductActivity.this, "Không tải được dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Toast.makeText(AdminProductActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView(List<Product> list) {
        adminAdapter = new AdminProductAdapter(
                list,
                this::handleEditProduct,
                this::confirmDeleteProduct
        );
        rcvProducts.setLayoutManager(new LinearLayoutManager(this));
        rcvProducts.setAdapter(adminAdapter);
    }

    private void showAddProductDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_product, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(dialogView);
        AlertDialog dialog = builder.create();

        EditText edtName = dialogView.findViewById(R.id.edtName);
        EditText edtPrice = dialogView.findViewById(R.id.edtPrice);
        EditText edtImage = dialogView.findViewById(R.id.edtImage);
        EditText edtType = dialogView.findViewById(R.id.edtType);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String priceStr = edtPrice.getText().toString().trim();
            String image = edtImage.getText().toString().trim();
            String type = edtType.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên và giá", Toast.LENGTH_SHORT).show();
                return;
            }
            
            double price;
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                price = 0.0;
            }
            
            ProductBody newProduct = new ProductBody(name, price, image, type);
            createProductAPI(newProduct, dialog);
        });
        
        dialog.show();
    }

    private void createProductAPI(ProductBody productBody, Dialog dialog) {
        RetrofitClient.getInstance().addProduct(productBody).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    Toast.makeText(AdminProductActivity.this, "Thêm thành công!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadAllProducts();
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Thất bại";
                    Toast.makeText(AdminProductActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(AdminProductActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleEditProduct(Product product) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_product, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(dialogView);
        AlertDialog dialog = builder.create();

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        EditText edtName = dialogView.findViewById(R.id.edtName);
        EditText edtPrice = dialogView.findViewById(R.id.edtPrice);
        EditText edtImage = dialogView.findViewById(R.id.edtImage);
        EditText edtType = dialogView.findViewById(R.id.edtType);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        tvTitle.setText("Cập Nhật Sản Phẩm");
        btnSave.setText("Cập nhật");
        edtName.setText(product.getName());
        edtPrice.setText(String.format("%.0f", product.getPrice()));
        edtImage.setText(product.getImage() != null ? product.getImage() : "");
        edtType.setText(product.getType() != null ? product.getType() : "");

        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String priceStr = edtPrice.getText().toString().trim();
            String image = edtImage.getText().toString().trim();
            String type = edtType.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Tên và giá không được trống", Toast.LENGTH_SHORT).show();
                return;
            }
            
            double price;
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                price = 0.0;
            }
            
            ProductBody updateBody = new ProductBody(name, price, image, type);
            updateProductAPI(product.get_id(), updateBody, dialog);
        });
        
        dialog.show();
    }

    private void updateProductAPI(String id, ProductBody productBody, Dialog dialog) {
        RetrofitClient.getInstance().updateProduct(id, productBody).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    Toast.makeText(AdminProductActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadAllProducts();
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Lỗi";
                    Toast.makeText(AdminProductActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(AdminProductActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDeleteProduct(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa '" + product.getName() + "'?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteProductAPI(product))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteProductAPI(Product product) {
        RetrofitClient.getInstance().deleteProduct(product.get_id()).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    Toast.makeText(AdminProductActivity.this, "Đã xóa", Toast.LENGTH_SHORT).show();
                    loadAllProducts();
                } else {
                    Toast.makeText(AdminProductActivity.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(AdminProductActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}