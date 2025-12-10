package com.example.dulong.activity.cart

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dulong.R
import com.example.dulong.activity.utils.CartManager
import com.example.dulong.adapter.CartAdapter
import com.google.android.material.button.MaterialButton
import java.text.DecimalFormat

class CardActivity : AppCompatActivity() {
    private lateinit var btnCheckout: MaterialButton
    private lateinit var btnBack: ImageView
    private lateinit var rvCart: RecyclerView
    private lateinit var tvTotalAmount: TextView
    private lateinit var adapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_card)

        // 1. Ánh xạ View
        btnCheckout = findViewById(R.id.btnCheckout)
        btnBack = findViewById(R.id.btnBack)
        rvCart = findViewById(R.id.rvCart)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)

        // 2. Lấy dữ liệu từ CartManager
        val cartList = CartManager.getCartItems()

        // 3. Khởi tạo Adapter với Callback
        // Callback này sẽ được gọi mỗi khi user bấm + hoặc - trong Adapter
        adapter = CartAdapter(cartList) {
            // Khi số lượng thay đổi -> Tính lại tổng tiền và hiển thị
            updateTotalPrice()
        }

        rvCart.layoutManager = LinearLayoutManager(this)
        rvCart.adapter = adapter

        // 4. Tính tổng tiền lần đầu khi mở màn hình
        updateTotalPrice()

        // 5. Xử lý nút Back
        btnBack.setOnClickListener {
            finish()
        }

        // 6. Xử lý nút MUA HÀNG
        btnCheckout.setOnClickListener {
            if (cartList.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng đang trống!", Toast.LENGTH_SHORT).show()
            } else {
                // Chuyển sang màn hình Thanh toán (CheckoutActivity)
                val intent = Intent(this, CheckoutActivity::class.java)
                startActivity(intent)
            }
        }

        // Xử lý giao diện Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Hàm cập nhật text tổng tiền lên giao diện
    private fun updateTotalPrice() {
        val total = CartManager.getTotalPrice()
        val formatter = DecimalFormat("#,###")
        tvTotalAmount.text = "${formatter.format(total)}đ"
    }
}