@echo off
echo ========================================
echo 智能面试模拟系统 - 快速启动脚本
echo ========================================

echo.
echo 1. 检查MySQL服务状态...
net start mysql >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] MySQL服务启动失败，请手动启动MySQL服务
    pause
    exit /b 1
) else (
    echo [成功] MySQL服务正在运行
)

echo.
echo 2. 检查数据库连接...
mysql -u root -p123456 -e "USE interview_system;" >nul 2>&1
if %errorlevel% neq 0 (
    echo [警告] 数据库连接失败，请检查数据库配置
    echo 请确保：
    echo - 数据库 interview_system 已创建
    echo - 用户名和密码正确
    echo - MySQL服务正在运行
    pause
) else (
    echo [成功] 数据库连接正常
)

echo.
echo 3. 启动后端服务...
cd backend
echo 正在编译和启动后端服务...
start "Backend Service" cmd /k "mvn spring-boot:run"

echo.
echo 4. 等待后端服务启动...
timeout /t 10 /nobreak >nul

echo.
echo 5. 启动前端服务...
cd ..\frontend
echo 正在启动前端服务...
start "Frontend Service" cmd /k "npm start"

echo.
echo ========================================
echo 系统启动完成！
echo ========================================
echo.
echo 后端服务: http://localhost:8080
echo 前端服务: http://localhost:3000
echo.
echo 测试账号:
echo 用户名: admin, testuser, zhangsan, lisi, wangwu
echo 密码: password
echo.
echo 如果遇到问题，请查看 TROUBLESHOOTING.md
echo.
pause 