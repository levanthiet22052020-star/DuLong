@echo off
echo ========================================
echo      SỬA TẤT CẢ LỖI ANDROID PROJECT
echo ========================================

echo.
echo [1] Kiểm tra và sửa lỗi duplicate resources...
if exist "app\src\main\res\drawable\ic_add.png" (
    del "app\src\main\res\drawable\ic_add.png"
    echo ✅ Đã xóa ic_add.png
)
if exist "app\src\main\res\drawable\ic_arrow_back_24.png" (
    del "app\src\main\res\drawable\ic_arrow_back_24.png"
    echo ✅ Đã xóa ic_arrow_back_24.png
)
if exist "app\src\main\res\drawable\ic_arrow_forward_24.png" (
    del "app\src\main\res\drawable\ic_arrow_forward_24.png"
    echo ✅ Đã xóa ic_arrow_forward_24.png
)
if exist "app\src\main\res\drawable\ic_google_logo.png" (
    del "app\src\main\res\drawable\ic_google_logo.png"
    echo ✅ Đã xóa ic_google_logo.png
)
if exist "app\src\main\res\drawable\ic_search.png" (
    del "app\src\main\res\drawable\ic_search.png"
    echo ✅ Đã xóa ic_search.png
)

echo.
echo [2] Tìm Java từ Android Studio...
set "ANDROID_STUDIO_JBR=C:\Program Files\Android\Android Studio\jbr"
if exist "%ANDROID_STUDIO_JBR%" (
    echo ✅ Tìm thấy Java từ Android Studio
    set "JAVA_HOME=%ANDROID_STUDIO_JBR%"
    set "PATH=%JAVA_HOME%\bin;%PATH%"
    goto :clean
)

echo [3] Tìm Java từ hệ thống...
for /d %%i in ("C:\Program Files\Java\jdk*") do (
    if exist "%%i\bin\java.exe" (
        echo ✅ Tìm thấy Java tại: %%i
        set "JAVA_HOME=%%i"
        set "PATH=%JAVA_HOME%\bin;%PATH%"
        goto :clean
    )
)

echo ❌ Không tìm thấy Java
echo Vui lòng cài đặt Android Studio hoặc JDK
pause
exit /b 1

:clean
echo.
echo [4] Clean project...
.\gradlew clean
if errorlevel 1 (
    echo ❌ Clean failed
    pause
    exit /b 1
)

echo.
echo [5] Sync dependencies...
.\gradlew --refresh-dependencies
if errorlevel 1 (
    echo ⚠️  Refresh dependencies có lỗi, tiếp tục...
)

echo.
echo [6] Build project...
.\gradlew assembleDebug
if errorlevel 1 (
    echo ❌ Build failed - Kiểm tra lỗi ở trên
    pause
    exit /b 1
) else (
    echo.
    echo ✅ BUILD THÀNH CÔNG!
    echo APK được tạo tại: app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo Bạn có thể:
    echo 1. Cài đặt trên thiết bị: adb install app\build\outputs\apk\debug\app-debug.apk
    echo 2. Hoặc kéo thả file APK vào emulator
    echo 3. Hoặc mở trong Android Studio để debug
)

echo.
pause