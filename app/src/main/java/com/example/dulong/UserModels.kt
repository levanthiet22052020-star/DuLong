package com.example.dulong

// 1. Gửi lên Server khi Login
data class LoginRequest(
    val account: String,
    val password: String
)

// 2. Gửi lên Server khi Register (Thêm cái này)
data class RegisterRequest(
    val username: String, // Phải khớp tên biến server NodeJS yêu cầu
    val phone: String,
    val password: String,
    val confirmPassword: String
)

// 3. Thông tin User
data class User(
    val _id: String,
    val username: String,
    val phone: String,
    val avatar: String?,
    val createdAt: String?
)

// 4. Phản hồi chung từ Server (Dùng chung cho cả Login và Register)
data class LoginResponse(
    val status: Boolean,
    val message: String,
    val user: User?,
    val otp: String? // <-- Thêm dòng này để hứng mã OTP từ server
)

// Gửi số điện thoại và mã OTP người dùng nhập lên server
data class VerifyOtpRequest(
    val phone: String,
    val otp: String
)

data class ResetPasswordRequest(
    val phone: String, // Dùng số điện thoại làm định danh (hoặc ID nếu bạn có)
    val newPassword: String
)

data class Product(
    val _id: String,
    val name: String,
    val price: Double,
    val image: String?,
    val weight: String?,
    val balance: String?,
    val flex: String?,
    val description: String?,
    val type: String?
)

// Thêm class Response cho list Product
data class ProductResponse(
    val status: Boolean,
    val message: String,
    val data: List<Product>
)