package com.example.dulong

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AdminMenuDropActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_menu_drop)

        // Xử lý tràn viền
        val rootView = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Ánh xạ View (Liên kết với XML)
        val btnMenu = findViewById<ImageView>(R.id.adminBtnMenu)
        val btnSearch = findViewById<ImageView>(R.id.adminBtnSearch)
        val edtSearch = findViewById<EditText>(R.id.adminSearchBar)
        val menuDropdown = findViewById<LinearLayout>(R.id.adminMenuDropdown)
        val btnAddProduct = findViewById<Button>(R.id.adminBtnAddProduct)

        // 2. Gắn sự kiện click (Chỉ giữ lại các chức năng khác)

        // Nút Menu (Chỉ thông báo chơi, không làm gì cả)
        btnMenu.setOnClickListener {
            Toast.makeText(this, "Menu đang hiển thị", Toast.LENGTH_SHORT).show()
        }

        // Nút Tìm kiếm
        btnSearch.setOnClickListener {
            val query = edtSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                Toast.makeText(this, "Đang tìm: $query", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Vui lòng nhập từ khóa", Toast.LENGTH_SHORT).show()
            }
        }

        // Nút Thêm sản phẩm
        btnAddProduct.setOnClickListener {
            Toast.makeText(this, "Mở màn hình thêm sản phẩm", Toast.LENGTH_SHORT).show()
        }

        // Xử lý các nút Sửa/Xóa (Gọi hàm phụ)
        setupItemActions(findViewById(R.id.adminBtnEdit1), findViewById(R.id.adminBtnDelete1), "Vợt Yonex Astrox")
        setupItemActions(findViewById(R.id.adminBtnEdit2), findViewById(R.id.adminBtnDelete2), "Giày Lining A3")
        setupItemActions(findViewById(R.id.adminBtnEdit3), findViewById(R.id.adminBtnDelete3), "Ống Cầu Lông")
        setupItemActions(findViewById(R.id.adminBtnEdit4), findViewById(R.id.adminBtnDelete4), "Dây Cước Cầu Lông")
    }

    // Hàm phụ xử lý Sửa/Xóa
    private fun setupItemActions(btnEdit: ImageView?, btnDelete: ImageView?, productName: String) {
        btnEdit?.setOnClickListener {
            Toast.makeText(this, "Sửa: $productName", Toast.LENGTH_SHORT).show()
        }

        btnDelete?.setOnClickListener { view ->
            Toast.makeText(this, "Đã xóa: $productName", Toast.LENGTH_SHORT).show()
            try {
                val parentLayout = view.parent as? View
                val cardView = parentLayout?.parent as? View
                cardView?.visibility = View.GONE
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}