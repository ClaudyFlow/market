# 多阶段构建 - Spring Boot 应用
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# 复制 pom.xml 并下载依赖（利用 Docker 缓存层）
COPY backend/pom.xml ./pom.xml
RUN mvn dependency:go-offline -B

# 复制源代码并构建
COPY backend/src ./src
RUN mvn clean package -DskipTests -B

# 运行阶段
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN apk add --no-cache wget

COPY --from=builder /build/target/*.jar app.jar

RUN mkdir -p /app/logs

EXPOSE 8080

ENV JAVA_OPTS="-Xms512m -Xmx1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
