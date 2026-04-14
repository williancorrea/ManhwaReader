# CLAUDE.md

Este arquivo fornece orientações ao Claude Code (claude.ai/code) ao trabalhar com o código deste repositório.

## Layout do repositório

Duas aplicações independentes compartilham este repositório:

- `backend/` — serviço Spring Boot 3.5 / Java 25 / Maven (`dev.williancorrea.manhwa.reader`).
- `frontend/` — aplicação Angular 21 SPA (`manhwa-reader`, client-side rendering puro).
- `artifacts/` — artefatos de build/saída (não é código-fonte).

## Comandos comuns

Backend (executar a partir de `backend/`):
- Build: `./mvnw clean package`
- Executar (profile dev): `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`
- Testes: `./mvnw test`
- Teste único: `./mvnw test -Dtest=ClassName#methodName`

Frontend (executar a partir de `frontend/`):
- Servidor de desenvolvimento: `npm start` (ng serve, padrão http://localhost:4200)
- Build: `npm run build`
- Testes (Vitest): `npm test`

## Arquitetura do backend

Organização de pacotes por fatias de funcionalidade (feature-sliced) em `backend/src/main/java/.../reader/`:

- `features/<domínio>/` — cada domínio (work, chapter, volume, page, author, tag, scanlator, language, library, progress, rating, comments, auth, access) é autocontido: entidade JPA, `*Repository`, `*Service`, `*Resource` (controller REST) e um subpacote `dto/`. Domínios complexos (por exemplo, `work/`) adicionam `*Specification` para consultas via JPA Specifications e pastas aninhadas para sub-agregados (`cover/`, `link/`, `synchronization/`).
- `scraper/` — integrações com fontes externas, com abstrações em `base/` e um pacote por provedor: `mediocrescan`, `mangotoons`, `mangadex`, `lycantoons`. O pacote `features/work/synchronization` orquestra a importação dessas fontes para o modelo de domínio. Os clients Feign são configurados por fonte em `application.yaml`, sob `spring.cloud.openfeign.client.config`.
- `storage/minio/` + `features/storage/` — armazenamento de objetos no MinIO (capas, páginas). Configurado via `minio.*` em `application.yaml`; a URL pública difere da URL local, então links gerados devem usar `minio.url.public`.
- `security/` + `features/auth/` — JWT (jjwt) com tokens de acesso e refresh, além de validação de id-token do Google OAuth2 (`google-api-client`). Cookies configurados em `app.auth.cookie`. Origens CORS definidas em `app.cors.allowed-origins`.
- `features/access/` — RBAC (usuários/papéis/permissões) que sustenta as verificações `@PreAuthorize` nos resources.
- `features/scheduling/` — jobs agendados persistentes (gatilhos de scraping/sincronização no estilo cron).
- `email/` + `config/email/` — e-mails transacionais com templates Thymeleaf (`spring-boot-starter-mail`).
- `exception/` — `@ControllerAdvice` e tipos de exceção em `custom/`; mensagens de validação em `ValidationMessages_pt_BR.properties` / `messages.properties` (i18n pt-BR).
- `config/`, `system/`, `utils/` — wiring transversal (Feign, retry, Jackson, etc.).

Persistência:
- MariaDB via `mariadb-java-client`; o schema é gerenciado pelo **Flyway** (`src/main/resources/db/migration/V###__*.sql`). `spring.jpa.hibernate.ddl-auto=none` — nunca dependa do Hibernate para criar/alterar o schema; adicione uma nova migração `V###`. `validate-on-migrate=true` e `clean-disabled=true`.
- JPA usa `default_batch_fetch_size=50` e `enable_lazy_load_no_trans=true`; `open-in-view=false`. Todas as datas são forçadas para UTC (Hibernate `time_zone`, Jackson `time-zone`).

Profiles: `application.yaml` (compartilhado) + `application-dev.yaml` / `application-prod.yaml`. Banco, MinIO, JWT, client id do Google, origens CORS e credenciais de scrapers são todos orientados por variáveis de ambiente, com valores padrão para desenvolvimento (veja as entradas `${VAR:default}` em `application.yaml`).

## Arquitetura do frontend

Aplicação Angular 21 SPA com standalone components (CSR puro — sem SSR/hydration). Tailwind 4 via `@tailwindcss/postcss`. Roteamento em `app.routes.ts`. As pastas de funcionalidades ficam em `src/app/features/` (`admin`, `auth`, `catalog`, `home`, `library`, `work`); a autenticação transversal fica em `src/app/core/auth`; a UI compartilhada em `src/app/shared/components`. Como a aplicação é 100% browser, não adicione guardas `isPlatformBrowser`/`PLATFORM_ID` — use APIs do DOM (`window`, `localStorage`, `IntersectionObserver`) diretamente.

## Convenções a preservar

- Adicione mudanças de banco como uma nova migração Flyway — não edite arquivos `V###` já existentes.
- As fatias de funcionalidade permanecem autocontidas: mantenha entidade, repository, service, resource e DTOs dentro de `features/<domínio>/`. Novos scrapers vão em `scraper/<provedor>/` e são integrados via `features/work/synchronization`.
- Mensagens de validação/i18n são em português (pt-BR); adicione chaves aos arquivos `messages.properties` / `ValidationMessages_pt_BR.properties` existentes em vez de hardcoded strings.
- UTC em todos os lugares — não introduza conversões para horário local no servidor.
