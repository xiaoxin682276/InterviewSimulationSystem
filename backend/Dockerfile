# 使用多阶段构建
FROM maven:3.9.6-eclipse-temurin-21 AS build

# 设置工作目录
WORKDIR /app

# 复制pom.xml
COPY pom.xml .

# 下载依赖
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 构建应用
RUN mvn clean package -DskipTests

# 运行阶段
FROM eclipse-temurin:21-jre-alpine

# 设置工作目录
WORKDIR /app

# 复制构建的jar文件
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8080

# 设置JVM参数
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 