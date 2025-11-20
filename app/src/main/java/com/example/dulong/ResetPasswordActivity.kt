package com.example.dulong

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var etNewPass: EditText
    private lateinit var etConfirmPass: EditText
    private lateinit var ivNewPassToggle: ImageView
    private lateinit var ivConfirmPassToggle: ImageView
    private lateinit var btnConfirm: MaterialButton

    private var userPhone: String = ""
    private var isNewPassVisible = false
    private var isConfirmPassVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reset_password)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Nhận SĐT từ màn hình VerifyOtp truyền sang
        userPhone = intent.getStringExtra("phone_number") ?: ""

        initViews()
        addEvents()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar_reset_password)
        etNewPass = findViewById(R.id.et_new_password)
        etConfirmPass = findViewById(R.id.et_confirm_password)
        ivNewPassToggle = findViewById(R.id.iv_pass_toggle_new)
        ivConfirmPassToggle = findViewById(R.id.iv_pass_toggle_confirm)
        btnConfirm = findViewById(R.id.btn_confirm_reset)
    }

    private fun addEvents() {
        // Nút Back
        toolbar.setNavigationOnClickListener { finish() }

        // Nút xác nhận đổi mật khẩu
        btnConfirm.setOnClickListener {
            handleResetPassword()
        }

        // Toggle ẩn/hiện mật khẩu mới
        ivNewPassToggle.setOnClickListener {
            if (isNewPassVisible) {
                etNewPass.transformationMethod = PasswordTransformationMethod.getInstance()
                ivNewPassToggle.setImageResource(R.drawable.ic_eye_24)
                isNewPassVisible = false
            } else {
                etNewPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
                isNewPassVisible = true
            }
            etNewPass.setSelection(etNewPass.text.length)
        }

        // Toggle ẩn/hiện mật khẩu xác nhận
        ivConfirmPassToggle.setOnClickListener {
            if (isConfirmPassVisible) {
                etConfirmPass.transformationMethod = PasswordTransformationMethod.getInstance()
                ivConfirmPassToggle.setImageResource(R.drawable.ic_eye_24)
                isConfirmPassVisible = false
            } else {
                etConfirmPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
                isConfirmPassVisible = true
            }
            etConfirmPass.setSelection(etConfirmPass.text.length)
        }
    }

    private fun handleResetPassword() {
        val newPass = etNewPass.text.toString().trim()
        val confirmPass = etConfirmPass.text.toString().trim()

        // 1. Validate
        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPass != confirmPass) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show()
            return
        }

        if (userPhone.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy số điện thoại", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Gọi API
        Toast.makeText(this, "Đang cập nhật mật khẩu...", Toast.LENGTH_SHORT).show()

        val request = ResetPasswordRequest(phone = userPhone, newPassword = newPass)

        RetrofitClient.instance.resetPassword(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    Toast.makeText(this@ResetPasswordActivity, "Đổi mật khẩu thành công!", Toast.LENGTH_LONG).show()

                    // 3. Chuyển về màn hình Login để đăng nhập lại
                    val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                    // Xóa sạch back stack để không back lại được màn đổi pass
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.putExtra("register_phone", userPhone) // Điền sẵn SĐT cho tiện
                    startActivity(intent)
                } else {
                    val msg = response.body()?.message ?: "Cập nhật thất bại"
                    Toast.makeText(this@ResetPasswordActivity, msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@ResetPasswordActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}