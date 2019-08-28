#!/bin/bash

### Start of Telegam Message ###

message="🆘 Сборка Auth-Social Demo неудачна 🆘"
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