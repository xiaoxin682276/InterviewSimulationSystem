@echo off
echo ========================================
echo MySQL连接检查
echo ========================================

echo.
echo 请检查以下配置：
echo.
echo 1. MySQL服务是否启动
echo 2. 数据库连接配置是否正确
echo.

echo 当前配置（backend/src/main/resources/application.yml）：
echo - 主机: localhost
echo - 端口: 3306
echo - 数据库: interview_system
echo - 用户名: root
echo - 密码: 123456
echo.

echo 如果您的MySQL密码不是123456，请修改配置文件：
echo 1. 打开 backend/src/main/resources/application.yml
echo 2. 修改 password: 123456 为您的实际密码
echo 3. 保存文件
echo 4. 重新启动后端服务
echo.

echo 常见密码：
echo - 空密码: password: ""
echo - 默认密码: password: "password"
echo - 其他密码: password: "您的密码"
echo.

echo 创建数据库的SQL命令：
echo CREATE DATABASE IF NOT EXISTS interview_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
echo.

echo 验证数据库的SQL命令：
echo SHOW DATABASES;
echo USE interview_system;
echo SHOW TABLES;
echo.

pause 