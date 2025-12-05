# Score Banking - Sistema de Análise de Crédito Ágil

## Documentação da Arquitetura

### Visão Geral
O Score Banking é um sistema de simulação e análise de crédito que utiliza:
- **Backend**: Microsserviço containerizado em Docker
- **Frontend**: Aplicação Angular com entrega via CDN
- **Infraestrutura**: AWS com 3 camadas (Frontend, Backend, Database)
- **Automação**: Pipeline CI/CD com GitHub Actions

## 💻 1. Arquitetura da Aplicação (Backend)

O Backend do Score Banking atua como o motor da aplicação, responsável por receber os dados do usuário, executar o modelo de análise de crédito e retornar a decisão de forma ágil e estruturada.

### 1.1. Estrutura da API

- **Linguagem/Framework**: Java com Spring Boot
- **API (Ponto de Contato)**: Endpoint principal `/api/analise-credito` - único canal de comunicação com o Frontend (Angular). Gerencia o fluxo assíncrono de requisição e resposta.
- **Implantação**: Containerizado via Docker, garantindo consistência e portabilidade.

### 1.2. Modelagem do Score (Cálculo Dinâmico)

**Fórmula**: `Score Final = (W₁ × Renda) + (W₂ × Idade) + (W₃ × Histórico Fictício)`

| Fator | Ponderação e Impacto |
|-------|---------------------|
| **Renda** | Principal fator, impactando diretamente o Limite Aprovado. (Renda Alta → Limite e Score Mais Altos). |
| **Idade** | Pondera o risco. Usuários mais jovens ou muito idosos podem ter um ajuste na pontuação. |
| **Limite Aprovado** | Calculado como uma porcentagem do Score Final e da Renda Mensal, garantindo que o valor seja realista. |
| **Nível de Confiança** | O Score Final é traduzido em linguagem humana (ex: 0-30 = Ruim, 70-100 = Excelente). |

### 1.3. Estratégia de Simulação Baseada no CPF

| Último Dígito do CPF | Histórico Simulado | Efeito no Score Final |
|---------------------|-------------------|----------------------|
| 0 a 3 | Risco Elevado (Histórico Negativado) | Redução substancial no Score base, resultando em reprovação ou limite mínimo. |
| 4 a 6 | Risco Moderado (Histórico Neutro) | O Score base é neutro, sendo totalmente dependente da Renda e Idade informadas. |
| 7 a 9 | Risco Baixo (Histórico Positivo) | Aumento no Score base, otimizando o resultado do Limite Aprovado. |

## ☁️ 2. Arquitetura de Infraestrutura (AWS)

A aplicação está implantada em uma arquitetura de três camadas na AWS, configurada via Terraform (IaC), garantindo escalabilidade, alta disponibilidade e segurança.

| Camada | Recurso AWS Principal | Função na Arquitetura |
|--------|----------------------|----------------------|
| **Frontend** | CloudFront Distribution | Entrega global e rápida dos arquivos estáticos (Angular) aos usuários via CDN. |
| | S3 Bucket | Armazenamento seguro dos arquivos do Frontend (acesso restrito apenas ao CloudFront). |
| **Backend** | Application Load Balancer (ALB) | Ponto de contato público da API, distribui o tráfego para as instâncias de backend. |
| | Auto Scaling Group (ASG) | Gerencia e dimensiona dinamicamente as instâncias EC2 da API. |
| | EC2 Instances | Máquinas virtuais que rodam o container Docker da API. |
| **Database** | EC2 Database Instance | Servidor de banco de dados isolado em sub-redes privadas. |
| **Rede** | VPC, Sub-redes, Security Groups | Criação da rede virtual isolada e definição de regras de firewall. |
| **Comunicação** | NAT Gateway | Permite que Backend e Database acessem a internet mantendo-os em redes privadas. |

## 🚀 3. CI/CD (GitHub Actions)

O processo de implantação e atualização é totalmente automatizado através de um pipeline no GitHub Actions.

| Etapa | Responsabilidade |
|-------|------------------|
| **Terraform Apply** | Garante que a infraestrutura (VPC, ALB, ASG, CloudFront, etc.) esteja provisionada e no estado desejado (IaC). |
| **Frontend Build** | Roda `npm install` e `npm run build` na pasta do Angular. |
| **Injeção de Variável** | O URL do ALB (output do Terraform) é injetado no código Angular para que o Frontend saiba onde encontrar o Backend. |
| **S3 Sync** | Os arquivos compilados do Frontend são enviados para o S3 Bucket, e o CloudFront propaga as alterações para a CDN. |
