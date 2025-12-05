# modules/database/outputs.tf

output "db_private_ip" {
  description = "The Private IP address of the Database EC2 instance."
  value       = aws_instance.db_instance.private_ip
}

output "db_instance_id" {
  description = "The ID of the Database EC2 instance."
  value       = aws_instance.db_instance.id
}