package com.example.dulong.activity.cart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dulong.R;
import com.example.dulong.activity.user.AddressListActivity;
import com.example.dulong.activity.user.OrderDetailActivity;
import com.example.dulong.activity.utils.CartManager;
import com.example.dulong.adapter.CartAdapter;
import com.example.dulong.api.RetrofitClient;
import com.example.dulong.model.AddressListResponse;
import com.example.dulong.model.AddressModel;
import com.example.dulong.model.GeneralResponse;
import com.example.dulong.model.Product;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {

    private TextView tvReceiverNamePhone;
    private TextView tvReceiverAddress;
    private TextView tvSubTotal;
    private TextView tvFinalTotal;
    private TextView tvBottomTotal;
    private RecyclerView rvCheckoutItems;
    private Button btnPlaceOrder;
    private LinearLayout btnSelectAddress;
    private SharedPreferences sharedPreferences;

    // Biến lưu địa chỉ đã chọn
    private String selectedAddressString = "";
    private String selectedNamePhone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        initViews();

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarCheckout);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // 1. Hiển thị danh sách sản phẩm
        List<Product> cartItems = CartManager.getCartItems();

        // --- SỬA LỖI Ở ĐÂY ---
        // Truyền thêm lambda { updateTotalPrice() } vào Adapter
        CartAdapter adapter = new CartAdapter(cartItems, this::updateTotalPrice);

        rvCheckoutItems.setLayoutManager(new LinearLayoutManager(this));
        rvCheckoutItems.setAdapter(adapter);

        // 2. Tính toán tiền lần đầu
        updateTotalPrice();

        // 3. Lấy địa chỉ mặc định
        loadDefaultAddress();

        // 4. Sự kiện chọn địa chỉ
        btnSelectAddress.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddressListActivity.class);
            intent.putExtra("isSelectMode", true);
            startActivityForResult(intent, 100);
        });

        // 5. SỰ KIỆN ĐẶT HÀNG
        btnPlaceOrder.setOnClickListener(v -> {
            if (selectedAddressString.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn địa chỉ nhận hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            
            processCheckout();
        });
    }

    private void initViews() {
        tvReceiverNamePhone = findViewById(R.id.tvReceiverNamePhone);
        tvReceiverAddress = findViewById(R.id.tvReceiverAddress);
        tvSubTotal = findViewById(R.id.tvSubTotal);
        tvFinalTotal = findViewById(R.id.tvFinalTotal);
        tvBottomTotal = findViewById(R.id.tvBottomTotal);
        rvCheckoutItems = findViewById(R.id.rvCheckoutItems);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        btnSelectAddress = findViewById(R.id.btnSelectAddress);
        sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
    }

    // Hàm cập nhật tổng tiền lên giao diện
    private void updateTotalPrice() {
        double total = CartManager.getTotalPrice();
        DecimalFormat formatter = new DecimalFormat("#,###");
        String totalString = formatter.format(total) + "đ";

        tvSubTotal.setText(totalString);
        tvFinalTotal.setText(totalString);
        tvBottomTotal.setText(totalString);
    }

    private void loadDefaultAddress() {
        String userId = sharedPreferences.getString("USER_ID", "");
        if (userId == null || userId.isEmpty()) return;

        RetrofitClient.getInstance().getUserAddresses(userId).enqueue(new Callback<AddressListResponse>() {
            @Override
            public void onResponse(Call<AddressListResponse> call, Response<AddressListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<AddressModel> list = response.body().getData();
                    AddressModel defaultAddr = null;
                    
                    // Tìm địa chỉ mặc định
                    for (AddressModel addr : list) {
                        if (addr.isDefault()) {
                            defaultAddr = addr;
                            break;
                        }
                    }
                    
                    // Nếu không có mặc định thì lấy cái đầu tiên
                    if (defaultAddr == null && !list.isEmpty()) {
                        defaultAddr = list.get(0);
                    }

                    if (defaultAddr != null) {
                        selectedNamePhone = defaultAddr.getName() + " (" + defaultAddr.getPhone() + ")";
                        selectedAddressString = defaultAddr.getAddress();
                        tvReceiverNamePhone.setText(selectedNamePhone);
                        tvReceiverAddress.setText(selectedAddressString);
                    }
                }
            }

            @Override
            public void onFailure(Call<AddressListResponse> call, Throwable t) {
                // Handle failure
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            String name = data.getStringExtra("name");
            String phone = data.getStringExtra("phone");
            String addr = data.getStringExtra("address");

            selectedNamePhone = name + " (" + phone + ")";
            selectedAddressString = addr != null ? addr : "";

            tvReceiverNamePhone.setText(selectedNamePhone);
            tvReceiverAddress.setText(selectedAddressString);
        }
    }

    private void processCheckout() {
        String userId = sharedPreferences.getString("USER_ID", "");
        if (userId.isEmpty()) {
            Toast.makeText(this, "Chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable button để tránh click nhiều lần
        btnPlaceOrder.setEnabled(false);
        btnPlaceOrder.setText("Đang xử lý...");

        // Chuẩn bị dữ liệu đơn hàng
        List<Product> cartItems = CartManager.getCartItems();
        List<Map<String, Object>> items = new ArrayList<>();
        
        for (Product product : cartItems) {
            Map<String, Object> item = new HashMap<>();
            item.put("productId", product.get_id());
            item.put("name", product.getName());
            item.put("price", product.getPrice());
            item.put("quantity", product.getQuantity());
            item.put("image", product.getImage());
            items.add(item);
        }

        // Tính tổng tiền
        double totalAmount = CartManager.getTotalPrice();
        DecimalFormat formatter = new DecimalFormat("#,###");
        String totalPriceString = formatter.format(totalAmount) + "đ";

        // Chuẩn bị địa chỉ giao hàng
        Map<String, String> shippingAddress = new HashMap<>();
        String[] nameParts = selectedNamePhone.split(" \\(");
        String fullName = nameParts.length > 0 ? nameParts[0] : "Khách hàng";
        String phone = nameParts.length > 1 ? nameParts[1].replace(")", "") : "";
        
        shippingAddress.put("fullName", fullName);
        shippingAddress.put("phone", phone);
        shippingAddress.put("address", selectedAddressString);
        shippingAddress.put("ward", "");
        shippingAddress.put("district", "");
        shippingAddress.put("province", "");

        // Tạo request body
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("userId", userId);
        orderData.put("items", items);
        orderData.put("totalPrice", totalPriceString);
        orderData.put("shippingAddress", shippingAddress);
        orderData.put("paymentMethod", "COD");

        // Gọi API checkout
        RetrofitClient.getInstance().checkout(orderData).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                btnPlaceOrder.setEnabled(true);
                btnPlaceOrder.setText("Đặt Hàng");

                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    Toast.makeText(CheckoutActivity.this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                    
                    // Lưu thông tin sản phẩm trước khi xóa giỏ hàng
                    List<Product> orderedItems = new ArrayList<>(CartManager.getCartItems());
                    
                    // Xóa giỏ hàng
                    CartManager.clearCart();
                    
                    // Chuyển đến OrderDetailActivity với thông tin đơn hàng
                    Intent intent = new Intent(CheckoutActivity.this, OrderDetailActivity.class);
                    intent.putExtra("order_name_phone", selectedNamePhone);
                    intent.putExtra("order_address", selectedAddressString);
                    intent.putExtra("order_total", totalAmount);
                    intent.putExtra("order_success", true);
                    intent.putExtra("order_message", response.body().getMessage());
                    
                    // Truyền danh sách sản phẩm (serialize)
                    intent.putExtra("ordered_items", new ArrayList<>(orderedItems));
                    
                    startActivity(intent);
                    finish();
                    
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Đặt hàng thất bại";
                    Toast.makeText(CheckoutActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                btnPlaceOrder.setEnabled(true);
                btnPlaceOrder.setText("Đặt Hàng");
                Toast.makeText(CheckoutActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}