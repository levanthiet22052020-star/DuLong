package com.example.dulong.activity.user

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dulong.R
import com.example.dulong.activity.cart.CardActivity
import kotlin.jvm.java
import com.example.dulong.activity.HomeActivity

class UserProfileActivity : AppCompatActivity() {

    // Khai báo biến SharedPreferences
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)

        // 1. Ánh xạ View (Kết nối code với giao diện XML)
        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        val tvPhone = findViewById<TextView>(R.id.tvPhone)
        val btnDetail = findViewById<ImageView>(R.id.btnDetail)
        val containerOrder = findViewById<LinearLayout>(R.id.btn_order_history_container)
        val btnCart = findViewById<ImageView>(R.id.iv_cart)
        val btnSettings = findViewById<ImageView>(R.id.iv_settings)
        val btnHome = findViewById<ImageView>(R.id.nav_home)



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)

        // Lấy dữ liệu đã lưu (Key phải khớp với bên LoginActivity)
        val savedName = sharedPreferences.getString("USER_NAME", "Người dùng")
        val savedPhone = sharedPreferences.getString("USER_PHONE", "09xxxxxxxx")
        // Lưu ý: Nếu bên LoginActivity bạn chưa thêm dòng lưu USER_PHONE thì ở đây sẽ hiện số mặc định

        // 3. Hiển thị lên giao diện
        tvUsername.text = savedName
        tvPhone.text = savedPhone

        val openOrderHistory = {
            val intent = Intent(this, TrackOrderActivity::class.java)
            startActivity(intent)
        }
        btnDetail.setOnClickListener { openOrderHistory() }
        containerOrder.setOnClickListener { openOrderHistory() }

        btnCart.setOnClickListener {
            // Đảm bảo bạn đã có CartActivity (hoặc CardActivity)
            val intent = Intent(this, CardActivity::class.java)
            startActivity(intent)
        }

        btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        btnHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

    }
}