#!/bin/bash

set -e
set -x

### Start of Telegam Message ###

message="🤘 Начата сборка Demo Auth-Social, следить за ходом сборки можно тут - https://git.cross-market.com/crossmarket/auth-social/pipelines 🤘"
apiToken=724494167:AAGb_n0_TLd0sm_9nsA02NL8Bywb8AiFQMc
chatId=-354180350

send() {
  curl -s \
    -X POST \
    https://api.telegram.org/bot$apiToken/sendMessage \
    -d text="$message" \
    -d chat_id=$chatId
}

if [[ ! -z "$message" ]]; then
  send
fi

### End of Telegam Message ###

IMAGE="crossmarket/auth-social"
TAG="${1:-demo}"
docker build -f CI_CD/Dockerfiles/Dockerfile_build.demo --compress --no-cache -t "${IMAGE}:${TAG}" .
docker push "${IMAGE}:${TAG}"

sleep 5

curl https://hooks.demo.cross-market.com/auth-socialupdate-node1

sleep 15

curl https://hooks.demo.cross-market.com/auth-socialupdate-node2
