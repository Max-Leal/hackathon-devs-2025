variable "aws_region" {
  type    = string
  default = "us-east-1"
}

# ------------------------------------------------
# network variables
# ------------------------------------------------

variable "security_group_name" {
  type = string
}

variable "vpc_id" {
  type = string
}

variable "subnet_name" {
  type = string
}
# ------------------------------------------------
# ec2 variables
# ------------------------------------------------

variable "instance_type" {
  type = string
}

variable "ami_id" {
  type = string
}

variable "instance_name" {
  type = string
}