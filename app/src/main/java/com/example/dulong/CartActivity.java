package com.example.dulong; // LƯU Ý: Hãy đổi thành tên package thực tế của dự án bạn (vd: com.example.badmintonshop)

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    // Danh sách để quản lý các sản phẩm trong giỏ
    private List<CartItem> cartItems = new ArrayList<>();

    // Các biến giao diện chung
    private TextView tvTotalAmount;
    private Button btnCheckout;
    private ImageView btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Đảm bảo file layout của bạn tên là 'activity_cart.xml'
        setContentView(R.layout.activity_cart);

        // 1. Ánh xạ các thành phần giao diện chung
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnMenu = findViewById(R.id.btnMenu); // Nút menu ở góc trái trên (nếu có ID là btnMenu)
        // Nếu trong XML bạn đặt là adminBtnMenu thì sửa lại ở đây nhé, nhưng trong file XML tôi gửi trước đó là btnMenu.

        // 2. Thiết lập dữ liệu cho 5 sản phẩm cứng trong XML
        // Cú pháp: setupCartItem(ID_Sản_phẩm, ID_RadioBtn, ID_Nút_Trừ, ID_Text_Số_lượng, ID_Nút_Cộng, Giá_Tiền)

        // Sản phẩm 1: Vợt Yonex 100ZZ - 3.500.000đ
        setupCartItem(1, R.id.rb1, R.id.btnMinus1, R.id.tvQty1, R.id.btnPlus1, 3500000);

        // Sản phẩm 2: Giày Lining - 1.200.000đ
        setupCartItem(2, R.id.rb2, R.id.btnMinus2, R.id.tvQty2, R.id.btnPlus2, 1200000);

        // Sản phẩm 3: Ống Cầu VinaStar - 180.000đ
        setupCartItem(3, R.id.rb3, R.id.btnMinus3, R.id.tvQty3, R.id.btnPlus3, 180000);

        // Sản phẩm 4: Bao Vợt Kawasaki - 750.000đ
        setupCartItem(4, R.id.rb4, R.id.btnMinus4, R.id.tvQty4, R.id.btnPlus4, 750000);

        // Sản phẩm 5: Quấn Cán (Hộp) - 120.000đ
        setupCartItem(5, R.id.rb5, R.id.btnMinus5, R.id.tvQty5, R.id.btnPlus5, 120000);

        // 3. Xử lý sự kiện nút Menu
        if (btnMenu != null) {
            btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CartActivity.this, "Bạn đã bấm Menu", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // 4. Xử lý sự kiện nút Thanh toán
        if (btnCheckout != null) {
            btnCheckout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CartActivity.this, "Đang xử lý thanh toán: " + tvTotalAmount.getText(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        // 5. Tính tổng tiền lần đầu tiên khi vừa mở màn hình
        calculateTotal();
    }

    /**
     * Hàm hỗ trợ thiết lập logic tăng/giảm/chọn cho từng dòng sản phẩm
     */
    private void setupCartItem(int id, int rbId, int btnMinusId, int tvQtyId, int btnPlusId, long price) {
        // Tìm các view theo ID truyền vào
        RadioButton radioButton = findViewById(rbId);
        ImageView btnMinus = findViewById(btnMinusId);
        TextView tvQuantity = findViewById(tvQtyId);
        ImageView btnPlus = findViewById(btnPlusId);

        // Lấy số lượng ban đầu đang ghi trong file XML (ví dụ text="1" hoặc "5")
        int currentQty = 1;
        try {
            currentQty = Integer.parseInt(tvQuantity.getText().toString());
        } catch (NumberFormatException e) {
            currentQty = 1; // Mặc định là 1 nếu lỗi
        }

        // Tạo đối tượng CartItem để lưu trữ thông tin sản phẩm này
        CartItem item = new CartItem(id, price, currentQty, radioButton);
        cartItems.add(item);

        // --- Sự kiện 1: Chọn sản phẩm (RadioButton) ---
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khi bấm chọn/bỏ chọn -> tính lại tổng tiền
                calculateTotal();
            }
        });

        // --- Sự kiện 2: Nút TRỪ (-) ---
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.quantity > 1) {
                    item.quantity--; // Giảm số lượng đi 1
                    tvQuantity.setText(String.valueOf(item.quantity)); // Cập nhật số lên màn hình
                    calculateTotal(); // Tính lại tổng tiền
                } else {
                    Toast.makeText(CartActivity.this, "Số lượng tối thiểu là 1", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // --- Sự kiện 3: Nút CỘNG (+) ---
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.quantity++; // Tăng số lượng lên 1
                tvQuantity.setText(String.valueOf(item.quantity)); // Cập nhật số lên màn hình
                calculateTotal(); // Tính lại tổng tiền
            }
        });
    }

    /**
     * Hàm tính tổng tiền của tất cả sản phẩm ĐANG ĐƯỢC CHỌN
     */
    private void calculateTotal() {
        long total = 0;
        for (CartItem item : cartItems) {
            // Chỉ cộng tiền nếu RadioButton của sản phẩm đó đang được tích chọn
            if (item.radioButton.isChecked()) {
                total += (item.price * item.quantity);
            }
        }

        // Định dạng tiền tệ kiểu Việt Nam (VD: 5.000.000đ)
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        String formattedTotal = formatter.format(total) + "đ";

        // Nếu tổng tiền là 0 thì hiển thị 0đ
        if (total == 0) formattedTotal = "0đ";

        // Hiển thị lên TextView Tổng cộng
        tvTotalAmount.setText(formattedTotal);
    }

    /**
     * Class nội bộ (Model) dùng để lưu trạng thái của từng sản phẩm trong danh sách
     */
    private static class CartItem {
        int id;
        long price;
        int quantity;
        RadioButton radioButton;

        public CartItem(int id, long price, int quantity, RadioButton radioButton) {
            this.id = id;
            this.price = price;
            this.quantity = quantity;
            this.radioButton = radioButton;
        }
    }
}