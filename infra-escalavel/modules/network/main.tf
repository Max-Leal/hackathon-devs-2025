# Obtém as zonas de disponibilidade disponíveis na região para HA
data "aws_availability_zones" "available" {
  state = "available"
}

# --- 1. Subnets Públicas e Privadas ---

resource "aws_subnet" "public" {
  count                   = length(var.public_subnet_cidrs)
  vpc_id                  = var.existing_vpc_id
  cidr_block              = var.public_subnet_cidrs[count.index]
  availability_zone       = data.aws_availability_zones.available.names[count.index]
  map_public_ip_on_launch = true

  tags = {
    Name = "Public-Subnet-scorebanking-${count.index + 1}"
  }
}

resource "aws_subnet" "private" {
  count                   = length(var.private_subnet_cidrs)
  vpc_id                  = var.existing_vpc_id
  cidr_block              = var.private_subnet_cidrs[count.index]
  availability_zone       = data.aws_availability_zones.available.names[count.index]
  map_public_ip_on_launch = false

  tags = {
    Name = "Private-Subnet-scorebanking-${count.index + 1}"
  }
}

# --- 2. NAT Gateway e Roteamento Privado ---

resource "aws_eip" "nat" {
  domain = "vpc"
}

resource "aws_nat_gateway" "nat" {
  allocation_id = aws_eip.nat.id
  subnet_id     = aws_subnet.public[0].id

  tags = {
    Name = "Project-NAT-Gateway-scorebanking"
  }
}

resource "aws_route_table" "private" {
  vpc_id = var.existing_vpc_id

  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.nat.id
  }
  tags = {
    Name = "Private-Route-Table-scorebanking"
  }
}

resource "aws_route_table_association" "private" {
  count          = length(aws_subnet.private)
  subnet_id      = aws_subnet.private[count.index].id
  route_table_id = aws_route_table.private.id
}

# --- 3. Security Groups (Criados sem referências cruzadas internas) ---

# SG-1: Database (PostgreSQL EC2)
resource "aws_security_group" "sg_database" {
  name   = "database-scorebanking"
  vpc_id = var.existing_vpc_id
  tags   = { Name = "scorebanking-database-sg" }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# SG-2: Backend (Java ASG/EC2)
resource "aws_security_group" "sg_backend" {
  name   = "backend-scorebanking"
  vpc_id = var.existing_vpc_id
  tags   = { Name = "scorebanking-backend-sg" }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow all outbound traffic"
  }
}

# SG-3: ALB (Load Balancer)
resource "aws_security_group" "sg_alb" {
  name   = "alb-scorebanking"
  vpc_id = var.existing_vpc_id
  tags   = { Name = "scorebanking-alb-sg" }

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# --- 4. Regras de Referência Cruzada (aws_security_group_rule) ---

# Regra 1: SG-1 (Database) Ingress: Permite 5432 APENAS do SG do Backend
resource "aws_security_group_rule" "db_ingress_from_backend" {
  type                     = "ingress"
  from_port                = var.postgresql_port
  to_port                  = var.postgresql_port
  protocol                 = "tcp"
  source_security_group_id = aws_security_group.sg_backend.id
  security_group_id        = aws_security_group.sg_database.id
  description              = "Allow PostgreSQL from Backend"
}

# Regra 2: SG-2 (Backend) Egress: Permite saída para 5432 no SG do Database
resource "aws_security_group_rule" "backend_egress_to_db" {
  type      = "egress"
  from_port = var.postgresql_port
  to_port   = var.postgresql_port
  protocol  = "tcp"
  # CORREÇÃO: Usando 'source_security_group_id' para regra de egress que referencia outro SG
  source_security_group_id = aws_security_group.sg_database.id
  security_group_id        = aws_security_group.sg_backend.id
  description              = "Allow outgoing to Database"
}

# Regra 3: SG-2 (Backend) Ingress: Permite 8080 APENAS do SG do ALB
resource "aws_security_group_rule" "backend_ingress_from_alb" {
  type                     = "ingress"
  from_port                = var.java_api_port
  to_port                  = var.java_api_port
  protocol                 = "tcp"
  source_security_group_id = aws_security_group.sg_alb.id
  security_group_id        = aws_security_group.sg_backend.id
  description              = "Allow API traffic from ALB"
}

# Regra 4: SG-3 (ALB) Egress: Permite saída para 8080 no SG do Backend
resource "aws_security_group_rule" "alb_egress_to_backend" {
  type      = "egress"
  from_port = var.java_api_port
  to_port   = var.java_api_port
  protocol  = "tcp"
  # CORREÇÃO: Usando 'source_security_group_id' para regra de egress que referencia outro SG
  source_security_group_id = aws_security_group.sg_backend.id
  security_group_id        = aws_security_group.sg_alb.id
  description              = "Allow outgoing to Backend"
}