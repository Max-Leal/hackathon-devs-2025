output "scorebanking_subnet_id" {
  value = aws_subnet.scorebanking_subnet.id
}

output "sgScorebanking_id" {
  value = aws_security_group.sgScorebanking.id
}