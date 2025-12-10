package com.example.dulong.activity.user

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dulong.R
import com.example.dulong.activity.utils.CartManager
import com.example.dulong.adapter.OrderAdapter // Bạn có thể dùng CartAdapter hoặc OrderAdapter nếu giống nhau
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

        // View hiển thị địa chỉ (Thêm id vào layout activity_order_detail.xml nếu chưa có)
        // Ví dụ trong XML bạn cần đặt id cho TextView tên+sđt và TextView địa chỉ
        val tvNamePhone: TextView = findViewById(R.id.tvOrderNamePhone) // Cần thêm ID trong XML
        val tvAddress: TextView = findViewById(R.id.tvOrderAddress)     // Cần thêm ID trong XML

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { finish() }

        // 2. Nhận dữ liệu từ CheckoutActivity
        val namePhone = intent.getStringExtra("order_name_phone") ?: "Không có tên"
        val address = intent.getStringExtra("order_address") ?: "Không có địa chỉ"
        val total = intent.getDoubleExtra("order_total", 0.0)

        // Hiển thị địa chỉ
        tvNamePhone.text = namePhone
        tvAddress.text = address

        // 3. Hiển thị danh sách sản phẩm
        val orderedItems = CartManager.getCartItems()
        val adapter = OrderAdapter(orderedItems) // Hoặc dùng CartAdapter nếu OrderAdapter chưa có
        rvOrderProducts.layoutManager = LinearLayoutManager(this)
        rvOrderProducts.adapter = adapter

        // 4. Hiển thị tổng tiền
        val formatter = DecimalFormat("#,###")
        tvTotalPrice.text = "${formatter.format(total)}đ"

        // 5. Tạo mã đơn hàng
        val randomId = "251209${Random.Default.nextInt(100000, 999999)}"
        tvOrderId.text = randomId

        tvOrderId.setOnClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Mã đơn hàng", randomId)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Đã sao chép mã đơn hàng", Toast.LENGTH_SHORT).show()
        }

        // 6. Xử lý nút Hủy đơn hàng
        btnCancel.setOnClickListener {
            Toast.makeText(this, "Đã hủy đơn hàng thành công", Toast.LENGTH_SHORT).show()
            // Clear giỏ hàng nếu cần
            // CartManager.clearCart()
            finish()
        }
    }
}