package com.example.dulong

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