variable "instance_type" {
  type = string
}

variable "ami_id" {
  type = string
}

variable "instance_name" {
  type = string
}

# network

variable "vpc_security_group_id" {
  type = string
}

variable "subnet_id" {
  type = string
}