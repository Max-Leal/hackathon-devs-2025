# modules/backend/variables.tf

variable "ami_id" {
  description = "The AMI ID for the Backend EC2 instances."
  type        = string
}

variable "instance_type" {
  description = "The instance type for the Backend EC2 instances (e.g., t3.micro)."
  type        = string
}

variable "key_name" {
  description = "The key pair name for SSH access."
  type        = string
}

variable "public_subnet_ids" {
  description = "List of Public Subnet IDs for the ALB."
  type        = list(string)
}

variable "private_subnet_ids" {
  description = "List of Private Subnet IDs for the ASG instances."
  type        = list(string)
}

variable "sg_backend_id" {
  description = "The Security Group ID for the Backend instances."
  type        = string
}

variable "sg_alb_id" {
  description = "The Security Group ID for the ALB."
  type        = string
}

variable "db_private_ip" {
  description = "The Private IP address of the Database EC2 instance."
  type        = string
}

variable "min_size" {
  description = "Minimum number of instances in the ASG."
  type        = number
}

variable "max_size" {
  description = "Maximum number of instances in the ASG."
  type        = number
}

variable "java_api_port" {
  description = "Port used by the Java Backend API (8080)."
  type        = number
  default     = 8080
}