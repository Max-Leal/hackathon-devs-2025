#!/bin/bash

if [ -z "$1" ]; then
    echo "ERRO CRÍTICO: O IP privado do Banco de Dados é obrigatório."
    echo "Uso: $0 <IP_PRIVADO_DB>"
    exit 1
fi

DB_PRIVATE_IP="$1"
CONFIG_FILE="./src/main/resources/application.properties"

SEARCH_STRING="jdbc:postgresql://DB_PLACEHOLDER_IP:5432"
REPLACE_STRING="jdbc:postgresql://$DB_PRIVATE_IP:5432"

SEARCH_ESCAPED=$(echo "$SEARCH_STRING" | sed 's/\//\\\//g')
REPLACE_ESCAPED=$(echo "$REPLACE_STRING" | sed 's/\//\\\//g')

if [ -f "$CONFIG_FILE" ]; then
    echo "IP Privado do DB detectado: $DB_PRIVATE_IP"
    echo "Injetando no arquivo: $CONFIG_FILE"
    
    sed -i "s/$SEARCH_ESCAPED/$REPLACE_ESCAPED/g" "$CONFIG_FILE"
    
    if [ $? -eq 0 ]; then
        echo "🎉 Substituição do IP do DB concluída com sucesso!"
    else
        echo "Falha na substituição do IP do DB via sed."
    fi
else
    echo "ERRO: Arquivo de configuração do Java ($CONFIG_FILE) não encontrado."
    exit 1
fi