# Guia de Bolso Financeiro

Uma aplicação backend em **Spring Boot 4.0.3** para gerenciar finanças pessoais, incluindo contas bancárias, cartões de crédito, transações, metas financeiras e análises com assistência de IA.

## 📋 Índice

- [Visão Geral](#visão-geral)
- [Arquitetura](#arquitetura)
- [Tecnologias](#tecnologias)
- [Requisitos do Sistema](#requisitos-do-sistema)
- [Instalação e Configuração](#instalação-e-configuração)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Endpoints da API](#endpoints-da-api)
- [Modelos de Dados](#modelos-de-dados)
- [Perfis de Configuração](#perfis-de-configuração)

## 🎯 Visão Geral

O **Guia de Bolso Financeiro** é uma solução completa para gestão de finanças pessoais que permite:

✅ Criar e gerenciar contas bancárias (Corrente e Poupança)  
✅ Registrar e controlar cartões de crédito  
✅ Rastrear transações e faturas  
✅ Definir e monitorar metas financeiras  
✅ Receber recomendações de gastos com IA (via API Groq)  
✅ Visualizar análises e históricos de gastos  

## 🏗️ Arquitetura

A aplicação segue a arquitetura **em camadas**:

```
┌─────────────────────────────────────────────┐
│          REST API Controllers               │
│  (User, Account, Transaction, etc)          │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│      Business Logic (Services)              │
│  (UserService, TransactionService, etc)     │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│   Data Persistence (Repositories)           │
│         JPA / Hibernate                     │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│   Database (MySQL / H2)                     │
│   Redis (Cache)                             │
└─────────────────────────────────────────────┘
```

### Camadas da Aplicação

| Camada | Responsabilidade |
|--------|-----------------|
| **Controllers** | Receber requisições HTTP e retornar respostas |
| **Services** | Implementar lógica de negócio |
| **Repositories** | Acessar e persistir dados no banco |
| **DTOs** | Transferir dados entre camadas |
| **Mappers** | Converter entre Entities e DTOs |
| **Entities** | Representação das tabelas no banco de dados |

## 🛠️ Tecnologias

### Core
- **Java 21** - Linguagem de programação
- **Spring Boot 4.0.3** - Framework web
- **Spring Cloud 2025.1.0** - Microserviços e resiliência
- **Spring WebFlux** - Programação reativa

### Database
- **MySQL** - Banco de dados principal
- **H2** - Banco de dados em memória (testes e desenvolvimento)
- **Spring Data JPA** - ORM
- **Hibernate** - Persistência
- **Flyway** - Migração de banco de dados

### Cache & Resiliência
- **Redis** (reactive) - Cache distribuído
- **Resilience4j** - Tolerância a falhas (Circuit Breaker)

### APIs Externas
- **Groq** - IA para análise e recomendações financeiras

### Ferramentas
- **Lombok** - Redução de boilerplate
- **SpringDoc OpenAPI** - Documentação Swagger/OpenAPI
- **Maven** - Gerenciador de dependências

## 💻 Requisitos do Sistema

- **Java 21** ou superior
- **Maven 3.8+** ou Maven Wrapper (mvnw)
- **MySQL 8.0+** (para produção)
- **Redis** (opcional, para cache)
- **API Key da Groq** (para funcionalidades de IA)

## 📦 Instalação e Configuração

### 1. Clonar o repositório

```bash
git clone <url-do-repositorio>
cd appGuiaDeBolsoFinanceiro
```

### 2. Configurar variáveis de ambiente

Crie um arquivo `.env` ou configure em `application.properties`:

```properties
# Banco de Dados
spring.datasource.url=jdbc:mysql:sua_url
spring.datasource.username=root
spring.datasource.password=sua_senha

# IA
groq.api.key=sua_chave_api_groq

# Perfil ativo
spring.profiles.active=prod
```

### 3. Construir o projeto

```bash
./mvnw clean install
```

ou

```bash
mvn clean install
```

### 4. Executar a aplicação

```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em: **http://localhost:8080**

### 5. Acessar a documentação da API

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console (desenvolvimento)

## 📁 Estrutura do Projeto

```
appGuiaDeBolsoFinanceiro/
├── src/
│   ├── main/
│   │   ├── java/com/example/guiaFinanceiro/
│   │   │   ├── GuiaFinanceiroApplication.java       # Classe principal
│   │   │   ├── config/                               # Configurações
│   │   │   │   ├── CorsConfig.java
│   │   │   │   └── WebClientConfig.java
│   │   │   ├── controller/                           # REST Controllers
│   │   │   │   ├── userController.java
│   │   │   │   ├── accountController.java
│   │   │   │   ├── transactionController.java
│   │   │   │   ├── CreditCardController.java
│   │   │   │   ├── InvoiceController.java
│   │   │   │   ├── FinancialGoalController.java
│   │   │   │   └── IaController.java
│   │   │   ├── service/                              # Lógica de negócio
│   │   │   │   ├── UserService.java
│   │   │   │   ├── AccountService.java
│   │   │   │   ├── TransactionService.java
│   │   │   │   ├── CreditCardService.java
│   │   │   │   ├── InvoiceService.java
│   │   │   │   ├── FinancialGoalService.java
│   │   │   │   └── IaService.java
│   │   │   ├── repository/                           # Acesso a dados
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── AccountRepository.java
│   │   │   │   ├── TransactionRepository.java
│   │   │   │   ├── CreditCardRepository.java
│   │   │   │   ├── InvoiceRepository.java
│   │   │   │   └── FinancialGoalRepository.java
│   │   │   ├── entites/                              # Modelos JPA
│   │   │   │   ├── Users.java
│   │   │   │   ├── Account.java
│   │   │   │   ├── Transaction.java
│   │   │   │   ├── CreditCard.java
│   │   │   │   ├── Invoice.java
│   │   │   │   ├── FinancialGoal.java
│   │   │   │   ├── TransactionType.java
│   │   │   │   ├── TransactionCategory.java
│   │   │   │   └── AccountType.java
│   │   │   ├── dto/                                  # Data Transfer Objects
│   │   │   │   ├── UsersDto.java
│   │   │   │   ├── AccountDto.java
│   │   │   │   ├── TransactionDto.java
│   │   │   │   ├── CreditCardDto.java
│   │   │   │   ├── InvoiceDto.java
│   │   │   │   ├── FinancialGoalDto.java
│   │   │   │   ├── TransactionCategoryView.java
│   │   │   │   ├── MonthlyHistory.java
│   │   │   │   └── GastoRecorrenteProjection.java
│   │   │   └── map/                                  # Mapeadores (Entity <-> DTO)
│   │   │       ├── AccountMapper.java
│   │   │       ├── TransactionMapper.java
│   │   │       ├── CreditCardMapper.java
│   │   │       ├── InvoiceMapper.java
│   │   │       └── FinancialGoalMapper.java
│   │   └── resources/
│   │       ├── application.properties                # Configuração padrão
│   │       ├── application-prod.properties           # Perfil produção
│   │       ├── application-qa.properties             # Perfil QA
│   │       ├── data.sql                              # Dados iniciais
│   │       └── db/migration/                         # Migrações Flyway
│   └── test/
│       └── java/com/example/guiaFinanceiro/
│           ├── GuiaFinanceiroApplicationTests.java
│           ├── TestGuiaFinanceiroApplication.java
│           └── TestcontainersConfiguration.java
├── pom.xml                                           # Dependências Maven
├── mvnw / mvnw.cmd                                   # Maven Wrapper
└── README.md
```

## 🔌 Endpoints da API

### 👤 Usuários (`/user`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/user/login` | Autenticação via email |
| GET | `/user/{id}` | Obter dados do usuário |

### 🏦 Contas (`/account`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/account` | Criar nova conta |
| GET | `/account/{id}` | Obter conta por ID |
| GET | `/account/user/{userId}` | Listar contas do usuário |
| PUT | `/account/{id}` | Atualizar conta |
| DELETE | `/account/{id}` | Deletar conta |

### 💳 Cartões de Crédito (`/creditCard`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/creditCard` | Criar novo cartão |
| GET | `/creditCard/{id}` | Obter cartão por ID |
| GET | `/creditCard/user/{userId}` | Listar cartões do usuário |
| PUT | `/creditCard/{id}` | Atualizar cartão |

### 💰 Transações (`/transaction`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/transaction` | Registrar nova transação |
| GET | `/transaction/{id}` | Obter transação por ID |
| GET | `/transaction/user/{userId}` | Listar transações do usuário |
| GET | `/transaction/gastoTotal/{userId}` | Total de gastos |
| PUT | `/transaction/{id}` | Atualizar transação |
| DELETE | `/transaction/{id}` | Deletar transação |

### 📄 Faturas (`/invoice`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/invoice` | Criar fatura |
| GET | `/invoice/{id}` | Obter fatura por ID |
| GET | `/invoice/creditCard/{creditCardId}` | Faturas do cartão |
| PUT | `/invoice/{id}` | Atualizar fatura |

### 🎯 Metas Financeiras (`/goal`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/goal` | Criar meta |
| GET | `/goal/{id}` | Obter meta por ID |
| GET | `/goal/user/{userId}` | Listar metas do usuário |
| PUT | `/goal/{id}` | Atualizar meta |

### 🤖 IA e Recomendações (`/Ia`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/Ia/{id}` | Obter recomendações financeiras |
| GET | `/Ia/dicas/{id}` | Análise de gastos e dicas |

## 📊 Modelos de Dados

### Users (Usuários)
```
id (UUID) - PK
name (String)
email (String)
senha (String)
```

### Account (Contas)
```
id (UUID) - PK
name (String)
type (CURRENT, INVESTMENT)
balance (BigDecimal)
users_id (UUID) - FK
```

### CreditCard (Cartões de Crédito)
```
id (UUID) - PK
name (String)
limitAmount (BigDecimal)
availableLimit (BigDecimal)
users_id (UUID) - FK
```

### Transaction (Transações)
```
id (UUID) - PK
description (String)
amount (BigDecimal)
type (TRANSFER, INVOICE_PAYMENT, CREDIT_CARD_PURCHASE, etc)
category (ALIMENTACAO, INTERNET, ENERGIA, etc)
date (LocalDate)
sourceAccountId (UUID) - FK
destinationAccountId (UUID) - FK
creditCardId (UUID) - FK
```

### Invoice (Faturas)
```
id (UUID) - PK
referenceMonth (LocalDate)
totalAmount (BigDecimal)
paid (Boolean)
creditCardId (UUID) - FK
```

### FinancialGoal (Metas Financeiras)
```
id (UUID) - PK
name (String)
targetAmount (BigDecimal)
currentAmount (BigDecimal)
dueDate (LocalDate)
users_id (UUID) - FK
```

## ⚙️ Perfis de Configuração

A aplicação suporta múltiplos perfis:

### Desenvolvimento (database em H2)
```properties
spring.profiles.active=dev
spring.datasource.url=jdbc:h2:mem:testdb
```

### QA
```properties
spring.profiles.active=qa
```

### Produção (MySQL)
```properties
spring.profiles.active=prod
spring.datasource.url=jdbc:mysql://localhost:3306/guia_financeiro
```

## 🔐 Segurança

- **CORS** configurado em `CorsConfig.java`
- **Validação** de entrada com `@Valid`
- **API Key Groq** protegida em variáveis de ambiente

## 📚 Recursos Adicionais

- **Documentação OpenAPI**: `/swagger-ui.html`
- **H2 Console**: `/h2-console` (apenas desenvolvimento)
- **Healthcheck**: `/actuator/health`

## 🤝 Contribuindo

1. Crie uma branch para sua feature: `git checkout -b feature/nova-funcionalidade`
2. Commit suas mudanças: `git commit -m 'Adiciona nova funcionalidade'`
3. Push para a branch: `git push origin feature/nova-funcionalidade`
4. Abra um Pull Request

## 📝 Licença

Este projeto está sob licença [Especifique a licença]

---

**Desenvolvido por:** William Abreu  
**Data:** Abril de 2026

📱 Demonstração

![imagem 2](https://github.com/user-attachments/assets/4e699e00-9109-45d3-a9a4-27b7d02cd6ea)


Relatórios Financeiros

![imagem](https://github.com/user-attachments/assets/890ab80c-dbe7-4b8e-b492-f91562dd56f3)


