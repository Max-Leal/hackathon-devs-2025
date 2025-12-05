# modules/database/variables.tf

variable "ami_id" {
  description = "The AMI ID for the PostgreSQL EC2 instance (e.g., Ubuntu or Amazon Linux 2)."
  type        = string
}

variable "instance_type" {
  description = "The instance type for the database EC2 (e.g., t3.micro or t3.small)."
  type        = string
  default     = "t3.micro"
}

variable "private_subnet_ids" {
  description = "List of Private Subnet IDs where the DB can be launched."
  type        = list(string)
}

variable "sg_database_id" {
  description = "The Security Group ID for the DB instance."
  type        = string
}

variable "db_name" {
  description = "Name of the initial database."
  type        = string
  default     = "scorebanking_db"
}

variable "key_name" {
  description = "The key pair name for SSH access to the EC2 instance."
  type        = string
}