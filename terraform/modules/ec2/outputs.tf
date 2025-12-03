output "instance_public_ip" {
  value       = aws_instance.instance-scorebanking.public_ip
}

output "app_url" {
  value       = "APP: http://${aws_instance.instance-scorebanking.public_ip}:8080"
}