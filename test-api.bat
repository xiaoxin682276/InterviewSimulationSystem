@echo off
echo ========================================
echo 测试后端API连接
echo ========================================

echo.
echo 测试后端服务是否响应...
curl -X GET http://localhost:8080/api/auth/check-auth?username=admin

echo.
echo.
echo 如果看到JSON响应，说明后端API正常工作
echo 如果看到错误，请检查：
echo 1. 后端服务是否在8080端口运行
echo 2. 防火墙是否阻止了连接
echo 3. 浏览器是否能够访问 http://localhost:8080
echo.

pause 