#!/bin/bash

APP_PATH="/home/ubuntu/app"
cd $APP_PATH

echo ">>> 현재 실행 중인 Docker 컨테이너 중지 및 삭제"
docker-compose down

echo ">>> 새로운 이미지 빌드 및 컨테이너 실행"
docker-compose up --build -d

echo ">>> 배포 완료!"