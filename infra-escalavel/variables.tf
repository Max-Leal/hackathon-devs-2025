# variables.tf (na raiz do seu projeto)

variable "region" {
  description = "AWS region to deploy resources."
  type        = string
  default     = "us-east-1"
}

variable "existing_vpc_id" {
  description = "The ID of the existing VPC provided by the course."
  type        = string
}

variable "ami_id" {
  description = "The AMI ID for the EC2 instances (Backend/Database)."
  type        = string
}

variable "key_name" {
  description = "The name of the SSH key pair registered in AWS."
  type        = string
}

variable "db_instance_type" {
  description = "The instance type for the Database EC2 (e.g., t3.micro)."
  type        = string
  default     = "t3.micro"
}

# Variáveis do Backend (ASG)
variable "backend_instance_type" {
  description = "The instance type for the Backend EC2 instances (e.g., t3.small)."
  type        = string
  default     = "t3.small"
}

variable "backend_min_capacity" {
  description = "Minimum number of Backend EC2 instances in the Auto Scaling Group."
  type        = number
  default     = 1
}

variable "backend_max_capacity" {
  description = "Maximum number of Backend EC2 instances in the Auto Scaling Group."
  type        = number
  default     = 4
}