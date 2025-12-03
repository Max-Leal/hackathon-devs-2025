terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"

    }
  }

  backend "s3" {
    bucket         = "scorebanking-tfstate-bucket"
    key            = "tfstate/terraform.tfstate"
    region         = "us-east-1"
    dynamodb_table = "terraform-state-lock-scorebanking"
    encrypt        = true
  }
}

provider "aws" {
  region = "us-east-1"
}
