# 📈 Relatório de Progresso e Gestão - Desafio Loomi

## 1. Gestão de Atividades
**🔗 Link do Backlog: https://github.com/jsrrocha/order-challenge

## 2. Organização das Demandas
Adotei uma abordagem **Incremental e Iterativa**, dividindo o desafio em épicos técnicos para garantir que a base arquitetural estivesse 
sólida antes de implementar regras de negócio complexas.

**A organização seguiu esta ordem lógica:**
1. **Core Domain:** Modelagem das entidades (`Order`, `Product`, `OrderItem`) e repositórios.
2. **Setup & Infraestrutura:** Configuração do Docker Compose (Postgres + Redpanda/Kafka) e estruturação do Spring Boot.
3. **Fluxo Assíncrono (EDA):** Implementação do `Producer` e `Consumer` para garantir que o mecanismo de eventos funcionasse antes da lógica de negócio.
4.  **Regras de Negócio (Strategy Pattern):** Implementação isolada de cada regra (`Physical`, `Digital`, `Subscription`, etc.).
5.  **Refatoração & Qualidade:** Aplicação de Clean Code (`OrderFactory`, `OrderProcessingHelper`) e escrita de testes (Unitários e Integração).

## 3. Priorização das Entregas
Utilizei o conceito de **Caminho Crítico (Critical Path)** para definir o MVP:

* **Prioridade Alta (Must Have):** API recebendo requisição + Persistência no Banco + Envio para Kafka. (Sem isso, o sistema não para em pé).
* **Prioridade Média (Should Have):** Implementação das estratégias de processamento e validações de negócio (estoque, descontos, limites).
* **Prioridade Baixa (Could Have/Refinement):** Refatoração para reduzir complexidade ciclomática, melhoria de logs e cobertura de testes.

## 4. Desafios Enfrentados e Soluções

### 📉 Desafio 1: Complexidade Ciclomática no Service
* **Problema:** O `OrderProcessingService` centralizava muita responsabilidade (validação, decisão de estratégia, persistência, tratamento de erro), resultando em alta complexidade e alertas de código ruim.
* **Solução:** Apliquei o **Princípio da Responsabilidade Única (SRP)** refatorando o código:
    * Extraí a criação do pedido para uma `OrderFactory`.
    * Extraí a orquestração das regras para um `OrderProcessingHelper`.
    * O Service passou a atuar apenas como um orquestrador transacional leve.


### 🔄 Desafio 2: Manutenção do Estado e Idempotência
* **Problema:** Garantir que o processamento não ocorresse duplicado ou em estados inválidos se o consumidor reprocessasse a mensagem.
* **Solução:** Implementei checagens de estado rigorosas (`if status != PENDING return`) logo no início do fluxo e garanti transacionalidade (`@Transactional`) nos métodos críticos.

## 5. O que faria diferente (Em um contexto real de projeto)

Com mais tempo ou em um ambiente produtivo, focaria nos seguintes pontos de evolução:

1. **Melhorias: Mais refatorações para melhorar a qualidade do código
2. **Database Migrations:** Utilizaria **Liquibase** ou **Flyway** para versionamento do esquema do banco de dados.
3. **API Documentation:** Adicionaria **Swagger/OpenAPI** para gerar documentação testável dos endpoints para o time de Front-end.
5. **Usar localmente Sonar ou Jacoco.