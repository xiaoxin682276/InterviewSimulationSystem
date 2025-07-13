@echo off
echo 正在停止Java进程...
taskkill /f /im java.exe 2>nul
timeout /t 2 /nobreak >nul

echo 正在启动后端服务...
cd backend
mvn spring-boot:run 