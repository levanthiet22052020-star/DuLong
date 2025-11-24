package com.example.dulong

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path
import retrofit2.http.DELETE
import retrofit2.http.PUT


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
    fun getListProduct(
        @Query("type") type: String?,
        @Query("search") search: String?
    ): Call<ProductResponse>

    @POST("products/add")
    fun addProduct(@Body product: ProductBody): Call<GeneralResponse>

    @PUT("products/update/{id}")
    fun updateProduct(@Path("id") id: String, @Body product: ProductBody): Call<GeneralResponse>

    @DELETE("products/delete/{id}")
    fun deleteProduct(@Path("id") id: String): Call<GeneralResponse>

    // --- CATEGORY API ---
    @GET("categories/list")
    fun getListCategory(): Call<CategoryResponse>
}