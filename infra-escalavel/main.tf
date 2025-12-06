# main.tf

module "network" {
  source          = "./modules/network"
  existing_vpc_id = var.existing_vpc_id
}

module "database" {
  source        = "./modules/database"
  ami_id        = var.ami_id
  instance_type = var.db_instance_type
  key_name      = var.key_name

  # O Banco pode continuar na privada (backend acessa ele via IP local)
  private_subnet_ids = module.network.private_subnet_ids
  sg_database_id     = module.network.sg_database_id
}

module "backend" {
  source        = "./modules/backend"
  ami_id        = var.ami_id
  instance_type = var.backend_instance_type
  key_name      = var.key_name
  min_size      = var.backend_min_capacity
  max_size      = var.backend_max_capacity
  
  public_subnet_ids  = module.network.public_subnet_ids
  private_subnet_ids = module.network.public_subnet_ids 
  
  # ---------------------------------------------

  sg_backend_id      = module.network.sg_backend_id
  sg_alb_id          = module.network.sg_alb_id
  db_private_ip      = module.database.db_private_ip
}

module "frontend" {
  source       = "./modules/frontend"
  alb_dns_name = module.backend.alb_dns_name
}