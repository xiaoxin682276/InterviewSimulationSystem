# Ubuntu VPS 部署指南

## 1. 上传项目到VPS

### 方法一：使用Git（推荐）
```bash
# 在VPS上执行
git clone <您的项目仓库地址>
cd InterviewSimulationSystem
```

### 方法二：使用SCP上传
```bash
# 在本地打包
tar -czf interview-system.tar.gz InterviewSimulationSystem/

# 上传到VPS
scp interview-system.tar.gz root@your-vps-ip:/root/

# 在VPS上解压
ssh root@your-vps-ip
cd /root
tar -xzf interview-system.tar.gz
cd InterviewSimulationSystem
```

## 2. 执行部署

```bash
# 给脚本执行权限
chmod +x deploy-ubuntu.sh

# 执行部署
./deploy-ubuntu.sh
```

## 3. 配置域名（可选）

### 修改Nginx配置
编辑 `frontend/nginx.conf`：
```nginx
server {
    listen 80;
    server_name your-domain.com;  # 改为您的域名
    # ... 其他配置
}
```

### 重新部署
```bash
docker-compose down
docker-compose up --build -d
```

## 4. 常用管理命令

```bash
# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 重启服务
docker-compose restart

# 停止服务
docker-compose down

# 更新部署
docker-compose down
docker-compose up --build -d
```

## 5. 防火墙配置

确保VPS防火墙开放必要端口：
```bash
# Ubuntu UFW
sudo ufw allow 80
sudo ufw allow 443
sudo ufw allow 8080

# 或者使用iptables
sudo iptables -A INPUT -p tcp --dport 80 -j ACCEPT
sudo iptables -A INPUT -p tcp --dport 443 -j ACCEPT
sudo iptables -A INPUT -p tcp --dport 8080 -j ACCEPT
```

## 6. SSL证书配置（推荐）

```bash
# 安装certbot
sudo apt update
sudo apt install certbot python3-certbot-nginx

# 获取证书
sudo certbot --nginx -d your-domain.com

# 自动续期
sudo crontab -e
# 添加：0 12 * * * /usr/bin/certbot renew --quiet
```

## 7. 访问地址

- 前端：http://your-vps-ip 或 https://your-domain.com
- 后端API：http://your-vps-ip:8080/api
- 数据库：your-vps-ip:3306（仅内网访问）

## 8. 故障排除

### 查看容器日志
```bash
# 查看所有服务日志
docker-compose logs

# 查看特定服务日志
docker-compose logs frontend
docker-compose logs backend
docker-compose logs mysql
```

### 进入容器调试
```bash
# 进入前端容器
docker-compose exec frontend sh

# 进入后端容器
docker-compose exec backend bash

# 进入数据库容器
docker-compose exec mysql mysql -u interview -p
```

### 检查网络连接
```bash
# 检查端口是否开放
netstat -tlnp | grep :80
netstat -tlnp | grep :8080

# 检查防火墙状态
sudo ufw status
``` 