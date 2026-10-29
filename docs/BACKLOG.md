# Backlog

Cada item vira **uma issue no GitHub e um PR** (meta: um por dia). O título já está
no formato de conventional commit, para servir também de título do PR.

Branch: `<tipo>/<nº-da-issue>-<resumo>` (ex.: `feat/4-crud-servicos`).
Etiquetas sugeridas: `backend`, `frontend`, `infra`, `docs`, `test`, `segment`.

Legenda de tamanho: **P** (até 1h), **M** (1 a 3h), **G** (3h ou mais; quebre se puder).

---

## Semana 1: fundação e cadastros básicos

### 1. `chore: configurar proteção da main e template de PR` · infra · P
- Regra na `main`: só merge via PR com o CI verde.
- `.github/pull_request_template.md` com "O que muda", "Como testar" e checklist.
- Templates de issue (feature e bug).

### 2. `feat: tratamento global de erros da API` · backend · M
- `@RestControllerAdvice` devolvendo `ProblemDetail` (RFC 9457).
- Erros de validação listam campo e mensagem; 404 padronizado.
- Teste de controller cobrindo 400 e 404.

### 3. `feat: entidade base com auditoria` · backend · P
- Classe base com `id` (UUID), `createdAt` e `updatedAt` preenchidos automaticamente.
- Ajustar `business` para usar a mesma convenção.

### 4. `feat: CRUD de serviços` · backend · M
- Migration `service_offering` (nome, descrição, preço em `NUMERIC(10,2)`, duração em minutos, ativo).
- Endpoints `GET/POST/PUT/DELETE /api/services` com DTOs (`record`) e Bean Validation.
- Exclusão lógica (`active = false`).
- Testes de integração com Testcontainers.

### 5. `feat: CRUD de profissionais` · backend · M
- Migration `professional` (nome, telefone, e-mail, ativo).
- Endpoints `/api/professionals` no mesmo padrão dos serviços.

### 6. `feat: vincular serviços aos profissionais` · backend · M
- Tabela N:N `professional_service`.
- `PUT /api/professionals/{id}/services` define quais serviços cada um executa.

### 7. `feat: paginação e ordenação nas listagens` · backend · P
- `Pageable` nos `GET` de listagem, com resposta paginada padronizada.

### 8. `docs: documentar a API no Swagger` · docs · P
- Título, versão e descrição no OpenAPI; exemplos nos DTOs principais.

---

## Semana 2: clientes, agenda e segurança

### 9. `feat: CRUD de clientes` · backend · M
- Migration `customer` (nome, telefone, e-mail, observações, `custom_fields JSONB`).
- Busca por nome ou telefone (`?q=`).

### 10. `feat: horário de funcionamento por profissional` · backend · M
- Tabela `working_hours` (dia da semana, início, fim) e endpoint para configurar.

### 11. `feat: bloqueios de agenda (folgas e ausências)` · backend · P
- Tabela `schedule_block` (profissional, início, fim, motivo).

### 12. `feat: criar agendamento` · backend · G
- Migration `appointment` (cliente, profissional, serviço, início, fim, status, observações).
- Fim calculado pela duração do serviço; preço "congelado" no momento do agendamento.

### 13. `feat: validar conflito de horários` · backend · G
- Rejeitar sobreposição para o mesmo profissional (409 Conflict).
- Respeitar horário de funcionamento e bloqueios.
- Garantia no banco com constraint `EXCLUDE USING gist` em `tstzrange` (extensão `btree_gist`), além da validação no serviço.
- Testes para os casos de borda (encostado, sobreposto, dentro de bloqueio).

### 14. `feat: consultar horários disponíveis` · backend · G
- `GET /api/availability?professionalId=&serviceId=&date=` devolvendo os slots livres.

### 15. `feat: fluxo de status do agendamento` · backend · M
- Transições permitidas (agendado → confirmado → atendido; cancelado; não compareceu).
- Endpoint `PATCH /api/appointments/{id}/status`; transição inválida retorna 422.

