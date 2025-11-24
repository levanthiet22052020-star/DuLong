package com.example.dulong

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar

class ProductDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        val productImage: ImageView = findViewById(R.id.iv_product_image)
        val productName: TextView = findViewById(R.id.tv_product_name)
        val productPrice: TextView = findViewById(R.id.tv_product_price)
        val productDescription: TextView = findViewById(R.id.tv_product_description)
        val addToCartButton: Button = findViewById(R.id.btn_add_to_cart)

        // Bắt sự kiện click nút back trên toolbar
        toolbar.setNavigationOnClickListener {
            finish() // Đóng activity hiện tại để quay về màn hình trước
        }

        // Lấy dữ liệu được gửi từ adapter
        val name = intent.getStringExtra("PRODUCT_NAME")
        val price = intent.getStringExtra("PRODUCT_PRICE")
        val imageUrl = intent.getStringExtra("PRODUCT_IMAGE_URL")
        val description = intent.getStringExtra("PRODUCT_DESCRIPTION")

        // Hiển thị dữ liệu lên view
        productName.text = name
        productPrice.text = price
        productDescription.text = description

        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_logo_only) // Ảnh hiển thị trong lúc tải
            .error(R.drawable.ic_logo_only)       // Ảnh hiển thị nếu có lỗi
            .into(productImage)

        // Xử lý sự kiện click nút "Thêm vào giỏ hàng"
        addToCartButton.setOnClickListener {
            Toast.makeText(this, "Đã thêm '" + name + "' vào giỏ hàng", Toast.LENGTH_SHORT).show()
            // Tại đây, bạn có thể thêm logic xử lý giỏ hàng phức tạp hơn
        }
    }
}