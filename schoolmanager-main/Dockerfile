# --- Giai đoạn 1: Build dự án ---
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# 1. Tối ưu Cache: Chỉ copy pom.xml và download thư viện trước
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 2. Copy source và build
COPY src ./src
RUN mvn clean package -DskipTests

# --- Giai đoạn 2: Runtime ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Cài đặt tzdata để đồng bộ múi giờ Việt Nam
RUN apk add --no-cache tzdata
ENV TZ=Asia/Ho_Chi_Minh

# Copy file jar từ stage build
COPY --from=build /app/target/*.jar app.jar

# QUAN TRỌNG: Render sử dụng port động hoặc mặc định là 10000. 
# Ta ép Spring Boot nghe theo biến môi trường PORT của Render.
ENV SERVER_PORT=10000
EXPOSE 10000

# Sử dụng mảng để truyền tham số, giúp Spring Boot nhận diện biến PORT từ Render
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-10000} -jar app.jar"]