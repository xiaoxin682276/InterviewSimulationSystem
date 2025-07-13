# 数据库设置指南

## 问题描述
错误信息显示：`Unknown database 'interview_system'`

这是因为数据库还没有创建。请按照以下步骤解决：

## 解决方案

### 方法1：使用MySQL Workbench（推荐）

1. **打开MySQL Workbench**
2. **连接到您的MySQL服务器**
3. **创建数据库**，执行以下SQL：
   ```sql
   CREATE DATABASE IF NOT EXISTS interview_system 
   CHARACTER SET utf8mb4 
   COLLATE utf8mb4_unicode_ci;
   ```
4. **选择数据库**：
   ```sql
   USE interview_system;
   ```
5. **执行初始化脚本**，复制 `init-database.sql` 文件中的所有内容并执行

### 方法2：使用phpMyAdmin

1. **打开phpMyAdmin**
2. **点击"新建"创建数据库**
3. **数据库名称**：`interview_system`
4. **字符集**：选择 `utf8mb4_unicode_ci`
5. **点击"创建"**
6. **选择新创建的数据库**
7. **点击"SQL"标签**
8. **复制并执行** `init-database.sql` 文件中的内容

### 方法3：使用命令行（如果MySQL在PATH中）

```bash
# 方法A：直接执行SQL文件
mysql -u root -p < init-database.sql

# 方法B：手动执行
mysql -u root -p
```

然后在MySQL命令行中执行：
```sql
CREATE DATABASE IF NOT EXISTS interview_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE interview_system;
-- 然后复制init-database.sql的内容并执行
```

### 方法4：使用Navicat或其他MySQL客户端

1. **连接到MySQL服务器**
2. **创建新数据库**：`interview_system`
3. **选择该数据库**
4. **执行** `init-database.sql` 文件中的SQL语句

## 验证数据库创建

执行以下SQL查询验证：

```sql
-- 查看所有数据库
SHOW DATABASES;

-- 使用数据库
USE interview_system;

-- 查看所有表
SHOW TABLES;

-- 查看用户数据
SELECT * FROM users;
```

## 重新启动后端服务

数据库创建完成后，重新启动后端服务：

```bash
cd backend
mvn spring-boot:run
```

## 预期结果

如果一切正常，您应该看到：

1. **后端启动成功**，没有数据库错误
2. **测试账号可用**：
   - 用户名：`admin`, `testuser`, `zhangsan`, `lisi`, `wangwu`
   - 密码：`password`

## 常见问题

### 问题1：MySQL连接失败
**解决**：检查MySQL服务是否启动
```bash
# Windows
net start mysql

# 或者检查服务状态
services.msc
```

### 问题2：权限不足
**解决**：使用有权限的用户账号
```sql
-- 创建用户并授权（如果需要）
CREATE USER 'interview_user'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON interview_system.* TO 'interview_user'@'localhost';
FLUSH PRIVILEGES;
```

### 问题3：字符集问题
**解决**：确保使用utf8mb4字符集
```sql
ALTER DATABASE interview_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## 快速检查清单

- [ ] MySQL服务正在运行
- [ ] 数据库 `interview_system` 已创建
- [ ] 所有表已创建（users, interview_sessions, interview_questions, evaluation_results）
- [ ] 测试用户数据已插入
- [ ] 后端服务重新启动
- [ ] 前端可以正常访问

## 测试连接

创建数据库后，可以使用以下命令测试：

```bash
# 测试数据库连接
mysql -u root -p -e "USE interview_system; SELECT COUNT(*) FROM users;"
```

如果返回数字（应该是5），说明数据库设置成功！ 