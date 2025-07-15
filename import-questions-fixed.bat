@echo off
echo ========================================
echo 题库数据导入脚本（修复版）
echo ========================================
echo.

echo 正在检查MySQL连接...

REM 检查MySQL是否运行
mysql --version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: MySQL未安装或未配置PATH环境变量
    echo 请确保MySQL已正确安装并配置PATH
    pause
    exit /b 1
)

echo MySQL连接正常
echo.

echo 正在导入题库数据（修复版）...

REM 执行修复版数据库初始化脚本
mysql -u root -p123456 < init-database-fixed.sql

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo 题库数据导入成功！
    echo ========================================
    echo.
    echo 已导入的题库数据：
    echo - 前端开发: 技术能力、项目经验、学习能力、沟通能力
    echo - 后端开发: 技术能力、项目经验、学习能力、沟通能力  
    echo - 全栈开发: 技术能力、项目经验、学习能力、沟通能力
    echo.
    echo 每个岗位包含多个分类，每个分类包含多个题目
    echo 系统会随机抽取题目进行面试模拟
    echo.
    echo 题目统计信息：
    echo - 前端开发: 约20道题目
    echo - 后端开发: 约20道题目
    echo - 全栈开发: 约20道题目
    echo.
    echo 题目特点：
    echo - 涵盖技术能力、项目经验、学习能力、沟通能力
    echo - 包含不同难度级别（简单、中等、困难）
    echo - 支持不同题目类型（技术、行为、问题解决、系统设计）
    echo - 支持标签分类和筛选
    echo.
    echo 修复内容：
    echo - 移除了JSON类型，改为TEXT类型
    echo - 添加了DROP TABLE语句避免冲突
    echo - 优化了MySQL兼容性
    echo.
) else (
    echo.
    echo ========================================
    echo 题库数据导入失败！
    echo ========================================
    echo.
    echo 可能的原因：
    echo 1. MySQL服务未启动
    echo 2. 数据库连接信息错误
    echo 3. 数据库权限不足
    echo 4. SQL脚本语法错误
    echo.
    echo 请检查：
    echo - MySQL服务是否正常运行
    echo - 数据库连接信息是否正确
    echo - 用户权限是否足够
    echo.
)

echo 按任意键继续...
pause 