package com.example.dulong

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // Với máy ảo Android Studio: 10.0.2.2 thay cho localhost
    private const val BASE_URL = "http://10.0.2.2:3000/"

    val instance: ApiService by lazy {

        // 1. Tạo bộ chặn log (Interceptor) để xem dữ liệu gửi đi/nhận về
        val logging = HttpLoggingInterceptor().apply {
            // Level.BODY sẽ in ra toàn bộ nội dung JSON
            level = HttpLoggingInterceptor.Level.BODY
        }

        // 2. Cấu hình Client với timeout và logging
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS) // Tăng thời gian chờ lên 30s
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        // 3. Khởi tạo Retrofit
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client) // Quan trọng: Gắn client đã cấu hình vào đây
            .build()
            .create(ApiService::class.java)
    }
}