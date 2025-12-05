variable "existing_vpc_id" {
  description = "The ID of the existing VPC to deploy resources into."
  type        = string
}

variable "public_subnet_cidrs" {
  description = "List of public subnet CIDRs (e.g., in different AZs)"
  type        = list(string)
  default     = ["172.30.162.0/24", "172.30.161.0/24"]
}

variable "private_subnet_cidrs" {
  description = "List of private subnet CIDRs (e.g., in different AZs)"
  type        = list(string)
  default     = ["172.30.158.0/24", "172.30.159.0/24"]
}

variable "java_api_port" {
  description = "Port used by the Java Backend API (e.g., 8080)"
  type        = number
  default     = 8080
}

variable "postgresql_port" {
  description = "Port used by the PostgreSQL Database (e.g., 5432)"
  type        = number
  default     = 5432
}