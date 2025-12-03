output "instance_public_ip" {
  description = "Ip rodando o serviço:"
  value       = module.ec2.instance_public_ip
}

output "app_url" {
  description = "Ip do app rodando angular:"
  value       = module.ec2.app_url
}