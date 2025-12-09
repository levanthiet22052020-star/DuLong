package com.example.dulong

// ... Giữ nguyên các class cũ (LoginRequest, RegisterRequest, v.v.) ...

data class LoginRequest(val account: String, val password: String)

data class RegisterRequest(
    val username: String,
    val phone: String,
    val password: String,
    val confirmPassword: String
)

data class User(
    val _id: String,
    val username: String,
    val phone: String,
    val avatar: String?,
    val role: String = "user",
    val createdAt: String?
)

data class LoginResponse(
    val status: Boolean,
    val message: String,
    val user: User?,
    val otp: String?
)

data class VerifyOtpRequest(val phone: String, val otp: String)
data class ResetPasswordRequest(val phone: String, val newPassword: String)

// --- MỚI THÊM: Class gửi yêu cầu đổi tên ---
data class UpdateProfileRequest(
    val newUsername: String
)
// ------------------------------------------

// ... Giữ nguyên các class Product/Category bên dưới ...
data class Product(val _id: String, val name: String, val price: Double, val image: String?, val weight: String?, val balance: String?, val flex: String?, val description: String?, val type: String?)
data class ProductResponse(val status: Boolean, val message: String, val data: List<Product>)
data class Category(val _id: String, val name: String)
data class CategoryResponse(val status: Boolean, val message: String, val data: List<Category>)
data class ProductBody(val name: String, val price: Double, val image: String?, val type: String?)
data class GeneralResponse(val status: Boolean, val message: String)