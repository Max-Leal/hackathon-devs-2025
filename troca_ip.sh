#!/bin/bash
echo "Obtendo IP Público..."
IP_PUBLICO=$(curl -s ifconfig.me)

if [ -z "$IP_PUBLICO" ]; then
    echo "⚠️ Falha ao obter de ifconfig.me, tentando AWS..."
    IP_PUBLICO=$(curl -s checkip.amazonaws.com)
fi

if [ -z "$IP_PUBLICO" ]; then
    echo "🚨 ERRO CRÍTICO: Não foi possível obter o IP Público. Verifique a internet."
    exit 1
fi

echo "✅ IP Público detectado: $IP_PUBLICO"

SEARCH_STRING="http:\/\/localhost:8080"
REPLACE_STRING="http:\/\/$IP_PUBLICO:8080"

echo "🔄 Substituindo: $SEARCH_STRING"
echo "👉 Por:          $REPLACE_STRING"

FILES_TO_CHECK=$(find . -type f \( -name "*.ts" -o -name "*.js" -o -name "*.html" \) -not -path "*/node_modules/*" -not -path "*/.git/*" -print)

if [ -z "$FILES_TO_CHECK" ]; then
    echo "⚠️ Nenhum arquivo encontrado para substituição."
else

    for FILE in $FILES_TO_CHECK; do

        if grep -q "http://localhost:8080" "$FILE"; then
            echo "📝 Alterando: $FILE"
            
            sed -i "s/$SEARCH_STRING/$REPLACE_STRING/g" "$FILE"
        fi
    done
    echo "🎉 Substituição concluída com sucesso!"
fi