@echo off
echo Starting DuLong API Server and Android App...

REM Check if MongoDB is running
echo Checking MongoDB connection...
netstat -an | findstr :27017 >nul
if errorlevel 1 (
    echo MongoDB is not running on port 27017
    echo Please start MongoDB first: mongod --dbpath "C:\data\db"
    pause
    exit /b 1
)

REM Start API Server in new window
echo Starting API Server...
start "DuLong API Server" cmd /k "cd /d %~dp0..\DuLongSever && npm start"

REM Wait for API to start
echo Waiting for API server to start...
timeout /t 5

REM Check if API server is running
:check_api
netstat -an | findstr :3000 >nul
if errorlevel 1 (
    echo Waiting for API server...
    timeout /t 2
    goto check_api
)

echo API Server is running on http://localhost:3000

REM Build and start Android app
echo Building Android app...
cd /d %~dp0
gradlew.bat assembleDebug

echo Both services are ready!
echo API Server: http://localhost:3000
echo Android APK built successfully
pause