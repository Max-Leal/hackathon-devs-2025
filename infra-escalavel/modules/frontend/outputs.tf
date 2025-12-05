# modules/frontend/outputs.tf

output "cloudfront_domain_name" {
  description = "The domain name of the CloudFront distribution (Endpoint público do Frontend)."
  value       = aws_cloudfront_distribution.s3_distribution.domain_name
}

output "s3_bucket_name" {
  description = "The name of the S3 bucket."
  value       = aws_s3_bucket.frontend_bucket.id
}