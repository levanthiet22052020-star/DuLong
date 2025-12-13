package com.example.dulong.api;

import com.example.dulong.model.AddressListResponse;
import com.example.dulong.model.AddressModel;
import com.example.dulong.model.AdminOrderResponse;
import com.example.dulong.model.CategoryResponse;
import com.example.dulong.model.ChangePasswordRequest;
import com.example.dulong.model.DashboardResponse;
import com.example.dulong.model.GeneralResponse;
import com.example.dulong.model.LoginRequest;
import com.example.dulong.model.LoginResponse;
import com.example.dulong.model.NotificationResponse;
import com.example.dulong.model.OrderResponse;
import com.example.dulong.model.ProductBody;
import com.example.dulong.model.ProductResponse;
import com.example.dulong.model.RegisterRequest;
import com.example.dulong.model.ResetPasswordRequest;
import com.example.dulong.model.UpdateProfileRequest;
import com.example.dulong.model.VerifyOtpRequest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("users/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    
    @POST("users/google-login")
    Call<LoginResponse> googleLogin(@Body Map<String, String> request);

    @POST("users/register")
    Call<LoginResponse> register(@Body RegisterRequest request);

    @POST("users/verify-otp")
    Call<LoginResponse> verifyOtp(@Body VerifyOtpRequest request);

    @POST("users/resend-otp")
    Call<LoginResponse> resendOtp(@Body VerifyOtpRequest request);

    @POST("users/reset-password")
    Call<LoginResponse> resetPassword(@Body ResetPasswordRequest request);

    // --- API ĐỔI MẬT KHẨU ---
    @POST("users/change-password")
    Call<GeneralResponse> changePassword(@Body ChangePasswordRequest request);

    @GET("profile/{userId}")
    Call<LoginResponse> getUserProfile(@Path("userId") String userId);

    @PUT("profile/{userId}/username")
    Call<LoginResponse> updateUsername(@Path("userId") String userId, @Body UpdateProfileRequest request);

    @GET("products/list")
    Call<ProductResponse> getListProduct(
            @Query("type") String type,
            @Query("search") String search,
            @Query("categoryId") String categoryId
    );

    @POST("products/add")
    Call<GeneralResponse> addProduct(@Body ProductBody product);

    @PUT("products/update/{id}")
    Call<GeneralResponse> updateProduct(@Path("id") String id, @Body ProductBody product);

    @DELETE("products/delete/{id}")
    Call<GeneralResponse> deleteProduct(@Path("id") String id);

    @GET("categories/list")
    Call<CategoryResponse> getCategories();



    @GET("orders/user/{userId}")
    Call<OrderResponse> getUserOrders(@Path("userId") String userId);

    @GET("orders/history/{userId}")
    Call<OrderResponse> getOrderHistory(@Path("userId") String userId);

    @GET("orders/detail/{orderId}")
    Call<OrderResponse> getOrderDetail(@Path("orderId") String orderId);

    @DELETE("categories/delete/{id}")
    Call<GeneralResponse> deleteCategory(@Path("id") String id);

    // --- API QUẢN LÝ DANH MỤC ADMIN ---
    @GET("admin/categories")
    Call<CategoryResponse> getAdminCategories();

    @POST("admin/categories")
    Call<GeneralResponse> addAdminCategory(@Body Map<String, String> category);

    @PUT("admin/categories/{id}")
    Call<GeneralResponse> updateAdminCategory(@Path("id") String id, @Body Map<String, String> category);

    @DELETE("admin/categories/{id}")
    Call<GeneralResponse> deleteAdminCategory(@Path("id") String id);

    @PATCH("admin/categories/{id}/toggle-status")
    Call<GeneralResponse> toggleCategoryStatus(@Path("id") String id);

    // --- API QUẢN LÝ ĐỠN HÀNG ADMIN ---
    @GET("admin/orders")
    Call<AdminOrderResponse> getAdminOrders(
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("status") String status,
            @Query("search") String search
    );

    @GET("admin/orders/{id}")
    Call<OrderResponse> getAdminOrderDetail(@Path("id") String id);

    @PATCH("admin/orders/{id}/status")
    Call<GeneralResponse> updateAdminOrderStatus(@Path("id") String id, @Body Map<String, String> statusData);

    @GET("admin/orders/stats/overview")
    Call<GeneralResponse> getOrderStats(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

    // --- API DASHBOARD ADMIN ---
    @GET("admin/dashboard/stats")
    Call<DashboardResponse> getDashboardStats();

    @GET("admin/dashboard/stats/detailed")
    Call<DashboardResponse> getDetailedStats(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("period") String period
    );

    // --- API CHECKOUT ---
    @POST("orders/checkout")
    Call<GeneralResponse> checkout(@Body Map<String, Object> orderData);

    // --- API THÔNG BÁO ---
    @GET("notifications/{userId}")
    Call<NotificationResponse> getNotifications(
            @Path("userId") String userId,
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("type") String type
    );

    @POST("notifications")
    Call<GeneralResponse> addNotification(@Body Map<String, Object> notification);

    @PATCH("notifications/{id}/read")
    Call<GeneralResponse> markNotificationAsRead(@Path("id") String id);

    @PATCH("notifications/user/{userId}/read-all")
    Call<GeneralResponse> markAllNotificationsAsRead(@Path("userId") String userId);

    @DELETE("notifications/{id}")
    Call<GeneralResponse> deleteNotification(@Path("id") String id);

    @POST("notifications/broadcast")
    Call<GeneralResponse> broadcastNotification(@Body Map<String, String> notification);

    // --- API TEST ---
    @GET("test/ping")
    Call<GeneralResponse> testConnection();

    @GET("test/db-status")
    Call<GeneralResponse> testDatabaseStatus();

    @GET("test/endpoints")
    Call<GeneralResponse> getApiEndpoints();

    // --- API ĐỊA CHỈ MỚI ---
    @GET("addresses/user/{userId}")
    Call<AddressListResponse> getUserAddresses(@Path("userId") String userId);

    @GET("addresses/user/{userId}/default")
    Call<AddressListResponse> getDefaultAddress(@Path("userId") String userId);

    @POST("addresses")
    Call<GeneralResponse> addUserAddress(@Body AddressModel address);

    @PUT("addresses/{id}")
    Call<GeneralResponse> updateUserAddress(@Path("id") String id, @Body AddressModel address);

    @DELETE("addresses/{id}")
    Call<GeneralResponse> deleteUserAddress(@Path("id") String id);

    @PATCH("addresses/{id}/set-default")
    Call<GeneralResponse> setDefaultAddress(@Path("id") String id);
}