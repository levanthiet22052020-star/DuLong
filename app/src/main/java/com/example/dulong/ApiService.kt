package com.example.dulong

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @POST("users/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // Thêm hàm đăng ký vào đây
    // Lưu ý: đường dẫn "users/register" phải khớp với router bên NodeJS
    @POST("users/register")
    fun register(@Body request: RegisterRequest): Call<LoginResponse>

    // API xác thực OTP
    @POST("users/verify-otp")
    fun verifyOtp(@Body request: VerifyOtpRequest): Call<LoginResponse>

    // API yêu cầu gửi lại mã (nếu cần)
    @POST("users/resend-otp")
    fun resendOtp(@Body request: VerifyOtpRequest): Call<LoginResponse>

    @POST("users/reset-password")
    fun resetPassword(@Body request: ResetPasswordRequest): Call<LoginResponse>

    @GET("products/list")
    fun getListProduct(@Query("type") type: String): Call<ProductResponse>
}