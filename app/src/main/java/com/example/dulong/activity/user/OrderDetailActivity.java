package com.example.dulong.activity.user;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dulong.R;
import com.example.dulong.activity.HomeActivity;
import com.example.dulong.activity.utils.CartManager;
import com.example.dulong.adapter.OrderAdapter;
import com.example.dulong.model.Product;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

public class OrderDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // 1. Ánh xạ View
        Toolbar toolbar = findViewById(R.id.toolbarOrder);
        RecyclerView rvOrderProducts = findViewById(R.id.rvOrderProducts);
        TextView tvTotalPrice = findViewById(R.id.tvOrderTotalPrice);
        TextView tvOrderId = findViewById(R.id.tvOrderId);
        TextView btnCopyOrder = findViewById(R.id.btnCopyOrder);

        // Ánh xạ địa chỉ
        TextView tvNamePhone = findViewById(R.id.tvOrderNamePhone);
        TextView tvAddress = findViewById(R.id.tvOrderAddress);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        });

        // 2. Nhận dữ liệu từ CheckoutActivity
        String namePhone = getIntent().getStringExtra("order_name_phone");
        if (namePhone == null) namePhone = "Không có tên";
        
        String address = getIntent().getStringExtra("order_address");
        if (address == null) address = "Không có địa chỉ";
        
        double total = getIntent().getDoubleExtra("order_total", 0.0);

        // Hiển thị địa chỉ
        tvNamePhone.setText(namePhone);
        tvAddress.setText(address);

        // 3. Hiển thị danh sách sản phẩm
        boolean orderSuccess = getIntent().getBooleanExtra("order_success", false);
        List<Product> orderedItems;
        
        if (orderSuccess) {
            // Lấy danh sách sản phẩm từ intent
            orderedItems = (List<Product>) getIntent().getSerializableExtra("ordered_items");
            if (orderedItems == null) {
                orderedItems = new java.util.ArrayList<>();
            }
            
            // Hiển thị thông báo thành công
            String orderMessage = getIntent().getStringExtra("order_message");
            if (orderMessage != null) {
                Toast.makeText(this, orderMessage, Toast.LENGTH_LONG).show();
            }
        } else {
            orderedItems = CartManager.getCartItems();
        }
        
        OrderAdapter adapter = new OrderAdapter(orderedItems);
        rvOrderProducts.setLayoutManager(new LinearLayoutManager(this));
        rvOrderProducts.setAdapter(adapter);

        // 4. Hiển thị tổng tiền
        DecimalFormat formatter = new DecimalFormat("#,###");
        tvTotalPrice.setText(formatter.format(total) + "đ");

        // 5. Tạo mã đơn hàng (Nếu chưa có ID từ Intent thì tạo mới)
        String randomId = getIntent().getStringExtra("order_id");
        if (randomId == null) {
            randomId = "251209" + (new Random().nextInt(900000) + 100000);
        }
        tvOrderId.setText(randomId);

        // Sự kiện sao chép mã
        final String finalRandomId = randomId;
        btnCopyOrder.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Mã đơn hàng", finalRandomId);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Đã sao chép mã đơn hàng", Toast.LENGTH_SHORT).show();
        });
    }
}