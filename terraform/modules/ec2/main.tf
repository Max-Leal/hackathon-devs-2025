resource "aws_instance" "instance-scorebanking" {

  ami                         = var.ami_id
  instance_type               = var.instance_type
  subnet_id                   = var.subnet_id
  vpc_security_group_ids      = [var.vpc_security_group_id]
  associate_public_ip_address = true
  key_name                    = "scorebanking-key"
  user_data = base64encode(<<-EOF
    #!/bin/bash
    apt-get update
    apt install -y docker.io git build-essential
    systemctl start docker
    systemctl enable docker
    cd /root
    git clone https://github.com/Max-Leal/hackathon-devs-2025.git
    cd ./hackathon-devs-2025
    git checkout feature/terraform
    make docker-build
    make docker-run      
  EOF
  )

  tags = {
    Name = var.instance_name
  }
}