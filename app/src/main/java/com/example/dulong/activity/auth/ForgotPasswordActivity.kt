package com.example.dulong.activity.auth

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dulong.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var etPhone: EditText
    private lateinit var btnNext: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        addEvents()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar_forgot_password)
        etPhone = findViewById(R.id.et_phone)
        btnNext = findViewById(R.id.btn_next)
    }

    private fun addEvents() {
        toolbar.setNavigationOnClickListener { finish() }

        btnNext.setOnClickListener {
            // Chỉ chuyển trang, không gửi mã ở đây nữa
            handleNextStep()
        }
    }

    private fun handleNextStep() {
        val phone = etPhone.text.toString().trim()

        if (phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show()
            return
        }

        // Chuyển ngay sang màn VerifyOtpActivity
        val intent = Intent(this, VerifyOtpActivity::class.java)
        intent.putExtra("phone_number", phone)
        intent.putExtra("is_forgot_password", true)
        startActivity(intent)
    }
}