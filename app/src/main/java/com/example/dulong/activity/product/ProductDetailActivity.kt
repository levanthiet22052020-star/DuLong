package com.example.dulong.activity.product

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dulong.activity.cart.CardActivity // Hoặc CardActivity tùy tên file của bạn
import com.example.dulong.activity.utils.CartManager
import com.example.dulong.R
import com.example.dulong.model.Product
import com.google.android.material.appbar.MaterialToolbar
import java.text.DecimalFormat

class ProductDetailActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        // 1. Ánh xạ View
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        val ivImage: ImageView = findViewById(R.id.iv_product_image)
        val tvName: TextView = findViewById(R.id.tv_product_name)
        val tvPrice: TextView = findViewById(R.id.tv_product_price)
        val tvDescription: TextView = findViewById(R.id.tv_product_description)
        val btnAddToCart: Button = findViewById(R.id.btn_add_to_cart)

        // Thông số kỹ thuật
        val tvWeight: TextView = findViewById(R.id.tv_product_weight)
        val tvBalance: TextView = findViewById(R.id.tv_product_balance)
        val tvFlex: TextView = findViewById(R.id.tv_product_flex)
        val tvColor: TextView = findViewById(R.id.tv_product_color)
        val tvQuantity: TextView = findViewById(R.id.tv_product_quantity)

        toolbar.setNavigationOnClickListener { finish() }

        // 2. Nhận dữ liệu
        val product = intent.getSerializableExtra("PRODUCT_DATA") as? Product

        if (product != null) {
            tvName.text = product.name
            val formatter = DecimalFormat("#,###")
            tvPrice.text = "${formatter.format(product.price)}đ"
            tvDescription.text = product.description ?: "Chưa có mô tả."

            // Hiển thị thông số
            tvWeight.text = "Trọng lượng: ${product.weight ?: "..."}"
            tvBalance.text = "Điểm cân bằng: ${product.balance ?: "..."}"
            tvFlex.text = "Độ cứng: ${product.flex ?: "..."}"
            tvColor.text = "Màu sắc: ${product.color ?: "..."}"

            // Kiểm tra số lượng tồn kho (giả sử > 0 là còn hàng)
            // Lưu ý: Trong CartManager ta dùng 'quantity' là số lượng mua
            // Còn ở đây product.quantity có thể hiểu là tồn kho nếu API trả về
            if (product.quantity >= 0) {
                tvQuantity.text = "Tình trạng: Còn hàng"
                btnAddToCart.isEnabled = true
            } else {
                tvQuantity.text = "Hết hàng"
                btnAddToCart.isEnabled = false
                btnAddToCart.text = "Hết hàng"
            }

            Glide.with(this)
                .load(product.image)
                .placeholder(R.drawable.ic_logo_only)
                .into(ivImage)

            // 3. Xử lý thêm vào giỏ
            btnAddToCart.setOnClickListener {
                addToCart(product)
            }
        } else {
            Toast.makeText(this, "Lỗi dữ liệu!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun addToCart(product: Product) {
        CartManager.addToCart(product)
        Toast.makeText(this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show()
    }
}