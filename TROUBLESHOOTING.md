# 故障排除指南

## 注册失败网络问题排查

### 1. 检查后端服务状态

#### 1.1 确认后端服务是否启动

```bash
# 检查8080端口是否被占用
netstat -ano | findstr :8080

# 或者使用
lsof -i :8080
```

#### 1.2 启动后端服务

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

**预期输出：**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.7.0)

2024-01-01 12:00:00.000  INFO 1234 --- [main] c.i.b.BackendApplication : Starting BackendApplication...
2024-01-01 12:00:00.100  INFO 1234 --- [main] c.i.b.BackendApplication : Started BackendApplication...
```

### 2. 检查数据库连接

#### 2.1 确认MySQL服务状态

```bash
# Windows
net start mysql

# Linux/Mac
sudo systemctl status mysql
```

#### 2.2 测试数据库连接

```bash
mysql -u root -p
```

输入密码后，执行：
```sql
SHOW DATABASES;
USE interview_system;
SHOW TABLES;
```

#### 2.3 检查数据库配置

确认 `backend/src/main/resources/application.yml` 中的配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/interview_system?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 123456  # 确保这是正确的密码
```

### 3. 检查前端连接

#### 3.1 确认前端服务状态

```bash
cd frontend
npm start
```

**预期输出：**
```
Compiled successfully!

You can now view interview-simulation-system in the browser.

  Local:            http://localhost:3000
  On Your Network:  http://192.168.1.100:3000
```

#### 3.2 测试API连接

在浏览器控制台中执行：

```javascript
// 测试后端连接
fetch('http://localhost:8080/api/auth/check-auth?username=test')
  .then(response => response.json())
  .then(data => console.log('API测试结果:', data))
  .catch(error => console.error('API连接失败:', error));
```

### 4. 常见错误及解决方案

#### 4.1 错误：Failed to fetch

**原因：** 后端服务未启动或端口不匹配

**解决方案：**
1. 确认后端服务在8080端口运行
2. 检查防火墙设置
3. 尝试访问 `http://localhost:8080` 确认服务可用

#### 4.2 错误：HTTP 500 Internal Server Error

**原因：** 服务器内部错误

**解决方案：**
1. 检查后端日志
2. 确认数据库连接正常
3. 检查数据库表是否存在

#### 4.3 错误：HTTP 400 Bad Request

**原因：** 请求参数错误

**解决方案：**
1. 检查表单数据格式
2. 确认所有必填字段都已填写
3. 检查邮箱格式是否正确

#### 4.4 错误：用户名已存在

**原因：** 数据库中已存在相同用户名

**解决方案：**
1. 使用不同的用户名
2. 或者删除数据库中的重复用户：

```sql
DELETE FROM users WHERE username = '重复的用户名';
```

### 5. 调试步骤

#### 5.1 启用详细日志

在 `backend/src/main/resources/application.yml` 中添加：

```yaml
logging:
  level:
    com.interview: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

#### 5.2 检查浏览器网络请求

1. 打开浏览器开发者工具 (F12)
2. 切换到 Network 标签
3. 尝试注册，观察网络请求
4. 检查请求和响应的详细信息

#### 5.3 检查后端日志

```bash
# 查看实时日志
tail -f backend/logs/application.log

# 或者查看控制台输出
```

### 6. 快速测试

#### 6.1 使用curl测试API

```bash
# 测试注册API
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser123",
    "password": "password123",
    "email": "test123@example.com",
    "fullName": "测试用户"
  }'

# 测试登录API
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password"
  }'
```

#### 6.2 使用Postman测试

1. 打开Postman
2. 创建POST请求到 `http://localhost:8080/api/auth/register`
3. 设置Content-Type为application/json
4. 添加请求体：

```json
{
  "username": "testuser123",
  "password": "password123",
  "email": "test123@example.com",
  "fullName": "测试用户"
}
```

### 7. 环境检查清单

- [ ] MySQL服务正在运行
- [ ] 数据库 `interview_system` 已创建
- [ ] 数据库表已创建（执行schema.sql）
- [ ] 后端服务在8080端口运行
- [ ] 前端服务在3000端口运行
- [ ] 防火墙允许8080端口访问
- [ ] 数据库连接配置正确

### 8. 重置系统

如果问题持续存在，可以尝试重置系统：

```bash
# 1. 停止所有服务
# 2. 删除并重新创建数据库
mysql -u root -p
DROP DATABASE interview_system;
CREATE DATABASE interview_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE interview_system;
SOURCE backend/src/main/resources/schema.sql;

# 3. 重新启动服务
cd backend && mvn spring-boot:run
cd frontend && npm start
```

### 9. 获取帮助

如果问题仍然存在，请提供以下信息：

1. 错误消息的完整内容
2. 浏览器控制台的错误信息
3. 后端服务的日志输出
4. 操作系统和版本信息
5. 数据库版本信息 