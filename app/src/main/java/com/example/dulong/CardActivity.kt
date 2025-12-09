package com.example.dulong

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.NumberFormat
import java.util.Locale

class CardActivity : AppCompatActivity() {

    // 1. Khai báo các View chung (Tổng tiền, Nút mua, Điều hướng)
    private lateinit var btnCheckout: Button
    private lateinit var tvTotalAmount: TextView
    private lateinit var btnBack: ImageView
    private lateinit var navHome: ImageView
    private lateinit var navNotification: ImageView

    // 2. Class dữ liệu để quản lý từng dòng sản phẩm cho gọn code
    // Giúp không phải viết lặp lại code cho 5 sản phẩm
    data class ProductRow(
        val checkBox: CheckBox,
        val btnMinus: ImageView,
        val btnPlus: ImageView,
        val tvQty: TextView,
        val price: Long,    // Giá của 1 sản phẩm
        var quantity: Int   // Số lượng hiện tại
    )

    // Danh sách chứa 5 sản phẩm đang hiển thị trên màn hình
    private val productList = mutableListOf<ProductRow>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_card)

        // Xử lý giao diện Edge-to-Edge (Tràn viền)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 3. Ánh xạ các View chung
        initGlobalViews()

        // 4. Thiết lập dữ liệu cho 5 sản phẩm (Ánh xạ ID từ XML vào List)
        setupProductList()

        // 5. Gán sự kiện click (Cộng, Trừ, Check) cho từng món hàng
        setupProductEvents()

        // 6. Xử lý sự kiện điều hướng (Back, Home, Checkout)
        setupNavigationEvents()

        // 7. Tính tổng tiền lần đầu khi vừa mở màn hình
        calculateTotal()
    }

    private fun initGlobalViews() {
        btnCheckout = findViewById(R.id.btnCheckout)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        btnBack = findViewById(R.id.btnBack)

        // Đảm bảo trong XML bạn đã thêm ID nav_home và nav_notification như hướng dẫn trước
        navHome = findViewById(R.id.nav_home)
        navNotification = findViewById(R.id.nav_notification)
    }

    private fun setupProductList() {
        try {
            // --- SẢN PHẨM 1: Vợt Yonex 100ZZ (3.500.000đ) ---
            productList.add(ProductRow(
                findViewById(R.id.cb1),       // CheckBox
                findViewById(R.id.btnMinus1), // Nút Trừ
                findViewById(R.id.btnPlus1),  // Nút Cộng
                findViewById(R.id.tvQty1),    // Số lượng
                3500000L,                     // Giá tiền gốc
                1                             // Số lượng mặc định
            ))

            // --- SẢN PHẨM 2: Giày Lining (1.200.000đ) ---
            productList.add(ProductRow(
                findViewById(R.id.cb2),
                findViewById(R.id.btnMinus2),
                findViewById(R.id.btnPlus2),
                findViewById(R.id.tvQty2),
                1200000L,
                1
            ))

            // --- SẢN PHẨM 3: Ống Cầu VinaStar (180.000đ) - Số lượng gốc là 5 ---
            productList.add(ProductRow(
                findViewById(R.id.cb3),
                findViewById(R.id.btnMinus3),
                findViewById(R.id.btnPlus3),
                findViewById(R.id.tvQty3),
                180000L,
                5
            ))

            // --- SẢN PHẨM 4: Bao Vợt Kawasaki (750.000đ) ---
            productList.add(ProductRow(
                findViewById(R.id.cb4),
                findViewById(R.id.btnMinus4),
                findViewById(R.id.btnPlus4),
                findViewById(R.id.tvQty4),
                750000L,
                1
            ))

            // --- SẢN PHẨM 5: Quấn Cán (120.000đ) - Số lượng gốc là 3 ---
            productList.add(ProductRow(
                findViewById(R.id.cb5),
                findViewById(R.id.btnMinus5),
                findViewById(R.id.btnPlus5),
                findViewById(R.id.tvQty5),
                120000L,
                3
            ))

        } catch (e: Exception) {
            // Phòng trường hợp bạn quên sửa ID trong XML thì App không bị Crash
            e.printStackTrace()
            Toast.makeText(this, "Lỗi ánh xạ View: Vui lòng kiểm tra lại ID trong XML", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupProductEvents() {
        // Vòng lặp duyệt qua từng sản phẩm trong danh sách
        for (item in productList) {

            // 1. Sự kiện nút CỘNG (+)
            item.btnPlus.setOnClickListener {
                item.quantity++
                item.tvQty.text = item.quantity.toString() // Cập nhật số hiển thị
                calculateTotal() // Tính lại tổng tiền
            }

            // 2. Sự kiện nút TRỪ (-)
            item.btnMinus.setOnClickListener {
                if (item.quantity > 1) {
                    item.quantity--
                    item.tvQty.text = item.quantity.toString()
                    calculateTotal()
                } else {
                    Toast.makeText(this, "Số lượng tối thiểu là 1", Toast.LENGTH_SHORT).show()
                }
            }

            // 3. Sự kiện Checkbox (Chọn/Bỏ chọn món hàng)
            item.checkBox.setOnCheckedChangeListener { _, _ ->
                calculateTotal() // Chỉ cần tính lại tiền, không cần làm gì thêm
            }
        }
    }

    // Hàm tính tổng tiền dựa trên các món được Check
    private fun calculateTotal() {
        var totalMoney: Long = 0

        for (item in productList) {
            if (item.checkBox.isChecked) {
                totalMoney += (item.price * item.quantity)
            }
        }

        // Định dạng tiền Việt Nam (Ví dụ: 5.750.000đ)
        val localeVN = Locale("vi", "VN")
        val numberFormat = NumberFormat.getCurrencyInstance(localeVN)
        val strMoney = numberFormat.format(totalMoney) // Kết quả tự động có chữ "đ" hoặc "₫"

        tvTotalAmount.text = strMoney
    }

    private fun setupNavigationEvents() {
        // 1. Mũi tên Back (trên cùng bên trái) -> Về trang trước
        btnBack.setOnClickListener {
            finish()
        }

        // 2. Nút Home (ngôi nhà dưới cùng) -> Về trang chủ
        navHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            // Xóa các màn hình cũ để tránh chồng chéo (quay lại không bị lỗi logic)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        // 3. Nút Thông báo (cái chuông)
        navNotification.setOnClickListener {
            Toast.makeText(this, "Bạn chưa có thông báo mới", Toast.LENGTH_SHORT).show()
        }

        // 4. Nút Mua Hàng -> Sang CheckoutActivity
        btnCheckout.setOnClickListener {
            // Kiểm tra xem có món nào được chọn không
            val hasItem = productList.any { it.checkBox.isChecked }

            if (hasItem) {
                val intent = Intent(this, CheckoutActivity::class.java)
                // Gửi tổng tiền sang màn hình Thanh toán (nếu cần dùng)
                intent.putExtra("TOTAL_AMOUNT", tvTotalAmount.text.toString())
                startActivity(intent)
            } else {
                Toast.makeText(this, "Vui lòng chọn ít nhất 1 sản phẩm", Toast.LENGTH_SHORT).show()
            }
        }
    }
}