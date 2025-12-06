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

# Target Group
resource "aws_lb_target_group" "tg" {
  name        = "hackathon-tg"
  port        = var.java_api_port
  protocol    = "HTTP"
  vpc_id      = data.aws_vpc.selected.id
  target_type = "instance"

  health_check {
    path                = "/actuator/health" 
    protocol            = "HTTP"
    port                = var.java_api_port
    healthy_threshold   = 2
    unhealthy_threshold = 2
    timeout             = 5
    interval            = 30
  }
}

# Listener
resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.alb.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.tg.arn
  }
}

# Data Source
data "aws_vpc" "selected" {
  id = data.aws_security_group.backend_sg_info.vpc_id
}
data "aws_security_group" "backend_sg_info" {
  id = var.sg_backend_id
}

# --- 2. Launch Template ---

resource "aws_launch_template" "backend_lt" {
  name_prefix   = "backend-lt"
  image_id      = var.ami_id
  instance_type = var.instance_type
  key_name      = var.key_name

  network_interfaces {
    associate_public_ip_address = false # Subnet Privada
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

  vpc_zone_identifier = var.private_subnet_ids
  target_group_arns   = [aws_lb_target_group.tg.arn]

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