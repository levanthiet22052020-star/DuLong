# Hướng dẫn Setup Google Sign-In

## Bước 1: Tạo Project trên Google Cloud Console

1. Truy cập [Google Cloud Console](https://console.cloud.google.com/)
2. Tạo project mới hoặc chọn project hiện có
3. Enable Google Sign-In API

## Bước 2: Tạo OAuth 2.0 Client IDs

1. Vào **APIs & Services** > **Credentials**
2. Click **Create Credentials** > **OAuth 2.0 Client IDs**
3. Tạo 2 client IDs:
   - **Android**: Chọn Application type = Android
   - **Web**: Chọn Application type = Web application

### Cho Android Client ID:
- Package name: `com.example.dulong`
- SHA-1 certificate fingerprint: Lấy từ debug keystore

### Lấy SHA-1 fingerprint:
```bash
# Windows
keytool   

# macOS/Linux  
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

## Bước 3: Download google-services.json

1. Vào **Project Settings** trong Firebase Console
2. Download file `google-services.json`
3. Thay thế file `app/google-services.json` hiện tại

## Bước 4: Cập nhật Web Client ID

1. Copy **Web Client ID** từ Google Console
2. Thay thế `YOUR_WEB_CLIENT_ID` trong file `app/src/main/res/values/strings.xml`

```xml
<string name="default_web_client_id">YOUR_ACTUAL_WEB_CLIENT_ID</string>
```

## Bước 5: API Server

Đảm bảo API server có endpoint `/users/google-login` với format:

```javascript
// Request body
{
  "googleId": "string",
  "email": "string", 
  "name": "string",
  "photoUrl": "string"
}

// Response
{
  "status": true,
  "message": "Login successful",
  "user": {
    "_id": "user_id",
    "username": "name",
    "email": "email",
    "phone": "phone",
    "role": "user"
  }
}
```

## Bước 6: Test

1. Build và chạy app
2. Click button "Tiếp tục với Google"
3. Chọn tài khoản Google
4. Kiểm tra đăng nhập thành công

## Lưu ý

- Đảm bảo package name trong `google-services.json` khớp với `applicationId` trong `build.gradle`
- SHA-1 fingerprint phải chính xác
- Web Client ID phải đúng
- API server phải hỗ trợ Google login endpoint