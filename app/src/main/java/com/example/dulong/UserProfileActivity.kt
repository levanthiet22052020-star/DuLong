package com.example.dulong

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout // Cần thiết để ánh xạ các container chức năng
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserProfileActivity : AppCompatActivity() {

    private lateinit var imgAvatar: ImageView
    private lateinit var tvUsername: TextView
    private lateinit var tvPhone: TextView

    // Nút chức năng chính (Sử dụng LinearLayout container để bắt sự kiện click)
    private lateinit var btnOrderHistoryContainer: LinearLayout
    private lateinit var btnAddressContainer: LinearLayout
    private lateinit var btnLogout: Button

    // --- KHAI BÁO CÁC NÚT ĐIỀU HƯỚNG MỚI ---
    private lateinit var ivSettings: ImageView // Nút Cài đặt (trên cùng)
    private lateinit var ivCart: ImageView     // Nút Giỏ hàng (trên cùng)
    private lateinit var navHome: ImageView    // Nav Trang chủ (dưới cùng)
    private lateinit var navNotification: ImageView // Nav Thông báo (dưới cùng)
    // ----------------------------------------

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var apiService: ApiService
    private var userId: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)

        // 1. Ánh xạ View
        imgAvatar = findViewById(R.id.imgAvatar)
        tvUsername = findViewById(R.id.tvUsername)
        tvPhone = findViewById(R.id.tvPhone)
        btnLogout = findViewById(R.id.btnLogout)

        // Ánh xạ các container
        btnOrderHistoryContainer = findViewById(R.id.btn_order_history_container)
        btnAddressContainer = findViewById(R.id.btn_address_container)

        // --- ÁNH XẠ CÁC NÚT ĐIỀU HƯỚNG MỚI ---
        ivSettings = findViewById(R.id.iv_settings)
        ivCart = findViewById(R.id.iv_cart)
        navHome = findViewById(R.id.nav_home)
        navNotification = findViewById(R.id.nav_notification)
        // -------------------------------------

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 2. Cấu hình SharedPreferences và Retrofit
        sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("USER_ID", null)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        // 3. Load dữ liệu nếu có UserID
        if (userId != null) {
            getUserProfile(userId!!)
        } else {
            Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show()
        }

        setupEvents()
    }

    private fun setupEvents() {

        // Vào màn hình quản lý đơn hàng
        btnOrderHistoryContainer.setOnClickListener {
            val intent = Intent(this@UserProfileActivity, TrackOrderActivity::class.java)
            startActivity(intent)
        }

        // Vào màn hình quản lý địa chỉ
        btnAddressContainer.setOnClickListener {
            val intent = Intent(this@UserProfileActivity, AddressListActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        // Click vào tên để sửa tên
        tvUsername.setOnClickListener {
            showUpdateNameDialog()
        }

        // Đăng xuất
        btnLogout.setOnClickListener {
            sharedPreferences.edit().clear().apply()
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
            // Chuyển về màn hình Home/Login và clear stack
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        // --- XỬ LÝ SỰ KIỆN ĐIỀU HƯỚNG MỚI ---

        // 1. Nút Cài đặt (iv_settings)
        ivSettings.setOnClickListener {
            // Thay SettingsActivity::class.java nếu bạn tạo
            Toast.makeText(this, "Chuyển sang màn hình Cài đặt", Toast.LENGTH_SHORT).show()
        }

        // 2. Nút Giỏ hàng (iv_cart)
        ivCart.setOnClickListener {
            val intent = Intent(this@UserProfileActivity, CardActivity::class.java)
            startActivity(intent)
        }

        // 3. Thanh điều hướng - Trang chủ (nav_home)
        navHome.setOnClickListener {
            // Chuyển về Home và clear stack (không muốn back lại Profile)
            val intent = Intent(this@UserProfileActivity, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        // 4. Thanh điều hướng - Thông báo (nav_notification)
        navNotification.setOnClickListener {
            // Thay NotificationActivity::class.java nếu bạn tạo
            Toast.makeText(this, "Chuyển sang màn hình Thông báo", Toast.LENGTH_SHORT).show()
        }
    }

    // Gọi API lấy thông tin
    private fun getUserProfile(id: String) {
        apiService.getUserProfile(id).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    val user = response.body()?.user

                    // Gán dữ liệu lên màn hình (Đã fix lỗi hiển thị tên)
                    tvUsername.text = user?.username ?: "Không tên"
                    tvPhone.text = user?.phone ?: "Không có SĐT"

                    // Load ảnh Avatar
                    Glide.with(this@UserProfileActivity)
                        .load(user?.avatar)
                        .placeholder(R.drawable.ic_account_circle_64) // Placeholder
                        .error(R.drawable.ic_account_circle_64) // Error image
                        .circleCrop()
                        .into(imgAvatar)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@UserProfileActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Hiển thị hộp thoại sửa tên
    private fun showUpdateNameDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Đổi tên hiển thị")

        val input = EditText(this)
        input.hint = "Nhập tên mới"
        input.setText(tvUsername.text)
        builder.setView(input)

        builder.setPositiveButton("Lưu") { _, _ ->
            val newName = input.text.toString()
            if (newName.isNotEmpty()) {
                updateUsername(newName)
            }
        }
        builder.setNegativeButton("Hủy") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    // Gọi API cập nhật tên
    private fun updateUsername(newName: String) {
        if (userId == null) return

        val request = UpdateProfileRequest(newUsername = newName)

        apiService.updateUsername(userId!!, request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    Toast.makeText(this@UserProfileActivity, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                    tvUsername.text = newName
                } else {
                    Toast.makeText(this@UserProfileActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@UserProfileActivity, "Lỗi mạng", Toast.LENGTH_SHORT).show()
            }
        })
    }
}