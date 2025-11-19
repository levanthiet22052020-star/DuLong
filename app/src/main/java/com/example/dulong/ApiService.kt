package com.example.dulong

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("users/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // Thêm hàm đăng ký vào đây
    // Lưu ý: đường dẫn "users/register" phải khớp với router bên NodeJS
    @POST("users/register")
    fun register(@Body request: RegisterRequest): Call<LoginResponse>
}