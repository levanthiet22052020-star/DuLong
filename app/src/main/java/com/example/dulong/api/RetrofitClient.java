package com.example.dulong.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // Tự động chọn URL dựa trên môi trường
    private static final String BASE_URL = NetworkConfig.getBaseUrl();
    
    private static ApiService instance;

    public static ApiService getInstance() {
        if (instance == null) {
            // 1. Tạo bộ chặn log (Interceptor) để xem dữ liệu gửi đi/nhận về
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // Level.BODY sẽ in ra toàn bộ nội dung JSON
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 2. Cấu hình Client với timeout và logging
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(30, TimeUnit.SECONDS) // Tăng thời gian chờ lên 30s
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            // 3. Khởi tạo Retrofit
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client) // Quan trọng: Gắn client đã cấu hình vào đây
                    .build();
                    
            instance = retrofit.create(ApiService.class);
        }
        return instance;
    }
}