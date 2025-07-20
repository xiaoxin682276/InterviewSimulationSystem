#!/bin/bash

echo "🚀 开始部署面试模拟系统..."

# 检查Docker是否安装
if ! command -v docker &> /dev/null; then
    echo "❌ Docker未安装，请先安装Docker"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose未安装，请先安装Docker Compose"
    exit 1
fi

# 停止现有容器
echo "🛑 停止现有容器..."
docker-compose down

# 清理旧镜像
echo "🧹 清理旧镜像..."
docker system prune -f

# 构建并启动服务
echo "🔨 构建并启动服务..."
docker-compose up --build -d

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 30

# 检查服务状态
echo "📊 检查服务状态..."
docker-compose ps

# 检查服务健康状态
echo "🏥 检查服务健康状态..."
if curl -f http://localhost:80 > /dev/null 2>&1; then
    echo "✅ 前端服务运行正常"
else
    echo "❌ 前端服务启动失败"
fi

if curl -f http://localhost:8080/api/positions > /dev/null 2>&1; then
    echo "✅ 后端服务运行正常"
else
    echo "❌ 后端服务启动失败"
fi

echo "🎉 部署完成！"
echo "📱 前端访问地址: http://localhost"
echo "🔧 后端API地址: http://localhost:8080/api"
echo "🗄️  数据库端口: localhost:3306"

# 显示日志
echo "📋 查看服务日志:"
echo "docker-compose logs -f" 