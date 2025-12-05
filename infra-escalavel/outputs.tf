output "application_url" {
  description = "The public CloudFront domain name to access the Angular Frontend."
  value       = "https://${module.frontend.cloudfront_domain_name}"
}

output "backend_api_url" {
  description = "The public DNS name of the Application Load Balancer for API calls. Use this URL in the Frontend's configuration."
  value       = "http://${module.backend.alb_dns_name}"
}


output "s3_bucket_for_frontend_upload" {
  description = "S3 Bucket name where compiled Angular files must be uploaded using 'aws s3 sync'."
  value       = module.frontend.s3_bucket_name
}

output "database_private_ip" {
  description = "The Private IP address of the Database EC2 instance (used by the Backend ASG for connection)."
  value       = module.database.db_private_ip
}