output "public_subnet_ids" {
  description = "List of Public Subnet IDs for ALB"
  value       = aws_subnet.public[*].id
}

output "private_subnet_ids" {
  description = "List of Private Subnet IDs for EC2s"
  value       = aws_subnet.private[*].id
}

output "sg_backend_id" {
  description = "The ID of the Backend Security Group"
  value       = aws_security_group.sg_backend.id
}

output "sg_database_id" {
  description = "The ID of the Database Security Group"
  value       = aws_security_group.sg_database.id
}

output "sg_alb_id" {
  description = "The ID of the ALB Security Group"
  value       = aws_security_group.sg_alb.id
}