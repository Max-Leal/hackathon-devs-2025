resource "aws_instance" "instance-scorebanking" {

  ami                         = var.ami_id
  instance_type               = var.instance_type
  subnet_id                   = var.subnet_id
  vpc_security_group_ids    = [var.vpc_security_group_id]
  associate_public_ip_address = true
  key_name                    = "scorebanking-key"
  user_data = base64encode(<<-EOF
    #!/bin/bash
    apt-get update
    apt install -y docker.io git build-essential makefile
    systemctl start docker
    systemctl enable docker
    docker container stop jewelry-app 2> /dev/null
    cd /root
    rm -rf jewelry-devops-exercise/
    git clone https://github.com/Max-Leal/jewelry-devops-exercise.git
    cd ./jewelry-devops-exercise/modulo7-iac_tooling
    
    make docker-run      
  EOF
  )

  tags = {
    Name = var.instance_name
  }
}