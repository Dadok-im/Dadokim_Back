# 1. 빌드 스테이지 (Gradle)
FROM gradle:8.10.2-jdk17 AS builder
WORKDIR /workspace
COPY --chown=gradle:gradle . .
RUN gradle build -x test --no-daemon

# 2. 실행 스테이지 (ARM 지원 이미지 사용)
# eclipse-temurin:17-jdk-jammy 는 slim 기반 Ubuntu 22.04 (Jammy), ARM 지원 O
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=builder /workspace/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
