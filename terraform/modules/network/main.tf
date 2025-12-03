resource "aws_security_group" "sgScorebanking" {
  name        = var.security_group_name
  vpc_id      = var.vpc_id

  dynamic "ingress" {
    for_each = [22, 4200, 8080]

    content {
      description = "Allow port ${ingress.value}"
      from_port   = ingress.value
      to_port     = ingress.value
      protocol    = "tcp"
      cidr_blocks = ["0.0.0.0/0"]
    }
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = var.security_group_name
  }
}

resource "aws_subnet" "scorebanking_subnet" {
  vpc_id            = var.vpc_id
  cidr_block        = "172.30.133.0/24"
  availability_zone = "us-east-1a"

  tags = {
    Name = var.subnet_name
  }
}



