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

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etPass: EditText
    private lateinit var etConfirmPass: EditText
    private lateinit var btnRegister: MaterialButton
    private lateinit var tvLoginLink: TextView
    private lateinit var ivPassToggle: ImageView
    private lateinit var ivConfirmPassToggle: ImageView

    private var isPassVisible = false
    private var isConfirmPassVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Phải ánh xạ (initViews) xong hết các view...
        initViews()
        // 2. ...thì mới được gán sự kiện (addEvents)
        addEvents()
    }

    private fun initViews() {
        etName = findViewById(R.id.et_name)
        etPhone = findViewById(R.id.et_phone)
        etPass = findViewById(R.id.et_password)
        etConfirmPass = findViewById(R.id.et_confirm_password)
        btnRegister = findViewById(R.id.btn_register)
        tvLoginLink = findViewById(R.id.tv_login_link)

        // SỬA LỖI 1: Đưa ánh xạ 2 con mắt vào đây luôn để đảm bảo nó được khởi tạo trước khi dùng
        ivPassToggle = findViewById(R.id.iv_pass_toggle)
        ivConfirmPassToggle = findViewById(R.id.iv_confirm_pass_toggle)
    }

    private fun addEvents() {
        // Quay lại màn đăng nhập
        tvLoginLink.setOnClickListener {
            finish()
        }

        // Xử lý đăng ký
        btnRegister.setOnClickListener {
            handleRegister()
        }

        // Xử lý ẩn/hiện pass
        ivPassToggle.setOnClickListener {
            if (isPassVisible) {
                etPass.transformationMethod = PasswordTransformationMethod.getInstance()
                ivPassToggle.setImageResource(R.drawable.ic_eye_24)
                isPassVisible = false
            } else {
                etPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
                // ivPassToggle.setImageResource(R.drawable.ic_eye_off_24) // Mở comment nếu có icon
                isPassVisible = true
            }
            etPass.setSelection(etPass.text.length)
        }

        // Xử lý ẩn/hiện confirm pass
        ivConfirmPassToggle.setOnClickListener {
            if (isConfirmPassVisible) {
                etConfirmPass.transformationMethod = PasswordTransformationMethod.getInstance()
                ivConfirmPassToggle.setImageResource(R.drawable.ic_eye_24)
                isConfirmPassVisible = false
            } else {
                etConfirmPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
                // ivConfirmPassToggle.setImageResource(R.drawable.ic_eye_off_24) // Mở comment nếu có icon
                isConfirmPassVisible = true
            }
            etConfirmPass.setSelection(etConfirmPass.text.length)
        }
    }
    // SỬA LỖI 2: Xóa dấu ngoặc thừa ở đây, đảm bảo handleRegister nằm trong class

    private fun handleRegister() {
        val name = etName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val pass = etPass.text.toString().trim()
        val confirmPass = etConfirmPass.text.toString().trim()

        // 1. Validate dữ liệu cơ bản
        if (name.isEmpty() || phone.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            return
        }

        if (pass != confirmPass) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Gọi API
        val request = RegisterRequest(username = name, phone = phone, password = pass, confirmPassword = confirmPass)

        Toast.makeText(this, "Đang đăng ký...", Toast.LENGTH_SHORT).show()

        RetrofitClient.instance.register(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.status) {
                        Toast.makeText(this@RegisterActivity, "Đăng ký thành công!", Toast.LENGTH_LONG).show()

                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        intent.putExtra("register_phone", phone)
                        startActivity(intent)
                        finishAffinity()
                    } else {
                        val msg = apiResponse?.message ?: "Đăng ký thất bại"
                        Toast.makeText(this@RegisterActivity, msg, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, "Lỗi Server: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("RegisterError", t.message.toString())
            }
        })
    }
} // Kết thúc class RegisterActivity ở đây