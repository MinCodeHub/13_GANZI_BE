# 1. 베이스 이미지 설정
FROM bellsoft/liberica-openjdk-alpine:17

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. GitHub Actions에서 이미 빌드되어 넘어온 jar 파일을 복사
# 현재 폴더에 있는 api-0.0.1-SNAPSHOT.jar를 컨테이너 안의 app.jar로 복사
COPY api-0.0.1-SNAPSHOT.jar app.jar

# 4. 볼륨 및 포트 설정
VOLUME /tmp
VOLUME /logs
EXPOSE 8080

# 5. 실행 명령
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]