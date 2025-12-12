FROM bellsoft/liberica-openjdk-alpine:17 AS build
WORKDIR /app
COPY . .

RUN apk add --no-cache bash
RUN chmod +x gradlew && ./gradlew --version && ./gradlew clean build -x test --stacktrace

FROM bellsoft/liberica-openjdk-alpine:17
WORKDIR /app
COPY --from=build /app/build/libs/api-0.0.1-SNAPSHOT.jar app.jar

VOLUME /tmp
VOLUME /logs
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]