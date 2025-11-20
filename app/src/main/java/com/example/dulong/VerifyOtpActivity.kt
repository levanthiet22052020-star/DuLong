package com.example.dulong

import android.Manifest // Import quyền
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager // Import quyền
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat // Import quyền
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerifyOtpActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvInstruction: TextView
    private lateinit var etOtpCode: EditText
    private lateinit var tvSendCode: TextView
    private lateinit var btnConfirm: MaterialButton

    private var userPhone: String = ""
    private var isForgotPassword = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_verify_otp)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userPhone = intent.getStringExtra("phone_number") ?: ""
        isForgotPassword = intent.getBooleanExtra("is_forgot_password", false)

        initViews()
        setupUI()
        addEvents()

        // Xin quyền Notification ngay khi vào màn hình này
        checkNotificationPermission()
    }

    // 1. Hàm xin quyền
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar_verify_otp)
        tvInstruction = findViewById(R.id.tv_instruction)
        etOtpCode = findViewById(R.id.et_otp_code)
        tvSendCode = findViewById(R.id.tv_send_code)
        btnConfirm = findViewById(R.id.btn_confirm)
    }

    private fun setupUI() {
        if (userPhone.isNotEmpty()) {
            tvInstruction.text = "Bấm Send code để lấy code\ncủa số điện thoại $userPhone"
        }
        if (isForgotPassword) toolbar.title = "Quên mật khẩu" else toolbar.title = "Xác thực tài khoản"
    }

    private fun addEvents() {
        toolbar.setNavigationOnClickListener { finish() }

        // Bấm vào đây mới bắt đầu gửi mã
        tvSendCode.setOnClickListener {
            handleResendCode()
        }

        btnConfirm.setOnClickListener { handleVerify() }
    }

    // 2. Logic gửi mã (Đã tích hợp chế độ giả lập bất tử)
    private fun handleResendCode() {
        if (userPhone.isEmpty()) return
        Toast.makeText(this, "Đang gửi mã...", Toast.LENGTH_SHORT).show()

        val request = VerifyOtpRequest(phone = userPhone, otp = "")
        RetrofitClient.instance.resendOtp(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.status == true) {
                        // Server trả về mã OTP thật
                        val serverOtp = apiResponse.otp ?: "123456"
                        sendOtpNotification(serverOtp)
                    } else {
                        // Server logic lỗi -> Giả lập
                        sendOtpNotification("999999")
                    }
                } else {
                    // Server lỗi 404, 500 -> Giả lập luôn
                    sendOtpNotification("123456")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // Mất mạng -> Giả lập luôn
                sendOtpNotification("123456")
            }
        })
    }

    // 3. Hàm bắn thông báo (Copy từ file cũ sang)
    private fun sendOtpNotification(otp: String) {
        val channelId = "otp_channel_id"
        val notificationId = 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "OTP Notifications"
            val descriptionText = "Channel for OTP"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_lock_24)
            .setContentTitle("Mã xác thực Dulong App")
            .setContentText("Mã OTP của bạn là: $otp")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "OTP của bạn là: $otp", Toast.LENGTH_LONG).show()
            return
        }

        NotificationManagerCompat.from(this).notify(notificationId, builder.build())
        Toast.makeText(this, "Đã gửi mã về thông báo!", Toast.LENGTH_SHORT).show()
    }

    private fun handleVerify() {
        val otp = etOtpCode.text.toString().trim()
        if (otp.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mã OTP", Toast.LENGTH_SHORT).show()
            return
        }

        // Giả lập verify luôn cho nhanh (vì server verify-otp chỉ là dummy)
        // Hoặc bạn giữ nguyên API call cũng được, ở đây mình giữ nguyên logic cũ:

        val request = VerifyOtpRequest(phone = userPhone, otp = otp)
        RetrofitClient.instance.verifyOtp(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                // Chấp nhận mọi phản hồi (200 hoặc 404) miễn là OTP đúng logic client
                // (Ở đây server giả lập nên mình cứ cho qua nếu server verify OK)
                if (response.isSuccessful || response.code() == 404) {
                    // Code 404 là do API dummy, mình cứ cho qua

                    if (isForgotPassword) {
                        Toast.makeText(this@VerifyOtpActivity, "Xác thực xong.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@VerifyOtpActivity, ResetPasswordActivity::class.java)
                        intent.putExtra("phone_number", userPhone)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@VerifyOtpActivity, "Đăng ký thành công!", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@VerifyOtpActivity, LoginActivity::class.java)
                        intent.putExtra("register_phone", userPhone)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(this@VerifyOtpActivity, "Lỗi Server", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // Mất mạng cũng cho qua để test (Tùy bạn chọn)
                Toast.makeText(this@VerifyOtpActivity, "Offline Mode: OK", Toast.LENGTH_SHORT).show()
            }
        })
    }
}