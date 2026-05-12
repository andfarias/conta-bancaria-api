# API Conta Bancária

Implementação mínima da API de conta bancária conforme o desafio técnico.

Como executar

- Subir o Postgres com Docker Compose:

```bash
docker compose up -d
```

- Build da aplicação:

```bash
mvn package -DskipTests
```

- Executar:

```bash
mvn spring-boot:run
```

Endpoints (em Português)

- POST /api/contas - criar conta
- POST /api/contas/deposito - depositar
- POST /api/contas/saque - sacar
- POST /api/contas/transferencia - transferir entre contas
- GET /api/contas/{contaId}/extrato - extrato paginado

Decisões arquiteturais

- Arquitetura: separação do domínio (entidades e serviços) da infraestrutura (controllers e repositories) — abordagem hexagonal leve.
- Concorrência: utilização de lock pessimista (PESSIMISTIC_WRITE) para atualizar saldos e evitar problemas de double-spending. As transferências usam ordenação consistente ao travar duas contas para reduzir risco de deadlocks.
- Transações: métodos de serviço são anotados com @Transactional garantindo atomicidade (ACID).

