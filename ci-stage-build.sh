#!/bin/bash

### Start of RockerChat Message ###

curl -X POST -H 'Content-Type: application/json' --data '
{
"text": "🤘 Начата сборка Stage Auth-Social, следить за ходом сборки можно тут - https://git.cross-market.com/crossmarket/websocket-kotlin/pipelines 🤘"
}
' https://chat.tokmaster.com/hooks/wKf7c5siAG26JtsoR/7dNThTFYN3zBN9qGGJ5MdiGoDaA7B2RCmEQnbE4GaeQmopES

### End of RockerChat Message ###

### Start of Telegam Message ###

message="🤘 Начата сборка Stage Auth-Social, следить за ходом сборки можно тут - https://git.cross-market.com/crossmarket/websocket-kotlin/pipelines 🤘"
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
TAG="${1:-stage}"
docker build --no-cache --rm -t "${IMAGE}:${TAG}" .
docker push "${IMAGE}:${TAG}"

curl https://hooks.stage2.cross-market.com/websocketktupdate
