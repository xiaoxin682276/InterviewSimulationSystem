@echo off
echo ========================================
echo 智能面试系统 - 快速启动（H2数据库）
echo ========================================

echo.
echo 正在启动后端服务（使用H2内存数据库）...
cd backend
mvn spring-boot:run -Dspring.profiles.active=h2

echo.
echo 后端服务启动完成！
echo 访问地址：
echo - 后端API: http://localhost:8080
echo - H2数据库控制台: http://localhost:8080/h2-console
echo.
echo 测试账号：
echo - admin/password
echo - testuser/password
echo - zhangsan/password
echo - lisi/password
echo - wangwu/password
echo.
pause 