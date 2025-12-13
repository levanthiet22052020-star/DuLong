package com.example.dulong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dulong.R;
import com.example.dulong.activity.cart.CardActivity;
import com.example.dulong.activity.user.UserProfileActivity;
import com.example.dulong.adapter.CategoryAdapter;
import com.example.dulong.adapter.ProductAdapter;
import com.example.dulong.api.RetrofitClient;
import com.example.dulong.model.Category;
import com.example.dulong.model.CategoryResponse;
import com.example.dulong.model.ProductResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    // --- View Trang Chủ ---
    private RecyclerView rcvHot;
    private RecyclerView rcvSale;
    private ScrollView scrollHomeContent;

    // --- View Tìm Kiếm / Lọc ---
    private LinearLayout layoutSearchResults;
    private RecyclerView rcvSearchResults;
    private TextView tvSearchResultLabel;
    private EditText edtSearch;
    private ImageView btnSearchIcon;

    // --- Menu & Nav & Danh Mục ---
    private LinearLayout categoryOverlay;
    private RecyclerView rcvCategories;
    private ImageView btnMenu;
    private ImageView btnCart;
    private ImageView btnProfile;
    private ImageView btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        setupEvents();

        // 1. Load sản phẩm trang chủ (Hot/Sale)
        loadHomeProducts();

        // 2. Load danh mục từ Server
        loadCategories();
    }

    private void initViews() {
        // Ánh xạ View Trang chủ
        rcvHot = findViewById(R.id.rcvHot);
        rcvSale = findViewById(R.id.rcvSale);
        scrollHomeContent = findViewById(R.id.scrollHomeContent);

        // Ánh xạ View Tìm kiếm
        layoutSearchResults = findViewById(R.id.layoutSearchResults);
        rcvSearchResults = findViewById(R.id.rcvSearchResults);
        tvSearchResultLabel = findViewById(R.id.tvSearchResultLabel);
        edtSearch = findViewById(R.id.edtSearch);
        btnSearchIcon = findViewById(R.id.btnSearchIcon);

        // Ánh xạ Menu & Nav
        categoryOverlay = findViewById(R.id.categoryOverlay);
        rcvCategories = findViewById(R.id.rcvCategories); // RecyclerView trong menu sổ xuống

        btnMenu = findViewById(R.id.btnMenu);
        btnCart = findViewById(R.id.btnCart);
        btnProfile = findViewById(R.id.btnProfile);
        btnHome = findViewById(R.id.btnHome);
    }

    private void setupEvents() {
        // --- Sự kiện Menu: Ẩn/Hiện danh mục ---
        btnMenu.setOnClickListener(v -> {
            if (categoryOverlay.getVisibility() == View.VISIBLE) {
                categoryOverlay.setVisibility(View.GONE);
            } else {
                categoryOverlay.setVisibility(View.VISIBLE);
                categoryOverlay.bringToFront();

                // Nếu chưa có dữ liệu danh mục thì load lại
                if (rcvCategories.getAdapter() == null || rcvCategories.getAdapter().getItemCount() == 0) {
                    loadCategories();
                }
            }
        });

        // --- Sự kiện Tìm kiếm bằng từ khóa (nhập tay) ---
        btnSearchIcon.setOnClickListener(v -> {
            String keyword = edtSearch.getText().toString().trim();
            if (!keyword.isEmpty()) {
                // Gọi tìm kiếm với keyword, categoryId = null
                performSearch(keyword, null);

                // Ẩn bàn phím và menu
                hideKeyboard();
                categoryOverlay.setVisibility(View.GONE);
            }
        });

        // --- Navigation ---
        btnCart.setOnClickListener(v -> startActivity(new Intent(this, CardActivity.class)));
        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, UserProfileActivity.class)));

        // Nút Home: Quay về màn hình chính
        btnHome.setOnClickListener(v -> showHomeLayout());
    }

    private void loadCategories() {
        RetrofitClient.getInstance().getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    java.util.List<Category> listCat = response.body().getData();

                    // Setup Adapter
                    CategoryAdapter adapter = new CategoryAdapter(listCat, category -> {
                        // === XỬ LÝ KHI BẤM VÀO 1 DANH MỤC ===

                        // 1. Hiển thị tên danh mục lên ô tìm kiếm (để người dùng biết đang xem gì)
                        edtSearch.setText(category.getName());

                        // 2. Gọi API lọc sản phẩm theo ID danh mục (keyword để null)
                        // Đây là mấu chốt để sửa lỗi "tìm không liên quan"
                        performSearch(null, category.get_id());

                        // 3. Ẩn menu danh mục đi
                        categoryOverlay.setVisibility(View.GONE);
                        hideKeyboard();
                    });

                    // Hiển thị dạng lưới 3 cột
                    rcvCategories.setLayoutManager(new GridLayoutManager(HomeActivity.this, 3));
                    rcvCategories.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                // Có thể log lỗi
            }
        });
    }

    /**
     * Hàm tìm kiếm chung: Hỗ trợ tìm theo tên HOẶC theo ID danh mục
     */
    private void performSearch(String keyword, String categoryId) {
        // Ẩn nội dung trang chủ, hiện layout kết quả
        scrollHomeContent.setVisibility(View.GONE);
        layoutSearchResults.setVisibility(View.VISIBLE);

        // Cập nhật dòng thông báo trạng thái
        if (categoryId != null) {
            tvSearchResultLabel.setText("Đang lọc theo danh mục...");
        } else {
            tvSearchResultLabel.setText("Đang tìm kiếm: '" + keyword + "'...");
        }

        // Gọi API với 3 tham số: type=null, search=keyword, categoryId=categoryId
        RetrofitClient.getInstance().getListProduct(null, keyword, categoryId).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    java.util.List<com.example.dulong.model.Product> list = response.body().getData();

                    if (list.isEmpty()) {
                        tvSearchResultLabel.setText("Không tìm thấy sản phẩm nào.");
                    } else {
                        tvSearchResultLabel.setText("Tìm thấy " + list.size() + " sản phẩm.");
                    }

                    // Hiển thị kết quả ra RecyclerView
                    ProductAdapter adapter = new ProductAdapter(list, HomeActivity.this);
                    rcvSearchResults.setLayoutManager(new GridLayoutManager(HomeActivity.this, 2));
                    rcvSearchResults.setAdapter(adapter);
                } else {
                    tvSearchResultLabel.setText("Lỗi khi tải dữ liệu.");
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
                tvSearchResultLabel.setText("Lỗi kết nối.");
            }
        });
    }

    // Quay lại giao diện trang chủ ban đầu
    private void showHomeLayout() {
        layoutSearchResults.setVisibility(View.GONE);
        scrollHomeContent.setVisibility(View.VISIBLE);
        edtSearch.setText(""); // Xóa ô tìm kiếm
        hideKeyboard();
        categoryOverlay.setVisibility(View.GONE);
    }

    // Load sản phẩm Hot/Sale mặc định
    private void loadHomeProducts() {
        loadProductByType("hot", rcvHot);
        loadProductByType("sale", rcvSale);
    }

    // Helper load theo type
    private void loadProductByType(String type, RecyclerView recyclerView) {
        // Tham số search và categoryId để trống
        RetrofitClient.getInstance().getListProduct(type, null, null).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    java.util.List<com.example.dulong.model.Product> list = response.body().getData();
                    ProductAdapter adapter = new ProductAdapter(list, HomeActivity.this);
                    recyclerView.setLayoutManager(new GridLayoutManager(HomeActivity.this, 2));
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                // Handle failure
            }
        });
    }

    // Ẩn bàn phím ảo
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}