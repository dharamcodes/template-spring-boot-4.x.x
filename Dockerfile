FROM amazoncorretto:25 AS builder
WORKDIR /app
RUN dnf install -y findutils && dnf clean all
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon || true
COPY src ./src
RUN ./gradlew bootJar -x test --no-daemon
FROM amazoncorretto:25
WORKDIR /app
COPY --from=builder /app/build/libs/tragefox-api-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]