### 16. `feat: agenda do dia e da semana` · backend · P
- `GET /api/appointments?from=&to=&professionalId=`.

### 17. `feat: usuários e autenticação JWT` · backend · G
- Migration `app_user` (e-mail, senha com BCrypt, papel).
- `POST /api/auth/login` devolvendo JWT; Spring Security stateless.
- Rotas `/api/public/**`, `/actuator/health` e Swagger liberadas.

### 18. `feat: papéis ADMIN e PROFESSIONAL` · backend · M
- Profissional vê e altera só a própria agenda; admin vê tudo.

### 19. `test: ampliar cobertura e publicar relatório no CI` · test · P
- JaCoCo no build e resumo de cobertura no PR.

---

## Semana 3: front-end

### 20. `chore: criar projeto React + TypeScript (Vite)` · frontend · M
- Pasta `frontend/`, ESLint, Prettier, React Router e cliente HTTP.
- Job de lint e build do front no CI.

### 21. `feat: tela de login e sessão` · frontend · M
- Guarda o token, redireciona quando expira, logout.

### 22. `feat: layout do painel administrativo` · frontend · M
- Menu lateral, cabeçalho e responsivo no celular.

### 23. `feat: telas de serviços e profissionais` · frontend · M
- Listagem, criação e edição consumindo a API.

### 24. `feat: tela de clientes` · frontend · M

### 25. `feat: agenda visual por profissional` · frontend · G
- Visão de dia e semana, cores por status, criar agendamento clicando no horário.

### 26. `feat: página pública de agendamento` · frontend · G
- Fluxo: serviço → profissional → dia → horário → dados do cliente → confirmação.
- Endpoints públicos no back (`/api/public/...`) com limite de requisições.

### 27. `feat: painel de faturamento` · backend + frontend · M
- Faturamento do dia e do mês, serviços mais vendidos, taxa de não comparecimento.

---

## Semana 4: segmentos, notificações e deploy

### 28. `feat: carregar pacotes de segmento` · backend · segment · G
- Arquivos `segments/*.yml` (labels, ativo, campos customizados, fluxo, features, tema).
- `GET /api/segment` devolve o pacote ativo para o front.

### 29. `feat: front usa labels e tema do segmento` · frontend · segment · M
- Nenhum texto de domínio fixo no front; tudo vem do pacote.

### 30. `feat: ativo atendido com campos customizados` · backend · segment · G
- Tabela `asset` com `custom_fields JSONB`, validada pelos campos do segmento.
- Agendamento passa a aceitar um ativo (veículo, pet) quando o segmento exige.

### 31. `feat: pacotes de segmento mecânica e pet shop` · segment · M
- Seeds de demonstração para cada segmento.

### 32. `feat: abstração NotificationChannel e envio por e-mail` · backend · M
- Interface plugável; `EmailChannel` com Spring Mail e templates HTML.

### 33. `feat: e-mail de confirmação e lembrete` · backend · M
- Confirmação ao criar; lembrete agendado (`@Scheduled`) 24h antes.
- Envio assíncrono, com registro de tentativas.

### 34. `chore: Dockerfile da aplicação` · infra · M
- Build em múltiplas etapas, imagem enxuta, usuário não root.
- Serviço da app no `compose.yaml` (perfil opcional).

### 35. `chore: deploy do back-end e do banco` · infra · G
- Conferir planos gratuitos atuais (Render, Railway ou Fly.io; Neon ou Supabase).
- Variáveis de ambiente documentadas; deploy automático a partir da `main`.

### 36. `chore: deploy do front-end` · infra · M
- Vercel apontando para a API publicada; CORS configurado.

### 37. `docs: README de portfólio` · docs · M
- GIFs mostrando o mesmo sistema em barbearia, mecânica e pet shop.
- Diagrama da arquitetura, decisões técnicas e link da demo.

---

## Depois do MVP
- Notificações por WhatsApp (API oficial da Meta)
- Multi-tenant (vários negócios no mesmo sistema)
- Comissão de profissionais
- PWA
