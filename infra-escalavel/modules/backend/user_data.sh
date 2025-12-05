#!/bin/bash

apt-get update
apt install -y docker.io git build-essential docker-compose -y
systemctl start docker
systemctl enable docker

cd /root
git clone https://github.com/Max-Leal/hackathon-devs-2025.git
cd ./hackathon-devs-2025
git checkout feature/infrastructure

DB_PRIVATE_IP="${db_ip}"

if [ -f ./troca_db_ip.sh ]; then
    echo "Chamando script para injetar IP do DB..."
    chmod +x ./troca_db_ip.sh
    ./troca_db_ip.sh $DB_PRIVATE_IP
else
    echo "Warning: troca_db_ip.sh não encontrado. Verifique seu repositório."
fi

make docker-build
make docker-run