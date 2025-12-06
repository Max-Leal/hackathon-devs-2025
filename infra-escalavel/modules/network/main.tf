# modules/network/main.tf

data "aws_availability_zones" "available" {
  state = "available"
}

# --- 1. Internet Gateway (MODIFICADO) ---
# Em vez de criar (resource), nós BUSCAMOS o que já existe na sua VPC (data)
data "aws_internet_gateway" "default" {
  filter {
    name   = "attachment.vpc-id"
    values = [var.existing_vpc_id]
  }
}

# --- 2. Subnets ---

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

# --- 3. NAT Gateway e Roteamento ---

resource "aws_eip" "nat" {
  domain = "vpc"
}

resource "aws_nat_gateway" "nat" {
  allocation_id = aws_eip.nat.id
  subnet_id     = aws_subnet.public[0].id

  tags = {
    Name = "Project-NAT-Gateway-scorebanking"
  }
  
  # Removemos o depends_on do IGW pois ele já existe
}

# --- 4. Tabelas de Rota (Route Tables) ---

# Rota Pública: Aponta para o IGW encontrado (data)
resource "aws_route_table" "public" {
  vpc_id = var.existing_vpc_id

  route {
    cidr_block = "0.0.0.0/0"
    # CORREÇÃO AQUI: Usando o ID do Data Source, não do Resource
    gateway_id = data.aws_internet_gateway.default.id 
  }

  tags = {
    Name = "Public-Route-Table-scorebanking"
  }
}

# Rota Privada: Aponta para o NAT Gateway (Criado por nós)
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

# Associações
resource "aws_route_table_association" "public" {
  count          = length(aws_subnet.public)
  subnet_id      = aws_subnet.public[count.index].id
  route_table_id = aws_route_table.public.id
}

resource "aws_route_table_association" "private" {
  count          = length(aws_subnet.private)
  subnet_id      = aws_subnet.private[count.index].id
  route_table_id = aws_route_table.private.id
}

# --- 5. Security Groups ---
# (Mantenha seus Security Groups abaixo exatamente como estavam no arquivo anterior)
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

resource "aws_security_group" "sg_backend" {
  name   = "backend-scorebanking"
  vpc_id = var.existing_vpc_id
  tags   = { Name = "scorebanking-backend-sg" }
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

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

# --- 6. Regras de Referência Cruzada ---
# (Copie as regras db_ingress, backend_egress, etc. do arquivo anterior aqui)

resource "aws_security_group_rule" "db_ingress_from_backend" {
  type                     = "ingress"
  from_port                = var.postgresql_port
  to_port                  = var.postgresql_port
  protocol                 = "tcp"
  source_security_group_id = aws_security_group.sg_backend.id
  security_group_id        = aws_security_group.sg_database.id
}

resource "aws_security_group_rule" "backend_egress_to_db" {
  type                     = "egress"
  from_port                = var.postgresql_port
  to_port                  = var.postgresql_port
  protocol                 = "tcp"
  source_security_group_id = aws_security_group.sg_database.id
  security_group_id        = aws_security_group.sg_backend.id
}

resource "aws_security_group_rule" "backend_ingress_from_alb" {
  type                     = "ingress"
  from_port                = var.java_api_port
  to_port                  = var.java_api_port
  protocol                 = "tcp"
  source_security_group_id = aws_security_group.sg_alb.id
  security_group_id        = aws_security_group.sg_backend.id
}

resource "aws_security_group_rule" "alb_egress_to_backend" {
  type                     = "egress"
  from_port                = var.java_api_port
  to_port                  = var.java_api_port
  protocol                 = "tcp"
  source_security_group_id = aws_security_group.sg_backend.id
  security_group_id        = aws_security_group.sg_alb.id
}