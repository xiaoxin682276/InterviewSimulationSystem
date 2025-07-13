@echo off
echo ========================================
echo 创建数据库和表
echo ========================================

echo.
echo 请按照以下步骤操作：
echo.
echo 1. 打开MySQL Workbench或phpMyAdmin
echo 2. 连接到您的MySQL服务器
echo 3. 执行以下SQL命令：
echo.

echo CREATE DATABASE IF NOT EXISTS interview_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
echo.
echo 4. 然后执行init-database.sql文件中的内容
echo.

echo 或者，如果您有MySQL命令行工具，请运行：
echo mysql -u root -p < init-database.sql
echo.

echo 完成后，重新启动后端服务：
echo cd backend
echo mvn spring-boot:run
echo.

pause 