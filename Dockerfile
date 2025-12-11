FROM bellsoft/liberica-openjdk-alpine:17
WORKDIR /app

# JAR은 로컬에서 build/libs/*.jar 으로 생성해서 이 위치로 복사해야 함
COPY build/libs/*.jar app.jar

VOLUME /tmp
VOLUME /logs
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
