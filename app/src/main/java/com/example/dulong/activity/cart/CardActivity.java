package com.example.dulong.activity.cart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dulong.R;
import com.example.dulong.activity.utils.CartManager;
import com.example.dulong.adapter.CartAdapter;
import com.example.dulong.model.Product;
import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;
import java.util.List;

public class CardActivity extends AppCompatActivity {
    private MaterialButton btnCheckout;
    private ImageView btnBack;
    private RecyclerView rvCart;
    private TextView tvTotalAmount;
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        // 1. Ánh xạ View
        btnCheckout = findViewById(R.id.btnCheckout);
        btnBack = findViewById(R.id.btnBack);
        rvCart = findViewById(R.id.rvCart);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);

        // 2. Lấy dữ liệu từ CartManager
        List<Product> cartList = CartManager.getCartItems();

        // 3. Khởi tạo Adapter với Callback
        // Callback này sẽ được gọi mỗi khi user bấm + hoặc - trong Adapter
        adapter = new CartAdapter(cartList, this::updateTotalPrice);

        rvCart.setLayoutManager(new LinearLayoutManager(this));
        rvCart.setAdapter(adapter);

        // 4. Tính tổng tiền lần đầu khi mở màn hình
        updateTotalPrice();

        // 5. Xử lý nút Back
        btnBack.setOnClickListener(v -> finish());

        btnCheckout.setOnClickListener(v -> {
            List<Product> currentCartList = CartManager.getCartItems();
            if (currentCartList.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng đang trống!", Toast.LENGTH_SHORT).show();
            } else {
                // SỬA DÒNG NÀY: Chuyển sang CheckoutActivity thay vì OrderDetailActivity
                Intent intent = new Intent(this, CheckoutActivity.class);
                startActivity(intent);
            }
        });

        // Xử lý giao diện Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Hàm cập nhật text tổng tiền lên giao diện
    private void updateTotalPrice() {
        double total = CartManager.getTotalPrice();
        DecimalFormat formatter = new DecimalFormat("#,###");
        tvTotalAmount.setText(formatter.format(total) + "đ");
    }
}