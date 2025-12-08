# Loomi - Order Challenge 🚀

API de processamento de pedidos desenvolvida com Java 21 e Spring Boot, utilizando arquitetura orientada a eventos (Kafka) para garantir alta performance e desacoplamento.

## 🛠️ Tech Stack

* **Java 21**: Versão LTS mais recente.
* **Spring Boot 3**: Framework web.
* **PostgreSQL**: Banco de dados relacional.
* **Apache Kafka (Redpanda)**: Broker de mensagens para processamento assíncrono.
* **Docker & Docker Compose**: Orquestração de containers.
* **JUnit 5 & Mockito**: Testes Unitários.
* **Testcontainers**: Testes de Integração.
~~
~~---

## 🤖 Uso de IA Generativa no Projeto

Este projeto foi desenvolvido utilizando Inteligência Artificial como ferramenta de apoio (Pair Programming) para otimização de tempo e validação de conceitos. Abaixo, detalho como a tecnologia foi empregada:

### 1. Aceleração de Desenvolvimento (Boilerplate)
* **Logs e Mensagens:** Geração de mensagens de log padronizadas e descritivas para melhorar a observabilidade.
* **Docker & Infraestrutura:** Auxílio na sintaxe correta do `docker-compose.yml` e `Dockerfile` para configuração do ambiente Redpanda/Kafka e PostgreSQL.
* **Postman Collection:** Geração da estrutura JSON para a coleção de testes de API.
* **Massa de Dados:** Criação de scripts `data.sql` para popular o banco de dados com cenários de teste iniciais.

### 2. Documentação Técnica
* **Refinamento de Texto:** A IA foi utilizada para estruturar e revisar a documentação técnica (`README.md`, `PROGRESS.md`), garantindo clareza na explicação das decisões arquiteturais e instruções de setup.

### 3. Apoio à Decisão Arquitetural (Trade-offs)
A IA atuou como um "Senior Architect Advisor" para debater prós e contras em decisões críticas:
* **Kafka vs Redpanda:** Discussão sobre o overhead da JVM do Kafka versus a leveza do Redpanda para um ambiente de desenvolvimento local (Docker).
* **Monolito vs Microsserviços:** Análise sobre manter Producer e Consumer na mesma aplicação. Decidi mantê-los juntos para evitar over-engineering ("matar formiga com bazuca") dado o escopo do desafio, mas mantendo o desacoplamento lógico via pacotes e eventos para facilitar uma futura extração.
* **Gestão de Tempo:** Avaliação de complexidade vs. valor entregue para priorizar funcionalidades opcionais (ex: Liquibase, Swagger) dentro do prazo disponível.

---

## 🏗️ Arquitetura e Design Patterns

O projeto foi desenhado focando em **Clean Code** e **SOLID**:

### 1. Event-Driven Architecture (EDA)
O fluxo de criação de pedidos é **assíncrono**:
1.  **API**: Recebe o pedido, salva como `PENDING` e retorna `201 Created` imediatamente.
2.  **Producer**: Envia um evento `OrderCreatedEvent` para o Kafka.
3.  **Consumer**: Processa as regras de negócio pesadas em background e atualiza para `PROCESSED` ou `FAILED`.

### 2. Design Patterns Implementados
* **Strategy Pattern**: Utilizado para processar os diferentes tipos de produtos (`Physical`, `Digital`, `Subscription`, `PreOrder`, `Corporate`). 
* Cada regra de negócio está isolada em sua própria classe, facilitando a extensão sem modificar o código existente (Open/Closed Principle).

* **Factory Pattern**: A classe `OrderFactory` centraliza a lógica de criação e cálculo de totais do pedido, isolando o Service de regras de montagem de objetos.

* **Helper Pattern**: A classe `OrderProcessingHelper` orquestra a execução das estratégias e 
* o tratamento de erros, mantendo o Service com responsabilidade única e baixa complexidade ciclomática.

---

## 📋 Regras de Negócio Implementadas

O sistema valida e processa pedidos com as seguintes regras específicas:

* **Estoque Físico**: Baixa automática de estoque e disparo de alerta (`LowStockEvent`) via Kafka se restar menos de 5 unidades.
* **Assinaturas**: Validação de limite máximo (5 por cliente) e verificação de duplicidade (não permite assinar o mesmo plano duas vezes).
* **Produtos Digitais**: Verificação se o cliente já possui o item e geração de licença única.
* **Corporativo (B2B)**:
    * Pedidos acima de **$50.000** entram automaticamente em `PENDING_APPROVAL`.
    * Pedidos com mais de 100 itens recebem **15% de desconto** automático.
* **Pré-Venda**: Validação da data de lançamento para impedir encomendas de produtos já lançados.

---

## 🚀 Como Rodar o Projeto

### Pré-requisitos
* Docker e Docker Compose instalados.
* Java 21 (opcional, se rodar via Docker/Maven Wrapper).

### Passo a Passo

1.  **Subir a Infraestrutura (Banco + Kafka)**
    No terminal, na raiz do projeto:
    ```bash
    docker-compose up -d
    ```
    *Isso iniciará o PostgreSQL na porta `5432` e o Redpanda (Kafka) na porta `19092`.*

2.  **Executar a Aplicação**
    ```bash
    ./mvnw spring-boot:run
    ```
    *A aplicação estará disponível em `http://localhost:8080`.*

---

## 🧪 Testes

O projeto possui uma suíte robusta de testes:

* **Testes Unitários**: Cobrem 100% das regras de negócio (Strategies, Factory, Helper).
* **Testes de Integração**: Validam o fluxo ponta a ponta (API -> Banco -> Kafka -> Processamento) usando containers reais.

**Para rodar os testes:**
```bash
./mvnw clean test
