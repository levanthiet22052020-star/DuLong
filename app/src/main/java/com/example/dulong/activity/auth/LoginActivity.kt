package com.example.dulong.activity.auth

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
import com.example.dulong.activity.admin.AdminProductActivity
import com.example.dulong.activity.HomeActivity
import com.example.dulong.model.LoginRequest
import com.example.dulong.model.LoginResponse
import com.example.dulong.R
import com.example.dulong.api.RetrofitClient
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

        val registeredPhone = intent.getStringExtra("register_phone")
        if (registeredPhone != null) {
            etUsername.setText(registeredPhone)
            etPassword.requestFocus()
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
        btnLogin.setOnClickListener {
            handleLogin()
        }

        tvSignupLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        tvForgot.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        ivPassToggle.setOnClickListener {
            togglePasswordVisibility()
        }
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            ivPassToggle.setImageResource(R.drawable.ic_eye_24)
            isPasswordVisible = false
        } else {
            etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            // ivPassToggle.setImageResource(R.drawable.ic_eye_off_24)
            isPasswordVisible = true
        }
        etPassword.setSelection(etPassword.text.length)
    }

    private fun handleLogin() {
        val account = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

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

                        val user = loginResponse.user

                        // 1. LƯU ID VÀO BỘ NHỚ (ĐÃ THÊM)
                        val sharedPreferences = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("USER_ID", user?._id)
                        editor.putString("USER_ROLE", user?.role)
                        editor.putString("USER_NAME", user?.username)
                        editor.putString("USER_PHONE", user?.phone)
                        editor.apply()

                        Toast.makeText(this@LoginActivity, "Xin chào ${user?.username}", Toast.LENGTH_SHORT).show()

                        // 2. Phân quyền (Role)
                        val role = user?.role ?: "user"

                        if (role == "admin") {
                            Log.d("LOGIN_APP", "User is Admin")
                            val intent =
                                Intent(this@LoginActivity, AdminProductActivity::class.java)
                            startActivity(intent)
                        } else {
                            Log.d("LOGIN_APP", "User is Customer")
                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            startActivity(intent)
                        }

                        finish()

                    } else {
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