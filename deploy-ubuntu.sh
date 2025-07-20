#!/bin/bash

echo "🚀 开始部署面试模拟系统到Ubuntu VPS..."

# 检查Docker是否安装
if ! command -v docker &> /dev/null; then
    echo "❌ Docker未安装，正在安装Docker..."
    curl -fsSL https://get.docker.com -o get-docker.sh
    sh get-docker.sh
    sudo usermod -aG docker $USER
    echo "✅ Docker安装完成，请重新登录或重启系统"
    exit 1
fi

# 检查Docker Compose是否安装
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose未安装，正在安装..."
    sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
    echo "✅ Docker Compose安装完成"
fi

# 停止现有容器
echo "🛑 停止现有容器..."
docker-compose down

# 清理旧镜像和容器
echo "🧹 清理旧镜像和容器..."
docker system prune -f
docker volume prune -f

# 构建并启动服务
echo "🔨 构建并启动服务..."
docker-compose up --build -d

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 45

# 检查服务状态
echo "📊 检查服务状态..."
docker-compose ps

# 检查服务健康状态
echo "🏥 检查服务健康状态..."

# 检查前端服务
if curl -f http://localhost:80 > /dev/null 2>&1; then
    echo "✅ 前端服务运行正常"
else
    echo "❌ 前端服务启动失败，查看日志："
    docker-compose logs frontend
fi

# 检查后端服务
if curl -f http://localhost:8080/api/positions > /dev/null 2>&1; then
    echo "✅ 后端服务运行正常"
else
    echo "❌ 后端服务启动失败，查看日志："
    docker-compose logs backend
fi

# 检查数据库服务
if docker-compose exec mysql mysqladmin ping -h localhost --silent; then
    echo "✅ 数据库服务运行正常"
else
    echo "❌ 数据库服务启动失败，查看日志："
    docker-compose logs mysql
fi

echo "🎉 部署完成！"
echo "📱 前端访问地址: http://$(curl -s ifconfig.me)"
echo "🔧 后端API地址: http://$(curl -s ifconfig.me):8080/api"
echo "🗄️  数据库端口: $(curl -s ifconfig.me):3306"

# 显示容器状态
echo ""
echo "📋 容器状态："
docker-compose ps

echo ""
echo "📋 查看实时日志："
echo "docker-compose logs -f"

echo ""
echo "🔧 常用命令："
echo "停止服务: docker-compose down"
echo "重启服务: docker-compose restart"
echo "查看日志: docker-compose logs -f [service_name]"
echo "进入容器: docker-compose exec [service_name] bash" 