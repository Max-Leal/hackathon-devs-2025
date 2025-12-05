# modules/backend/main.tf
# --- 1. Application Load Balancer (ALB) ---
resource "aws_lb" "alb" {
  name               = "scorebanking-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [var.sg_alb_id]
  subnets            = var.public_subnet_ids

  tags = {
    Name = "scorebanking-alb"
  }
}

# Target Group: Define a porta e o health check para onde o ALB enviará o tráfego
resource "aws_lb_target_group" "tg" {
  name        = "hackathon-tg"
  port        = var.java_api_port # Porta da API Java (8080)
  protocol    = "HTTP"
  vpc_id      = data.aws_vpc.selected.id # Usaremos um data source para obter o VPC ID do SG
  target_type = "instance"

  health_check {
    # Altere o caminho se sua API tiver um endpoint de status (ex: /health)
    path                = "/"
    protocol            = "HTTP"
    port                = var.java_api_port
    healthy_threshold   = 2
    unhealthy_threshold = 2
    timeout             = 5
    interval            = 30
  }
}

# Listener: Escuta na porta 80 e encaminha para o Target Group
resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.alb.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.tg.arn
  }
}

# Data Source: Usado para obter o VPC ID, necessário no Target Group
data "aws_vpc" "selected" {
  id = data.aws_security_group.backend_sg_info.vpc_id
}
data "aws_security_group" "backend_sg_info" {
  id = var.sg_backend_id
}

# --- 2. Launch Template (Planta da EC2) ---

resource "aws_launch_template" "backend_lt" {
  name_prefix   = "backend-lt"
  image_id      = var.ami_id
  instance_type = var.instance_type
  key_name      = var.key_name

  network_interfaces {
    associate_public_ip_address = false # Fica na subnet privada, sem IP público
    security_groups             = [var.sg_backend_id]
  }

  user_data = base64encode(templatefile("${path.module}/user_data.sh", {
    db_ip = var.db_private_ip
  }))

  tag_specifications {
    resource_type = "instance"
    tags = {
      Name = "scorebanking-backend"
    }
  }
}

# --- 3. Auto Scaling Group (ASG) ---

resource "aws_autoscaling_group" "backend_asg" {
  name             = "backend-asg-scorebanking"
  min_size         = var.min_size
  max_size         = var.max_size
  desired_capacity = var.min_size

  # Instâncias devem ser criadas nas subnets privadas
  vpc_zone_identifier = var.private_subnet_ids

  # Associa o ASG ao Target Group do ALB
  target_group_arns = [aws_lb_target_group.tg.arn]

  launch_template {
    id      = aws_launch_template.backend_lt.id
    version = aws_launch_template.backend_lt.latest_version
  }

  lifecycle {
    create_before_destroy = true
  }

  tag {
    key                 = "Name"
    value               = "scorebanking-backend"
    propagate_at_launch = true
  }
}