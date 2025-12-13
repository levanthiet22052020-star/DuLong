package com.example.dulong.activity.product;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.dulong.R;
import com.example.dulong.activity.utils.CartManager;
import com.example.dulong.model.Product;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.DecimalFormat;

public class ProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // 1. Ánh xạ View
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        ImageView ivImage = findViewById(R.id.iv_product_image);
        TextView tvName = findViewById(R.id.tv_product_name);
        TextView tvPrice = findViewById(R.id.tv_product_price);
        TextView tvDescription = findViewById(R.id.tv_product_description);
        Button btnAddToCart = findViewById(R.id.btn_add_to_cart);

        // Thông số kỹ thuật
        TextView tvWeight = findViewById(R.id.tv_product_weight);
        TextView tvBalance = findViewById(R.id.tv_product_balance);
        TextView tvFlex = findViewById(R.id.tv_product_flex);
        TextView tvColor = findViewById(R.id.tv_product_color);
        TextView tvQuantity = findViewById(R.id.tv_product_quantity);

        toolbar.setNavigationOnClickListener(v -> finish());

        // 2. Nhận dữ liệu
        Product product = (Product) getIntent().getSerializableExtra("PRODUCT_DATA");

        if (product != null) {
            tvName.setText(product.getName());
            DecimalFormat formatter = new DecimalFormat("#,###");
            tvPrice.setText(formatter.format(product.getPrice()) + "đ");
            tvDescription.setText(product.getDescription() != null ? product.getDescription() : "Chưa có mô tả.");

            // Hiển thị thông số
            tvWeight.setText("Trọng lượng: " + (product.getWeight() != null ? product.getWeight() : "..."));
            tvBalance.setText("Điểm cân bằng: " + (product.getBalance() != null ? product.getBalance() : "..."));
            tvFlex.setText("Độ cứng: " + (product.getFlex() != null ? product.getFlex() : "..."));
            tvColor.setText("Màu sắc: " + (product.getColor() != null ? product.getColor() : "..."));

            // Kiểm tra số lượng tồn kho (giả sử > 0 là còn hàng)
            // Lưu ý: Trong CartManager ta dùng 'quantity' là số lượng mua
            // Còn ở đây product.quantity có thể hiểu là tồn kho nếu API trả về
            if (product.getQuantity() >= 0) {
                tvQuantity.setText("Tình trạng: Còn hàng");
                btnAddToCart.setEnabled(true);
            } else {
                tvQuantity.setText("Hết hàng");
                btnAddToCart.setEnabled(false);
                btnAddToCart.setText("Hết hàng");
            }

            Glide.with(this)
                    .load(product.getImage())
                    .placeholder(R.drawable.ic_logo_only)
                    .into(ivImage);

            // 3. Xử lý thêm vào giỏ
            btnAddToCart.setOnClickListener(v -> addToCart(product));
        } else {
            Toast.makeText(this, "Lỗi dữ liệu!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void addToCart(Product product) {
        CartManager.addToCart(product);
        Toast.makeText(this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
    }
}