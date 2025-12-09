package com.example.dulong

import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("users/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("users/register")
    fun register(@Body request: RegisterRequest): Call<LoginResponse>

    @POST("users/verify-otp")
    fun verifyOtp(@Body request: VerifyOtpRequest): Call<LoginResponse>

    @POST("users/resend-otp")
    fun resendOtp(@Body request: VerifyOtpRequest): Call<LoginResponse>

    @POST("users/reset-password")
    fun resetPassword(@Body request: ResetPasswordRequest): Call<LoginResponse>

    // --- MỚI THÊM: API PROFILE ---
    // Lưu ý: Đường dẫn "profile/..." phải khớp với cách bạn khai báo trong app.js
    @GET("profile/{userId}")
    fun getUserProfile(@Path("userId") userId: String): Call<LoginResponse>

    @PUT("profile/{userId}/username")
    fun updateUsername(
        @Path("userId") userId: String,
        @Body request: UpdateProfileRequest
    ): Call<LoginResponse>
    // -----------------------------

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

    @GET("categories/list")
    fun getListCategory(): Call<CategoryResponse>
}