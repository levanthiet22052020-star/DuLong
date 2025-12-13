package com.example.dulong.api;

public class NetworkConfig {
    // Cấu hình cho Emulator (mặc định)
    public static final String EMULATOR_BASE_URL = "http://10.0.2.2:3000/";
    
    // Cấu hình cho thiết bị thật - THAY ĐỔI IP NÀY
    public static final String DEVICE_BASE_URL = "http://192.168.1.100:3000/";
    
    // Force sử dụng emulator URL (để debug)
    private static boolean forceEmulator = true; // Mặc định dùng emulator
    
    // Tự động phát hiện môi trường
    public static String getBaseUrl() {
        if (forceEmulator || isEmulator()) {
            return EMULATOR_BASE_URL;
        } else {
            return DEVICE_BASE_URL;
        }
    }
    
    // Force sử dụng emulator URL
    public static void setForceEmulator(boolean force) {
        forceEmulator = force;
    }
    
    // Lấy IP máy tính hiện tại (cần cập nhật thủ công)
    public static String getCurrentPCIP() {
        // TODO: Cập nhật IP này theo máy tính của bạn
        return "192.168.1.100"; // Thay đổi IP này
    }
    
    // Phương thức để test kết nối
    public static boolean isEmulator() {
        return android.os.Build.FINGERPRINT.contains("generic") ||
               android.os.Build.FINGERPRINT.contains("unknown") ||
               android.os.Build.FINGERPRINT.contains("emulator") ||
               android.os.Build.MODEL.contains("google_sdk") ||
               android.os.Build.MODEL.contains("Emulator") ||
               android.os.Build.MODEL.contains("Android SDK built for x86") ||
               android.os.Build.MODEL.contains("sdk_gphone") ||
               android.os.Build.MANUFACTURER.contains("Genymotion") ||
               android.os.Build.MANUFACTURER.contains("Google") ||
               android.os.Build.HARDWARE.contains("goldfish") ||
               android.os.Build.HARDWARE.contains("ranchu") ||
               (android.os.Build.BRAND.startsWith("generic") && android.os.Build.DEVICE.startsWith("generic")) ||
               android.os.Build.PRODUCT.contains("sdk") ||
               android.os.Build.PRODUCT.contains("emulator");
    }
}