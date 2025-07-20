# 域名配置指南

## 1. 修改前端Nginx配置

编辑 `frontend/nginx.conf`，将 `server_name` 改为您的域名：

```nginx
server {
    listen 80;
    server_name your-domain.com;  # 改为您的域名
    # ... 其他配置
}
```

## 2. 修改Docker Compose配置

编辑 `docker-compose.yml`，添加域名环境变量：

```yaml
frontend:
  build:
    context: ./frontend
    dockerfile: Dockerfile
  container_name: interview-frontend
  restart: unless-stopped
  ports:
    - "80:80"
  environment:
    - DOMAIN=your-domain.com  # 添加您的域名
  depends_on:
    - backend
  networks:
    - interview-network
```

## 3. SSL证书配置（推荐）

### 使用Let's Encrypt免费证书：

```bash
# 安装certbot
sudo apt-get update
sudo apt-get install certbot python3-certbot-nginx

# 获取证书
sudo certbot --nginx -d your-domain.com

# 自动续期
sudo crontab -e
# 添加：0 12 * * * /usr/bin/certbot renew --quiet
```

### 修改Nginx配置支持HTTPS：

```nginx
server {
    listen 443 ssl;
    server_name your-domain.com;
    
    ssl_certificate /etc/letsencrypt/live/your-domain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/your-domain.com/privkey.pem;
    
    # ... 其他配置
}

server {
    listen 80;
    server_name your-domain.com;
    return 301 https://$server_name$request_uri;
}
```

## 4. 部署命令

```bash
# 给部署脚本执行权限
chmod +x deploy.sh

# 执行部署
./deploy.sh
```

## 5. 访问地址

- 前端：https://your-domain.com
- 后端API：https://your-domain.com/api
- 数据库：your-domain.com:3306（仅内网访问）

## 6. 监控和维护

```bash
# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 重启服务
docker-compose restart

# 更新部署
docker-compose down
docker-compose up --build -d
``` 