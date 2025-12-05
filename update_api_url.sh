#!/bin/bash

if [ -z "$1" ]; then
    echo "🚨 ERRO CRÍTICO: O DNS/URL da API do Backend (ALB) é obrigatório."
    echo "Uso: $0 <URL_COMPLETA_ALB>"
    exit 1
fi

REPLACE_URL="$1" 
SEARCH_STRING="http://localhost:8080"

# Prepara as strings para uso seguro no comando 'sed'
SEARCH_ESCAPED=$(echo "$SEARCH_STRING" | sed 's/\//\\\//g')
REPLACE_ESCAPED=$(echo "$REPLACE_URL" | sed 's/\//\\\//g')

echo "✅ URL do ALB fornecida: $REPLACE_URL"
echo "🔄 Substituindo: $SEARCH_STRING"

FILES_TO_CHECK=$(find . -type f \( -name "*.ts" -o -name "*.js" -o -name "*.html" \) -not -path "*/node_modules/*" -not -path "*/.git/*" -print)

if [ -z "$FILES_TO_CHECK" ]; then
    echo "⚠️ Nenhum arquivo encontrado para substituição. Verifique o caminho."
    exit 0
fi

ENCONTRADO=0

for FILE in $FILES_TO_CHECK; do
    if grep -q "$SEARCH_STRING" "$FILE"; then
        echo "📝 Alterando: $FILE"
        sed -i "s/$SEARCH_ESCAPED/$REPLACE_ESCAPED/g" "$FILE"
        ENCONTRADO=1
    fi
done

if [ "$ENCONTRADO" -eq 1 ]; then
    echo "🎉 Substituição concluída com sucesso! Backend do Frontend configurado para o ALB."
else
    echo "⚠️ Placeholder de busca ($SEARCH_STRING) não encontrado em nenhum arquivo."
fi