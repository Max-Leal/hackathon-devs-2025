# modules/frontend/main.tf

resource "aws_s3_bucket" "frontend_bucket" {
  bucket = "scorebanking-frontend-${substr(md5(var.alb_dns_name), 0, 8)}"

  tags = {
    Name = "Scorebanking-Frontend-Bucket"
  }
}

resource "aws_s3_bucket_public_access_block" "block" {
  bucket = aws_s3_bucket.frontend_bucket.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

# 1. Recurso OAI (Origin Access Identity)
resource "aws_cloudfront_origin_access_identity" "oai" {
  comment = "OAI for S3 Frontend Bucket"
}

# 2. Política IAM (para dar acesso ao OAI)
data "aws_iam_policy_document" "s3_policy" {
  statement {
    actions   = ["s3:GetObject"]
    resources = ["${aws_s3_bucket.frontend_bucket.arn}/*"]
    principals {
      type        = "AWS"
      identifiers = [aws_cloudfront_origin_access_identity.oai.iam_arn]
    }
  }
  statement {
    actions   = ["s3:ListBucket"]
    resources = [aws_s3_bucket.frontend_bucket.arn]
    principals {
      type        = "AWS"
      identifiers = [aws_cloudfront_origin_access_identity.oai.iam_arn]
    }
  }
}

resource "aws_s3_bucket_policy" "s3_policy" {
  bucket = aws_s3_bucket.frontend_bucket.id
  policy = data.aws_iam_policy_document.s3_policy.json
}

# 3. CloudFront Distribution
resource "aws_cloudfront_distribution" "s3_distribution" {
  origin {
    domain_name = aws_s3_bucket.frontend_bucket.bucket_regional_domain_name
    origin_id   = aws_s3_bucket.frontend_bucket.id
    
    # CORREÇÃO AQUI: Referenciar o OAI ID para acesso ao S3 (método OAI)
    s3_origin_config {
      origin_access_identity = aws_cloudfront_origin_access_identity.oai.cloudfront_access_identity_path
    }
    
    # CORREÇÃO AQUI: Removemos custom_origin_config pois o S3 não é um endpoint HTTP(S) externo.
    # custom_origin_config { ... }
  }

  enabled             = true
  is_ipv6_enabled     = true
  comment             = "Frontend Angular App"
  default_root_object = "index.html"

  default_cache_behavior {
    allowed_methods  = ["GET", "HEAD"]
    cached_methods   = ["GET", "HEAD"]
    target_origin_id = aws_s3_bucket.frontend_bucket.id

    forwarded_values {
      query_string = false
      cookies {
        forward = "none"
      }
    }

    viewer_protocol_policy = "redirect-to-https"
    min_ttl                = 0
    default_ttl            = 3600
    max_ttl                = 86400
  }

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  viewer_certificate {
    cloudfront_default_certificate = true
  }

  tags = {
    Name = "scorebanking-frontend-cdn"
  }
}