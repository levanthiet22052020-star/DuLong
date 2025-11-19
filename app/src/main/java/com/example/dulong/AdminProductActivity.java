package com.example.dulong; // Thay bằng tên package của bạn (ví dụ: com.example.badmintonshop)

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminProductActivity extends AppCompatActivity {

    // 1. Khai báo biến giao diện
    private ImageView btnMenu, btnSearch;
    private EditText edtSearch;
    private Button btnAddProduct;

    // Khai báo demo các nút của Sản phẩm số 1 (Để test logic Sửa/Xóa)
    private ImageView btnEdit1, btnDelete1;

    // Container chứa danh sách (sau này dùng để thêm view động nếu cần)
    private LinearLayout productListContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // LƯU Ý QUAN TRỌNG: Tên trong R.layout.phải_trùng_tên_file_xml_của_bạn
        setContentView(R.layout.admin_product);

        // 2. Ánh xạ ID từ file XML sang Java
        initViews();

        // 3. Gắn sự kiện bấm nút
        setupListeners();
    }

    private void initViews() {
        // Header
        btnMenu = findViewById(R.id.adminBtnMenu);
        btnSearch = findViewById(R.id.adminBtnSearch);
        edtSearch = findViewById(R.id.adminSearchBar);

        // Nút chức năng chính
        btnAddProduct = findViewById(R.id.adminBtnAddProduct);
        productListContainer = findViewById(R.id.adminProductListContainer);

        // Các nút thao tác trên sản phẩm 1 (Demo)
        btnEdit1 = findViewById(R.id.adminBtnEdit1);
        btnDelete1 = findViewById(R.id.adminBtnDelete1);
    }

    private void setupListeners() {
        // --- Xử lý nút MENU ---
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiện tại chưa có layout menu dropdown nên tạm thời Toast thông báo
                Toast.makeText(AdminProductActivity.this, "Bạn vừa bấm Menu!", Toast.LENGTH_SHORT).show();
            }
        });

        // --- Xử lý nút THÊM SẢN PHẨM ---
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminProductActivity.this, "Chuyển sang màn hình Thêm sản phẩm", Toast.LENGTH_SHORT).show();
                // Sau này dùng: startActivity(new Intent(AdminProductActivity.this, AddProductActivity.class));
            }
        });

        // --- Xử lý nút TÌM KIẾM ---
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = edtSearch.getText().toString().trim();
                if (keyword.isEmpty()) {
                    Toast.makeText(AdminProductActivity.this, "Vui lòng nhập từ khóa!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminProductActivity.this, "Đang tìm kiếm: " + keyword, Toast.LENGTH_SHORT).show();
                    // Logic tìm kiếm database sẽ viết ở đây
                }
            }
        });

        // --- Xử lý SỬA sản phẩm 1 ---
        if (btnEdit1 != null) {
            btnEdit1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(AdminProductActivity.this, "Sửa sản phẩm: Vợt Yonex Astrox", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // --- Xử lý XÓA sản phẩm 1 ---
        if (btnDelete1 != null) {
            btnDelete1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(AdminProductActivity.this, "Đã xóa: Vợt Yonex Astrox", Toast.LENGTH_SHORT).show();

                    // Demo: Ẩn dòng sản phẩm này đi (lấy cha của nút delete là ConstraintLayout, cha của ConstraintLayout là CardView)
                    View cardView = (View) v.getParent().getParent();
                    cardView.setVisibility(View.GONE);
                }
            });
        }
    }
}