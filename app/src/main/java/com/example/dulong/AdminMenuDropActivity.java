package com.example.dulong; // LƯU Ý: Đổi thành tên package dự án của bạn

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminMenuDropActivity extends AppCompatActivity {

    // 1. Khai báo biến giao diện
    private ImageView btnMenu, btnSearch;
    private EditText edtSearch;
    private LinearLayout menuDropdown; // Khối menu xổ xuống
    private Button btnAddProduct;

    // Các nút Sửa/Xóa cho 4 sản phẩm mẫu (Hardcode)
    private ImageView btnEdit1, btnDelete1;
    private ImageView btnEdit2, btnDelete2;
    private ImageView btnEdit3, btnDelete3;
    private ImageView btnEdit4, btnDelete4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Đảm bảo tên file layout đúng với file XML bạn gửi
        setContentView(R.layout.admin_menudrop);

        // 2. Ánh xạ View
        initViews();

        // 3. Thiết lập trạng thái ban đầu
        // Ẩn menu dropdown đi khi mới vào màn hình
        if (menuDropdown != null) {
            menuDropdown.setVisibility(View.GONE);
        }

        // 4. Gắn sự kiện click
        setupListeners();
    }

    private void initViews() {
        // Header & Menu
        btnMenu = findViewById(R.id.adminBtnMenu);
        btnSearch = findViewById(R.id.adminBtnSearch);
        edtSearch = findViewById(R.id.adminSearchBar);
        menuDropdown = findViewById(R.id.adminMenuDropdown);

        // Nút chức năng chính
        btnAddProduct = findViewById(R.id.adminBtnAddProduct);

        // Sản phẩm 1
        btnEdit1 = findViewById(R.id.adminBtnEdit1);
        btnDelete1 = findViewById(R.id.adminBtnDelete1);

        // Sản phẩm 2
        btnEdit2 = findViewById(R.id.adminBtnEdit2);
        btnDelete2 = findViewById(R.id.adminBtnDelete2);

        // Sản phẩm 3
        btnEdit3 = findViewById(R.id.adminBtnEdit3);
        btnDelete3 = findViewById(R.id.adminBtnDelete3);

        // Sản phẩm 4
        btnEdit4 = findViewById(R.id.adminBtnEdit4);
        btnDelete4 = findViewById(R.id.adminBtnDelete4);
    }

    private void setupListeners() {
        // --- Xử lý nút MENU (Quan trọng nhất) ---
        if (btnMenu != null) {
            btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (menuDropdown == null) return;

                    // Kiểm tra trạng thái hiện tại để ẩn/hiện
                    if (menuDropdown.getVisibility() == View.VISIBLE) {
                        menuDropdown.setVisibility(View.GONE); // Đang hiện -> Ẩn
                    } else {
                        menuDropdown.setVisibility(View.VISIBLE); // Đang ẩn -> Hiện
                    }
                }
            });
        }

        // --- Xử lý nút Tìm kiếm ---
        if (btnSearch != null) {
            btnSearch.setOnClickListener(v -> {
                String query = edtSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    Toast.makeText(this, "Đang tìm: " + query, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Vui lòng nhập từ khóa", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // --- Xử lý nút Thêm sản phẩm ---
        if (btnAddProduct != null) {
            btnAddProduct.setOnClickListener(v ->
                    Toast.makeText(this, "Mở màn hình thêm sản phẩm", Toast.LENGTH_SHORT).show()
            );
        }

        // --- Xử lý các nút Sửa/Xóa cho từng sản phẩm ---
        setupItemActions(btnEdit1, btnDelete1, "Vợt Yonex Astrox");
        setupItemActions(btnEdit2, btnDelete2, "Giày Lining A3");
        setupItemActions(btnEdit3, btnDelete3, "Ống Cầu Lông");
        setupItemActions(btnEdit4, btnDelete4, "Dây Cước Cầu Lông");
    }

    // Hàm phụ để gắn sự kiện cho từng cặp nút Sửa/Xóa cho gọn code
    private void setupItemActions(ImageView btnEdit, ImageView btnDelete, String productName) {
        if (btnEdit != null) {
            btnEdit.setOnClickListener(v ->
                    Toast.makeText(this, "Sửa: " + productName, Toast.LENGTH_SHORT).show()
            );
        }

        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> {
                Toast.makeText(this, "Đã xóa: " + productName, Toast.LENGTH_SHORT).show();
                // Logic xóa UI: Ẩn CardView chứa nút này đi
                // (Lấy cha của nút delete -> ConstraintLayout -> CardView)
                try {
                    View parent = (View) v.getParent().getParent();
                    parent.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}