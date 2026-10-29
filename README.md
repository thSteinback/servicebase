# servicebase

[![CI](https://github.com/thSteinback/servicebase/actions/workflows/ci.yml/badge.svg)](https://github.com/thSteinback/servicebase/actions/workflows/ci.yml)
![Java 21](https://img.shields.io/badge/Java-21-orange)
![Spring Boot 4](https://img.shields.io/badge/Spring%20Boot-4.1-6DB33F)
![PostgreSQL 17](https://img.shields.io/badge/PostgreSQL-17-336791)
[![Licença MIT](https://img.shields.io/badge/licen%C3%A7a-MIT-blue)](LICENSE)

Sistema de **agendamento e gestão para pequenos negócios de serviço**, construído
como uma base que se adapta a diferentes segmentos (barbearia, mecânica, pet shop,
estética, assistência técnica...) trocando apenas um **pacote de segmento**.

> 🚧 Em desenvolvimento. O primeiro segmento é **barbearia**.

## Ideia central

Todo negócio de serviço tem o mesmo esqueleto: cliente, profissional, serviço e
atendimento. O que muda é o vocabulário, o fluxo de status e os dados extras.

| Conceito genérico | Barbearia | Mecânica | Pet shop |
|---|---|---|---|
| Cliente | Cliente | Cliente | Tutor |
| Ativo atendido | (nenhum) | Veículo | Pet |
| Profissional | Barbeiro | Mecânico | Tosador |
| Atendimento | Agendamento | Ordem de Serviço | Agendamento |

O núcleo é genérico; cada segmento define labels, campos customizados (em `JSONB`),
fluxo de status, features e tema.

## Stack

- **Back-end:** Java 21, Spring Boot 4 (Web MVC, Data JPA, Validation, Actuator)
- **Banco:** PostgreSQL 17 com migrations no Flyway
- **Documentação da API:** springdoc-openapi (Swagger UI)
- **Testes:** JUnit 5 e Testcontainers (Postgres real nos testes)
- **CI:** GitHub Actions
- **Front-end (em breve):** React + TypeScript (Vite)

## Como rodar

Pré-requisitos: JDK 21 e Docker Desktop rodando.

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

O Spring Boot sobe o PostgreSQL do `compose.yaml` automaticamente e aplica as migrations.

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health check: http://localhost:8080/actuator/health

### Testes

```bash
mvnw.cmd verify      # Windows
./mvnw verify        # Linux / macOS
```

Os testes de integração sobem um PostgreSQL descartável via Testcontainers, então o
Docker precisa estar rodando.

## Roadmap

- [ ] CRUD de serviços, profissionais e clientes
- [ ] Agenda por profissional com validação de conflito de horários
- [ ] Autenticação JWT
- [ ] Painel administrativo e página pública de agendamento (React)
- [ ] Notificações por e-mail (depois WhatsApp)
- [ ] Pacotes de segmento: barbearia, mecânica e pet shop
- [ ] Deploy

O backlog detalhado está em [docs/BACKLOG.md](docs/BACKLOG.md).

## Licença

[MIT](LICENSE)
