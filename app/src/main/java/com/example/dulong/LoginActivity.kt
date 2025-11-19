package com.example.dulong

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.ImageView

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var tvSignupLink: TextView
    private lateinit var ivPassToggle: ImageView
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        tvSignupLink = findViewById(R.id.tv_signup_link)
        ivPassToggle = findViewById(R.id.iv_pass_toggle)


        btnLogin.setOnClickListener {
            handleLogin()
        }

        tvSignupLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        setupPasswordToggle()
    }

    private fun setupPasswordToggle() {
        ivPassToggle.setOnClickListener {
            if (isPasswordVisible) {
                // Đang hiện -> Ẩn đi (hiện dấu chấm)
                etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                // Đổi icon mắt (nếu bạn có icon mắt gạch chéo thì thay vào đây)
                ivPassToggle.setImageResource(R.drawable.ic_eye_24)
                isPasswordVisible = false
            } else {
                // Đang ẩn -> Hiện ra (hiện chữ thường)
                etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                // Đổi icon mắt (để người dùng biết đang mở)
                // ivPassToggle.setImageResource(R.drawable.ic_eye_off_24) // Ví dụ nếu có icon gạch chéo
                isPasswordVisible = true
            }
            // Đưa con trỏ về cuối văn bản để gõ tiếp không bị lỗi
            etPassword.setSelection(etPassword.text.length)
        }
    }

    private fun handleLogin() {
        val account = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (account.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Thông báo bắt đầu gọi API (để biết nút bấm đã ăn)
        Toast.makeText(this, "Đang kết nối server...", Toast.LENGTH_SHORT).show()
        Log.d("API_LOG", "Bắt đầu gửi request: $account")

        val request = LoginRequest(account, password)
        RetrofitClient.instance.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                // Log mã phản hồi (200, 400, 500...)
                Log.d("API_LOG", "Mã phản hồi: ${response.code()}")

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && loginResponse.status) {
                        // --- THÀNH CÔNG ---
                        Toast.makeText(this@LoginActivity, "Đăng nhập thành công!", Toast.LENGTH_LONG).show()
                        Log.d("API_LOG", "Login Success: ${loginResponse.user?.username}")

                        // Chuyển màn hình (Nhớ tạo HomeActivity trước)
                        //val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        //startActivity(intent)
                        //finish()
                    } else {
                        // Server trả về false (sai pass, không tìm thấy user...)
                        val msg = loginResponse?.message ?: "Đăng nhập thất bại"
                        Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_SHORT).show()
                        Log.e("API_LOG", "Login Failed Logic: $msg")
                    }
                } else {
                    // Lỗi HTTP (404 Not Found, 500 Server Error...)
                    Toast.makeText(this@LoginActivity, "Lỗi server: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("API_LOG", "Server Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // Lỗi mạng (không có internet, server tắt, sai IP...)
                Toast.makeText(this@LoginActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("API_LOG", "Network Error: ${t.message}")
                t.printStackTrace()
            }
        })
    }
}