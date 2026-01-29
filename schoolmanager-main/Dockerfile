# --- Giai đoạn 1: Build dự án ---
# Sử dụng image Maven chính thức với JDK 21 để biên dịch code
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Thiết lập thư mục làm việc trong container
WORKDIR /app

# Copy file cấu hình Maven (pom.xml) trước để tận dụng cache của Docker
COPY pom.xml .

# Tải các thư viện (dependencies) về trước để tiết kiệm thời gian cho các lần build sau
RUN mvn dependency:go-offline

# Copy toàn bộ mã nguồn vào container
COPY src ./src

# Đóng gói dự án thành file .jar (bỏ qua Unit Test để không bị lỗi kết nối DB khi build)
RUN mvn clean package -DskipTests

# --- Giai đoạn 2: Runtime (Chạy ứng dụng) ---
# Sử dụng JRE 21 bản Alpine siêu nhẹ để giảm dung lượng file cuối cùng
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy file .jar đã build từ giai đoạn 1 sang môi trường chạy này
COPY --from=build /app/target/*.jar app.jar

# Cài đặt gói dữ liệu múi giờ để hệ thống chạy đúng giờ Việt Nam
RUN apk add --no-cache tzdata
ENV TZ=Asia/Ho_Chi_Minh

# Thông báo cổng mà ứng dụng Spring Boot sẽ lắng nghe (mặc định 8080)
EXPOSE 8080

# Lệnh khởi chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]