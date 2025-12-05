# modules/database/main.tf

resource "aws_instance" "db_instance" {
  ami           = var.ami_id
  instance_type = var.instance_type

  subnet_id              = var.private_subnet_ids[0]
  vpc_security_group_ids = [var.sg_database_id]

  associate_public_ip_address = false

  key_name = var.key_name

  user_data = base64encode(<<-EOF
    #!/bin/bash

    apt-get update
    apt install -y docker.io build-essential docker-compose
    systemctl start docker
    systemctl enable docker

    cd /root
    git clone https://github.com/Max-Leal/hackathon-devs-2025.git
    cd ./hackathon-devs-2025
    git checkout main

    docker-compose up -d db
    echo "PostgreSQL container started."
  EOF
  ) // ARRUMAR PARA USAR O COMPOSE

  tags = {
    Name        = "scorebanking-database"
    Environment = "Hackathon"
  }
}