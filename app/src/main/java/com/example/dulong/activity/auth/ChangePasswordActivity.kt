package com.example.dulong.activity.auth

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dulong.R
import com.example.dulong.api.RetrofitClient
import com.example.dulong.model.ChangePasswordRequest
import com.example.dulong.model.GeneralResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_change_password)

        // Xử lý Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ánh xạ
        val etOldPass = findViewById<EditText>(R.id.etOldPass)
        val etNewPass = findViewById<EditText>(R.id.etNewPass)
        val etConfirmPass = findViewById<EditText>(R.id.etConfirmPass)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // Sự kiện nút Lưu
        btnSave.setOnClickListener {
            val oldPass = etOldPass.text.toString().trim()
            val newPass = etNewPass.text.toString().trim()
            val confirmPass = etConfirmPass.text.toString().trim()

            // 1. Validate dữ liệu
            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPass != confirmPass) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPass.length < 6) {
                Toast.makeText(this, "Mật khẩu mới phải từ 6 ký tự trở lên", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Lấy UserID từ SharedPreferences (đã lưu lúc đăng nhập)
            val sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getString("USER_ID", "")

            if (userId.isNullOrEmpty()) {
                Toast.makeText(this, "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3. Gọi API đổi mật khẩu
            val requestBody = ChangePasswordRequest(userId, oldPass, newPass)

            RetrofitClient.instance.changePassword(requestBody).enqueue(object : Callback<GeneralResponse> {
                override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        Toast.makeText(this@ChangePasswordActivity, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show()
                        finish() // Đóng màn hình
                    } else {
                        // Lấy thông báo lỗi từ Server (ví dụ: "Mật khẩu cũ không đúng")
                        val msg = response.body()?.message ?: "Đổi mật khẩu thất bại"
                        Toast.makeText(this@ChangePasswordActivity, msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                    Toast.makeText(this@ChangePasswordActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}