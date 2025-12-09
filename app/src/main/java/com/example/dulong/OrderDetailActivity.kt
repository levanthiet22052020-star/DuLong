package com.example.dulong

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat
import kotlin.random.Random

class OrderDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        // 1. Ánh xạ View
        val toolbar: Toolbar = findViewById(R.id.toolbarOrder)
        val rvOrderProducts: RecyclerView = findViewById(R.id.rvOrderProducts)
        val tvTotalPrice: TextView = findViewById(R.id.tvOrderTotalPrice)
        val tvOrderId: TextView = findViewById(R.id.tvOrderId)
        val btnCancel: Button = findViewById(R.id.btnCancelOrder)

        // Setup Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false) // Tắt title mặc định để dùng TextView custom
        toolbar.setNavigationOnClickListener { finish() }

        // 2. Lấy dữ liệu sản phẩm (Giả sử lấy từ CartManager vừa mua)
        val orderedItems = CartManager.getCartItems()

        // Setup RecyclerView
        val adapter = OrderAdapter(orderedItems)
        rvOrderProducts.layoutManager = LinearLayoutManager(this)
        rvOrderProducts.adapter = adapter

        // 3. Tính tổng tiền và hiển thị
        val total = CartManager.getTotalPrice()
        val formatter = DecimalFormat("#,###")
        tvTotalPrice.text = "${formatter.format(total)}đ"

        // 4. Tạo mã đơn hàng ngẫu nhiên (Ví dụ: 251209...)
        val randomId = "251209${Random.nextInt(100000, 999999)}"
        tvOrderId.text = randomId

        // Copy mã đơn hàng
        tvOrderId.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = android.content.ClipData.newPlainText("Mã đơn hàng", randomId)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Đã sao chép mã đơn hàng", Toast.LENGTH_SHORT).show()
        }

        // 5. Xử lý nút Hủy đơn hàng
        btnCancel.setOnClickListener {
            Toast.makeText(this, "Đã gửi yêu cầu hủy đơn hàng", Toast.LENGTH_SHORT).show()
            // Có thể thêm logic quay về trang chủ ở đây
            finish()
        }
    }
}