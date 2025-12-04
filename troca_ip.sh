#!/bin/bash

if command -v hostname &> /dev/null && hostname -I &> /dev/null; then
    IP_LOCAL=$(hostname -I | awk '{print $1}')
elif command -v ip &> /dev/null; then

    IP_LOCAL=$(ip a | grep 'inet ' | grep -v 127.0.0.1 | awk '{print $2}' | cut -d/ -f1 | head -n 1)
elif command -v ifconfig &> /dev/null; then

    IP_LOCAL=$(ifconfig | grep 'inet ' | grep -v 127.0.0.1 | awk '{print $2}' | head -n 1)
else
    echo "ERRO: Não foi possível determinar o IP local. Verifique se 'hostname', 'ip' ou 'ifconfig' estão instalados."
    exit 1
fi

SEARCH_STRING="http:\/\/localhost:8080"
REPLACE_STRING="http:\/\/$IP_LOCAL:8080"

FILES_TO_CHECK=$(find . -type f \( -name "*.ts" -o -name "*.js" -o -name "*.html" \) -print)

if [ -z "$IP_LOCAL" ]; then
    echo "🚨 ERRO: O IP local não foi encontrado. Verifique sua conexão de rede."
    exit 1
fi

echo "IP Local Encontrado: $IP_LOCAL"
echo "Iniciando a substituição de $SEARCH_STRING por $REPLACE_STRING..."


for FILE in $FILES_TO_CHECK; do

    if [[ "$OSTYPE" == "darwin"* ]]; then
        sed -i '' "s/$SEARCH_STRING/$REPLACE_STRING/g" "$FILE"
    else
        sed -i.bak "s/$SEARCH_STRING/$REPLACE_STRING/g" "$FILE"
    fi
done

echo "Substituição concluída!"