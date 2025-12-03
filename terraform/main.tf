module "ec2" {
  source                = "./modules/ec2"
  instance_type         = var.instance_type
  ami_id                = var.ami_id
  instance_name         = var.instance_name
  subnet_id             = module.network.scorebanking_subnet_id
  vpc_security_group_id = module.network.sgScorebanking_id
}

module "network" {
  source              = "./modules/network"
  security_group_name = var.security_group_name
  vpc_id              = var.vpc_id
  subnet_name         = var.subnet_name
}
