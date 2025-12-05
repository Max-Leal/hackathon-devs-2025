# modules/frontend/variables.tf

variable "alb_dns_name" {
  description = "The DNS Name of the Application Load Balancer to be used as the Backend URL."
  type        = string
}

variable "domain_name" {
  description = "Domain name for the CloudFront distribution (opcional, se usar domínio próprio)."
  type        = string
  default     = ""
}