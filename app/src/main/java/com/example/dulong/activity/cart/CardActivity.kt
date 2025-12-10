package com.example.dulong.activity.cart

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
import com.google.android.material.button.MaterialButton
import java.text.DecimalFormat
import android.content.Intent
import com.example.dulong.activity.utils.CartManager
import com.example.dulong.activity.user.OrderDetailActivity
import com.example.dulong.R
import com.example.dulong.adapter.CartAdapter

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

        // 2. Thiết lập RecyclerView
        // Lấy danh sách từ CartManager
        val cartList = CartManager.getCartItems()

        adapter = CartAdapter(cartList)
        rvCart.layoutManager = LinearLayoutManager(this)
        rvCart.adapter = adapter

        // 3. Cập nhật tổng tiền
        updateTotalPrice()

        // 4. Xử lý nút Back
        btnBack.setOnClickListener {
            finish() // Đóng Activity để quay lại màn hình trước
        }

        // 5. Nút Mua hàng
        btnCheckout.setOnClickListener {
            if (cartList.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng đang trống!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show()
                // Xử lý logic đặt hàng ở đây (gọi API...)

                // Sau khi mua xong có thể xóa giỏ hàng
                // CartManager.clearCart()
                // adapter.notifyDataSetChanged()
                // updateTotalPrice()
            }
        }

        btnCheckout.setOnClickListener {
            val cartList = CartManager.getCartItems()
            if (cartList.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng đang trống!", Toast.LENGTH_SHORT).show()
            } else {
                // Chuyển sang màn hình Thông tin đơn hàng
                val intent = Intent(this, OrderDetailActivity::class.java)
                startActivity(intent)

                // Tùy chọn: Xóa giỏ hàng sau khi đặt thành công (nếu muốn)
                // CartManager.clearCart()
                // finish() // Đóng màn hình giỏ hàng lại
            }
        }

        // Xử lý giao diện tràn viền
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun updateTotalPrice() {
        val total = CartManager.getTotalPrice()
        val formatter = DecimalFormat("#,###")
        tvTotalAmount.text = "${formatter.format(total)}đ"
    }
}