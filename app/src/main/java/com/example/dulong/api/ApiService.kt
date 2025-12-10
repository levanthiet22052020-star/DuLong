package com.example.dulong.api

import com.example.dulong.model.AddressListResponse
import com.example.dulong.model.AddressModel
import com.example.dulong.model.CategoryResponse
import com.example.dulong.model.ChangePasswordRequest // Import class mới tạo
import com.example.dulong.model.GeneralResponse
import com.example.dulong.model.LoginRequest
import com.example.dulong.model.LoginResponse
import com.example.dulong.model.ProductBody
import com.example.dulong.model.ProductResponse
import com.example.dulong.model.RegisterRequest
import com.example.dulong.model.ResetPasswordRequest
import com.example.dulong.model.UpdateProfileRequest
import com.example.dulong.model.VerifyOtpRequest
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

    // --- API ĐỔI MẬT KHẨU (Thêm dòng này) ---
    @POST("users/change-password")
    fun changePassword(@Body request: ChangePasswordRequest): Call<GeneralResponse>

    @GET("profile/{userId}")
    fun getUserProfile(@Path("userId") userId: String): Call<LoginResponse>

    @PUT("profile/{userId}/username")
    fun updateUsername(
        @Path("userId") userId: String,
        @Body request: UpdateProfileRequest
    ): Call<LoginResponse>

    @GET("products/list")
    fun getListProduct(
        @Query("type") type: String?,
        @Query("search") search: String?,
        @Query("categoryId") categoryId: String?
    ): Call<ProductResponse>

    @POST("products/add")
    fun addProduct(@Body product: ProductBody): Call<GeneralResponse>

    @PUT("products/update/{id}")
    fun updateProduct(@Path("id") id: String, @Body product: ProductBody): Call<GeneralResponse>

    @DELETE("products/delete/{id}")
    fun deleteProduct(@Path("id") id: String): Call<GeneralResponse>

    @GET("categories/list")
    fun getCategories(): Call<CategoryResponse>

    // --- API ĐỊA CHỈ ---
    @GET("address/{userId}")
    fun getAddresses(@Path("userId") userId: String): Call<AddressListResponse>

    @POST("address/add")
    fun addAddress(@Body address: AddressModel): Call<GeneralResponse>

    @PUT("address/edit/{id}")
    fun updateAddress(@Path("id") id: String, @Body address: AddressModel): Call<GeneralResponse>

    @DELETE("address/delete/{id}")
    fun deleteAddress(@Path("id") id: String): Call<GeneralResponse>
}