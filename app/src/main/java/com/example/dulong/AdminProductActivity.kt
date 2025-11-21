package com.example.dulong

import android.os.Bundle
import android.view.View // Import để dùng View.VISIBLE và View.GONE
import android.widget.ImageView // Import ImageView
import android.widget.LinearLayout // Import LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AdminProductActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Đảm bảo tên file layout trùng với file XML bạn đã sửa: activity_admin_product
        setContentView(R.layout.activity_admin_product)

        // --- 1. XỬ LÝ TRÀN VIỀN (EDGE TO EDGE) ---
        // LƯU Ý: Trong file XML activity_admin_product.xml, thẻ gốc (ConstraintLayout)
        // PHẢI có thuộc tính android:id="@+id/main" thì dòng dưới mới chạy được.
        val mainView = findViewById<View>(R.id.main)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        // --- 2. XỬ LÝ LOGIC MENU DROPDOWN ---

        // Ánh xạ (Tìm) các view từ XML
        val adminBtnMenu = findViewById<ImageView>(R.id.adminBtnMenu)
        val adminMenuDropdown = findViewById<LinearLayout>(R.id.adminMenuDropdown)

        // Bắt sự kiện click vào nút Menu (3 gạch)
        adminBtnMenu.setOnClickListener {
            // Kiểm tra trạng thái hiện tại của menu
            if (adminMenuDropdown.visibility == View.VISIBLE) {
                // Nếu đang hiện -> Ẩn đi
                adminMenuDropdown.visibility = View.GONE
            } else {
                // Nếu đang ẩn -> Hiện lên
                adminMenuDropdown.visibility = View.VISIBLE
                // Đưa menu lên lớp trên cùng để đè lên danh sách sản phẩm
                adminMenuDropdown.bringToFront()
            }
        }

        // (Tùy chọn) Khi bấm vào chính bảng menu thì đóng lại cho gọn
        adminMenuDropdown.setOnClickListener {
            adminMenuDropdown.visibility = View.GONE
        }
    }
}