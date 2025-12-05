output "alb_dns_name" {
  description = "The DNS name of the Application Load Balancer."
  value       = aws_lb.alb.dns_name
}

output "alb_arn" {
  description = "The ARN of the Application Load Balancer."
  value       = aws_lb.alb.arn
}

output "target_group_arn" {
  description = "The ARN of the Target Group associated with the ALB."
  value       = aws_lb_target_group.tg.arn
}

output "backend_asg_name" {
  description = "The name of the Auto Scaling Group for the backend."
  value       = aws_autoscaling_group.backend_asg.name
}