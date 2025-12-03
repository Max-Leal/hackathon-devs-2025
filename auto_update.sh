#!/bin/bash
PROJECT_DIR="/root/hackathon-devs-2025"
BRANCH="develop"
LOG_FILE="/var/log/hackathon-update.log"

touch $LOG_FILE 2>/dev/null || LOG_FILE="$PROJECT_DIR/update.log"

log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1" >> "$LOG_FILE"
}

cd "$PROJECT_DIR" || { log "ERRO: Pasta $PROJECT_DIR não encontrada."; exit 1; }

log "Verificando atualizações..."
git fetch origin "$BRANCH"

# Pega os Hashes
LOCAL_HASH=$(git rev-parse HEAD)
REMOTE_HASH=$(git rev-parse "origin/$BRANCH")

# Compara
if [ "$LOCAL_HASH" != "$REMOTE_HASH" ]; then
    log "🚀 Alteração detectada! Atualizando de $LOCAL_HASH para $REMOTE_HASH..."

    # 1. Puxa o código
    if git pull origin "$BRANCH"; then
        log "Git pull realizado com sucesso."
    else
        log "ERRO ao fazer git pull. Verifique conflitos."
        exit 1
    fi

    # 2. Reconstrói os containers
    log "Reiniciando Docker Compose..."
    
    docker-compose down
    
    # Roda o build e sobe em background
    if docker-compose up -d --build; then
        log "Deploy realizado com sucesso!"
        
        # Limpeza opcional para economizar espaço em disco na AWS
        docker system prune -f
    else
        log "ERRO ao subir containers."
    fi

else
    log "Nenhuma alteração encontrada. Tudo atualizado."
fi