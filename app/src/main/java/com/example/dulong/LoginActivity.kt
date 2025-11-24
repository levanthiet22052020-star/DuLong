package com.example.dulong

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
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

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var tvSignupLink: TextView
    private lateinit var ivPassToggle: ImageView
    private lateinit var tvForgot: TextView

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

        initViews()
        addEvents()

        // Kiểm tra xem có dữ liệu từ màn Đăng ký chuyển qua không (để điền sẵn SĐT)
        val registeredPhone = intent.getStringExtra("register_phone")
        if (registeredPhone != null) {
            etUsername.setText(registeredPhone)
            etPassword.requestFocus() // Chuyển con trỏ xuống ô nhập mật khẩu luôn cho tiện
        }
    }

    private fun initViews() {
        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        tvSignupLink = findViewById(R.id.tv_signup_link)
        ivPassToggle = findViewById(R.id.iv_pass_toggle)
        tvForgot = findViewById(R.id.tv_forgot)
    }

    private fun addEvents() {
        // 1. Sự kiện đăng nhập
        btnLogin.setOnClickListener {
            handleLogin()
        }

        // 2. Chuyển sang màn đăng ký
        tvSignupLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // 3. Chuyển sang màn quên mật khẩu
        tvForgot.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        // 4. Ẩn/Hiện mật khẩu
        ivPassToggle.setOnClickListener {
            togglePasswordVisibility()
        }
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Đang hiện -> Ẩn đi (hiện dấu chấm)
            etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            ivPassToggle.setImageResource(R.drawable.ic_eye_24)
            isPasswordVisible = false
        } else {
            // Đang ẩn -> Hiện ra (hiện chữ thường)
            etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            // ivPassToggle.setImageResource(R.drawable.ic_eye_off_24) // Nếu có icon gạch chéo thì mở dòng này
            isPasswordVisible = true
        }
        // Đưa con trỏ về cuối văn bản để gõ tiếp không bị lỗi nhảy về đầu
        etPassword.setSelection(etPassword.text.length)
    }

    private fun handleLogin() {
        val account = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Validate cơ bản
        if (account.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Đang đăng nhập...", Toast.LENGTH_SHORT).show()

        val request = LoginRequest(account, password)

        // Gọi API
        RetrofitClient.instance.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && loginResponse.status) {
                        // --- ĐĂNG NHẬP THÀNH CÔNG ---
                        Toast.makeText(this@LoginActivity, "Xin chào ${loginResponse.user?.username}", Toast.LENGTH_SHORT).show()

                        // --- PHÂN QUYỀN (ROLE) ---
                        val role = loginResponse.user?.role ?: "user" // Mặc định là user nếu server ko trả về role

                        if (role == "admin") {
                            // Nếu là Admin -> Sang màn hình Quản lý
                            Log.d("LOGIN_APP", "User is Admin")
                            val intent = Intent(this@LoginActivity, AdminProductActivity::class.java)
                            startActivity(intent)
                        } else {
                            // Nếu là User thường -> Sang màn hình Trang chủ
                            Log.d("LOGIN_APP", "User is Customer")
                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            startActivity(intent)
                        }

                        // Đóng màn hình Login để không back lại được
                        finish()

                    } else {
                        // Server trả về false (Sai pass, không tìm thấy user...)
                        val msg = loginResponse?.message ?: "Đăng nhập thất bại"
                        Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Lỗi Server: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("LoginError", t.message.toString())
            }
        })
    }
}