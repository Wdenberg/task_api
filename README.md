# 🚀 To-Do List API - Java 21 & Spring Boot 3

Uma API RESTful robusta e conteinerizada para gerenciamento de tarefas (To-Do List). Desenvolvida com as melhores práticas de Engenharia de Software, utilizando **Java 21 LTS**, **Spring Boot 3**, **PostgreSQL** e testes automatizados com **Testcontainers**.

---

## 🛠️ Tecnologias Utilizadas

O ecossistema do projeto foi construído focando em performance, segurança e facilidade de manutenção:

* **Linguagem:** Java 21 LTS (Records, Pattern Matching, UUID nativo)
* **Framework:** Spring Boot 3.2+ (Spring Web, Spring Data JPA, Validation)
* **Banco de Dados:** PostgreSQL 16
* **Testes:** JUnit 5, Mockito e Testcontainers
* **Documentação:** Springdoc OpenAPI (Swagger 3)
* **DevOps / Infraestrutura:** Docker, Docker Compose (Multi-stage Build)
* **Gerenciador de Dependências:** Maven

---

## 🏗️ Arquitetura e Padrões Aplicados

O projeto adota uma arquitetura em camadas focada na separação de responsabilidades e regras de negócio blindadas:

* **Domain Model:** Entidades JPA ricas e Enums fortemente tipados.
* **DTOs com Java Records:** Estruturas de dados imutáveis para tráfego entre a API e a camada de serviço.
* **Service Layer:** Centralização das regras de negócio com controle transacional (`@Transactional`).
* **Global Exception Handling:** Captura e tratamento padronizado de exceções (RFC 7807) via `@RestControllerAdvice`, garantindo respostas limpas (ex: 400 Bad Request para validações nulas, 404 Not Found para UUIDs inexistentes).

---

## ⚙️ Pré-requisitos

Como a aplicação é 100% conteinerizada, as dependências locais são mínimas. Você só precisa ter instalado no seu ambiente (seja no Ubuntu, Linux Mint, macOS ou Windows):

* **Docker** e **Docker Compose**
* *(Opcional)* JDK 21 instalado localmente caso deseje rodar a aplicação via Maven sem o Docker.

---

## 🚀 Como Executar a Aplicação

### Opção 1: Via Docker Compose (Recomendado)
A maneira mais fácil e rápida de rodar. Um único comando subirá o banco de dados e a aplicação em containers isolados.

1. Clone o repositório.
2. Na raiz do projeto, execute o comando de orquestração:
   ```bash
   docker compose up --build -d

Opção 2: Via Maven Local (Para Desenvolvimento)
Caso queira rodar o Spring Boot localmente enquanto mantém apenas o banco de dados no Docker.

Suba apenas a infraestrutura do banco de dados:
```bash
  docker compose up postgres-db -d
```

Dê permissão de execução ao wrapper do Maven e inicie a aplicação:

```bash
    chmod +x mvnw
    ./mvnw spring-boot:run
```


### 📚 Documentação da API (Swagger)
Com a aplicação rodando, a documentação interativa e viva da API estará disponível através do Swagger UI.

👉 Acesse no navegador: http://localhost:8080/swagger-ui.html

### 🧪 Como Executar os Testes
O projeto conta com cobertura de testes unitários (Mockito) para a camada de regras de negócio e testes de integração utilizando Testcontainers.

Durante os testes de integração, o Testcontainers sobe um container efêmero do PostgreSQL em background, executa as requisições HTTP reais contra o banco e destrói o container automaticamente ao finalizar.

Para rodar a bateria completa de testes:
```bash
    ./mvnw clean test
```
### 👨‍💻 Autor
Wdenberg Ramos

Full Stack Developer
