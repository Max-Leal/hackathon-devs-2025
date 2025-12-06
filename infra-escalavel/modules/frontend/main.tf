# modules/frontend/main.tf

# --- BUCKET S3 (Armazenamento do Frontend) ---
resource "aws_s3_bucket" "frontend_bucket" {
  # Gera um nome único curto usando hash do DNS do ALB
  bucket = "scorebanking-frontend-${substr(md5(var.alb_dns_name), 0, 8)}"
  force_destroy = true
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

# --- SEGURANÇA (OAI - Origin Access Identity) ---
resource "aws_cloudfront_origin_access_identity" "oai" {
  comment = "OAI for S3 Frontend Bucket"
}

# --- POLÍTICAS DE ACESSO AO BUCKET ---
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

# --- CLOUDFRONT DISTRIBUTION (CDN + Proxy Reverso) ---
resource "aws_cloudfront_distribution" "s3_distribution" {
  
  # 1. ORIGEM DO FRONTEND (S3)
  origin {
    domain_name = aws_s3_bucket.frontend_bucket.bucket_regional_domain_name
    origin_id   = "S3-Frontend" # ID para referência interna

    s3_origin_config {
      origin_access_identity = aws_cloudfront_origin_access_identity.oai.cloudfront_access_identity_path
    }
  }

  # 2. ORIGEM DO BACKEND (ALB - NOVO!)
  # Isso permite que o CloudFront fale com o Backend via HTTP interno,
  # resolvendo o problema de Mixed Content.
  origin {
    domain_name = var.alb_dns_name
    origin_id   = "ALB-Backend" # ID para referência interna

    custom_origin_config {
      http_port              = 80
      https_port             = 443
      origin_protocol_policy = "http-only" # CloudFront -> ALB via HTTP
      origin_ssl_protocols   = ["TLSv1.2"]
    }
  }

  enabled             = true
  is_ipv6_enabled     = true
  comment             = "Frontend Angular App + API Proxy"
  default_root_object = "index.html"

  # --- ROTA DA API (Proxy Reverso para o ALB) ---
  # Captura requisições para /auth/* e manda para o Backend
  ordered_cache_behavior {
    path_pattern     = "/auth/*" 
    allowed_methods  = ["DELETE", "GET", "HEAD", "OPTIONS", "PATCH", "POST", "PUT"]
    cached_methods   = ["GET", "HEAD"]
    target_origin_id = "ALB-Backend" # Aponta para a origem do ALB definida acima

    # Repassa tudo (Headers, Cookies, Query) para o Backend processar
    forwarded_values {
      query_string = true
      headers      = ["*"]
      cookies {
        forward = "all"
      }
    }

    viewer_protocol_policy = "redirect-to-https"
    min_ttl                = 0
    default_ttl            = 0 # Não fazer cache de respostas da API
    max_ttl                = 0
  }

  # Se tiver outras rotas de API (ex: /api/*), copie o bloco ordered_cache_behavior acima e mude o path_pattern

  # --- ROTA PADRÃO (Frontend no S3) ---
  default_cache_behavior {
    allowed_methods  = ["GET", "HEAD"]
    cached_methods   = ["GET", "HEAD"]
    target_origin_id = "S3-Frontend" # Aponta para a origem do S3

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