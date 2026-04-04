# Prompt: implementação do sistema de autenticação — backend

## Contexto do projeto

Estou construindo o backend. Preciso implementar o módulo de autenticação completo com dois fluxos: login por **email/senha (JWT)** e login via **Google (frontend-driven, backend só valida o ID token)**.

O frontend é Angular e será desenvolvido separadamente. Este prompt cobre exclusivamente o backend.

---

## Decisões arquiteturais já definidas

1. **Tokens:** par `accessToken` (15 min, retornado no body JSON) + `refreshToken` (7 dias, retornado em cookie `httpOnly`, `Secure`, `SameSite=Strict`).
2. **Ambiente de desenvolvimento**: Em desenvolvimento (`localhost`), o cookie do refresh token deve usar `SameSite=Lax` e `Secure=false`, pois `SameSite=Strict` + `Secure=true` impede o envio de cookies em `http://localhost`. Usar profile do Spring (`dev` / `prod`) para alternar automaticamente.
3. **Google Login:** o Angular obtém o `id_token` via Google Identity Services SDK e envia ao backend via `POST /api/v1/auth/google`. O backend valida o token com a biblioteca `google-api-client`, faz upsert do usuário e retorna os mesmos tokens JWT da aplicação.
4. **Account linking:** se um usuário se cadastrou com email/senha e depois tenta login com Google usando o mesmo email, as contas são vinculadas automaticamente **apenas se** `email_verified: true` no ID token do Google. O `googleId` é salvo no usuário existente. Caso contrário, retorna erro orientando o login por senha.
5. **Senha nullable:** usuários criados via Google não possuem senha. O campo `passwordHash` fica `null`. O endpoint de login por email/senha deve rejeitar esses usuários com mensagem clara.
6. **Algoritmo JWT:** HMAC-SHA256 (`HS256`) com secret configurável via `application.yaml` pegando de uma variavel de ambiente, caso não exista pegar o valor padrao. O valor padrão é definido como 'secret' no arquivo `application.yaml`.
7. **Login via email (sem username):** o identificador de login é o **email** em todos os fluxos. O campo `username` será removido da tabela `user`.
8. **Sem enum AuthProvider:** um mesmo usuário pode ter **ambas** as formas de login (email/senha + Google). A capacidade de login é derivada dos dados: `passwordHash != null` → pode logar com email/senha; `googleId != null` → pode logar com Google. Não há necessidade de um enum `AuthProvider`.

---

## Conformidade com a arquitetura existente

> **IMPORTANTE:** o projeto já existe e possui padrões consolidados. Toda implementação deve seguir rigorosamente as convenções abaixo.

### Pacote base

O pacote raiz da aplicação é:
```
dev.williancorrea.manhwa.reader
```

### Convenções de nomenclatura

| Conceito          | Convenção do projeto         | **NÃO usar**             |
|-------------------|------------------------------|--------------------------|
| Controllers       | `*Resource.java`             | ~~*Controller.java~~     |
| DTOs de entrada   | `*Input.java`                | ~~*Request.java~~        |
| DTOs de saída     | `*Output.java`               | ~~*Response.java~~       |
| Entidades         | `*.java` (sem sufixo)        |                          |

### Organização de features

Todas as features ficam sob `features/`. Cada feature tem seus próprios subpacotes:
```
features/<nome>/
├── <Entity>.java
├── <Entity>Repository.java
├── <Entity>Service.java
├── <Entity>Resource.java
├── <Entity>Input.java
├── <Entity>Output.java
```

### Padrões de código obrigatórios

- **Lombok:** o projeto usa Lombok extensivamente. Usar `@Getter`, `@Setter`, `@Slf4j`, `@RequiredArgsConstructor`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`, `@Data` conforme necessário.
- **RequiredArgsConstructor** Utilizar o `@Lazy` para evitar dependência circular:
  ```java
  @Lazy public final AuthService authService;
  ```
- **IDs:** `UUID` com `@GeneratedValue(strategy = GenerationType.UUID)`.
- **Timestamps:** `OffsetDateTime` (não `LocalDateTime`), com `@PrePersist` e `@PreUpdate`.
- **Entidades:** implementar `Serializable`.
- **Transações:** `@Transactional` nos métodos de serviço que modificam dados.
- **Validação:** `@Validated` no serviço, `@Valid` nos DTOs do controller.
- **Mensagens:** todas as mensagens de texto visíveis ao usuário devem vir do `messages.properties`, usando `MessageSource` do Spring. As mensagens de Bean Validation já usam `ValidationMessages_pt_BR.properties`.

---

## Framework de exceções existente

O projeto já possui um framework completo de tratamento de exceções. **Toda a feature de autenticação deve utilizar este framework — não criar estruturas paralelas.**

### `ApiError.java` (JÁ EXISTE — `exception/ApiError.java`)

Formato padrão de resposta de erro da API:
```json
{
  "origin": "/api/v1/auth/login",
  "method": "POST",
  "status": {
    "code": 401,
    "description": "Unauthorized"
  },
  "dateTime": "2026-04-03T12:00:00.000-03:00",
  "items": [
    {
      "key": "auth.error.invalid-credentials",
      "message": "Credenciais inválidas",
      "detail": ""
    }
  ]
}
```

> **IMPORTANTE:** este é o formato de erro que a feature de autenticação DEVE utilizar. Não criar outro formato.

### Exceções customizadas existentes (`exception/custom/`)

| Classe               | Uso                                      | HTTP Status | Handler no GlobalExceptionHandler |
|----------------------|------------------------------------------|-------------|-----------------------------------|
| `BusinessException`  | Erros de regra de negócio genéricos      | `400`       | `handlerBusinessException()`      |
| `NotFoundException`  | Recurso não encontrado (com messageKey)  | `404`       | `handlerBusinessNotFoundException()` |

Ambas `BusinessException` e `NotFoundException` seguem o padrão:
```java
@Getter
public class BusinessException extends RuntimeException {
    private final String messageKey;    // chave do messages.properties
    private final Object[] messageArgs; // argumentos para interpolação
}
```

### `GlobalExceptionHandler.java` (JÁ EXISTE — `exception/GlobalExceptionHandler.java`)

O handler global já trata 15+ tipos de exceção. Para a feature de autenticação, é necessário **ADICIONAR** handlers para:

| Exceção                                          | HTTP Status | Ação                                                   |
|--------------------------------------------------|-------------|--------------------------------------------------------|
| `AuthenticationException` (Spring Security)      | `401`       | Novo handler — erros de autenticação JWT e login        |
| `ConflictException` (NOVA — `exception/custom/`) | `409`       | Novo handler — email já cadastrado                     |

#### Handler para `AuthenticationException` (ADICIONAR)

```java
@ExceptionHandler({AuthenticationException.class})
public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
    String messageCode = "auth.error.invalid-credentials";
    String message = getMessage(messageCode, null);

    var items = new ArrayList<ApiError.ErrorItem>();
    items.add(new ApiError.ErrorItem(messageCode, message, null));

    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    String method = ((ServletWebRequest) request).getRequest().getMethod();
    ApiError errors = new ApiError(HttpStatus.UNAUTHORIZED, uri, method, items);
    return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
}
```

#### Handler para `ConflictException` (ADICIONAR)

```java
@ExceptionHandler({ConflictException.class})
public ResponseEntity<Object> handlerConflictException(ConflictException ex, WebRequest request) {
    String messageCode = ex.getMessageKey();
    String message = getMessage(messageCode, ex.getMessageArgs());

    var items = new ArrayList<ApiError.ErrorItem>();
    items.add(new ApiError.ErrorItem(messageCode, message, null));

    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    String method = ((ServletWebRequest) request).getRequest().getMethod();
    ApiError errors = new ApiError(HttpStatus.CONFLICT, uri, method, items);
    return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.CONFLICT, request);
}
```

### `ConflictException.java` (NOVA — `exception/custom/`)

Nova exceção seguindo o mesmo padrão do projeto:
```java
@Getter
public class ConflictException extends RuntimeException {
    private final String messageKey;
    private final Object[] messageArgs;

    public ConflictException(String messageKey, Object[] messageArgs) {
        super(messageKey);
        this.messageKey = messageKey;
        this.messageArgs = messageArgs;
    }
}
```

### Mapeamento de erros de autenticação para exceções

| Cenário de erro                        | Exceção a lançar                                  | HTTP | Chave messages.properties                |
|----------------------------------------|---------------------------------------------------|------|------------------------------------------|
| Email/senha inválidos                  | `BadCredentialsException` (Spring Security)       | 401  | `auth.error.invalid-credentials`         |
| Usuário só tem login Google            | `BusinessException`                               | 400  | `auth.error.google-account`              |
| Email já cadastrado                    | `ConflictException` (NOVA)                        | 409  | `auth.error.email-already-exists`        |
| Token Google inválido                  | `BusinessException`                               | 400  | `auth.error.invalid-google-token`        |
| Email Google não verificado            | `BusinessException`                               | 400  | `auth.error.google-email-not-verified`   |
| Refresh token inválido/expirado        | `BusinessException`                               | 400  | `auth.error.invalid-refresh-token`       |
| Usuário não encontrado                 | `NotFoundException`                               | 404  | `auth.error.user-not-found`              |

> **NÃO criar** `EmailAlreadyExistsException` nem `InvalidGoogleTokenException` como classes separadas. Usar `BusinessException`, `ConflictException` e `NotFoundException` com as chaves do `messages.properties`.

---

## Internacionalização de mensagens

### `messages.properties` (JÁ EXISTE — `src/main/resources/messages.properties`)

O arquivo já existe com chaves de exceção genéricas do projeto. **ADICIONAR** as chaves de autenticação:

```properties
# ===== Auth - erros de autenticação =====
auth.error.invalid-credentials=Credenciais inv\u00e1lidas
auth.error.google-account=Esta conta foi criada via Google. Use o login com Google.
auth.error.email-already-exists=J\u00e1 existe uma conta com este e-mail
auth.error.invalid-google-token=Token do Google inv\u00e1lido ou expirado
auth.error.google-email-not-verified=O e-mail da conta Google n\u00e3o est\u00e1 verificado. Fa\u00e7a login com e-mail e senha.
auth.error.invalid-refresh-token=Refresh token inv\u00e1lido ou expirado
auth.error.user-not-found=Usu\u00e1rio n\u00e3o encontrado
```

> **Nota:** usar escaping unicode (`\u00e1` para `á`, etc.) conforme o padrão já utilizado no arquivo existente.

### `ValidationMessages_pt_BR.properties` (JÁ EXISTE)

Não precisa ser alterado — as mensagens genéricas de Bean Validation já estão configuradas.

---

## Entidade User existente — plano de migração

A entidade `User` já existe em `features/access/user/User.java` com os campos:

| Campo           | Tipo            | Constraint     |
|-----------------|-----------------|----------------|
| `id`            | `CHAR(36)` UUID | PK             |
| `username`      | `VARCHAR(50)`   | NOT NULL UNIQUE|
| `email`         | `VARCHAR(255)`  | NOT NULL UNIQUE|
| `password_hash` | `VARCHAR(255)`  | NOT NULL       |
| `created_at`    | `TIMESTAMP`     | DEFAULT NOW    |

### Alterações necessárias na entidade User

| Ação             | Campo            | Tipo             | Constraint              | Motivo                              |
|------------------|------------------|------------------|-------------------------|-------------------------------------|
| **REMOVER**      | `username`       | —                | —                       | Login será via email, campo desnecessário |
| **ADICIONAR**    | `name`           | `VARCHAR(100)`   | NOT NULL                | Nome de exibição (obrigatório)      |
| **ALTERAR**      | `password_hash`  | `VARCHAR(255)`   | **NULL** (era NOT NULL) | Google users não têm senha          |
| **ADICIONAR**    | `google_id`      | `VARCHAR(255)`   | NULL UNIQUE             | Sub do Google ID token              |
| **ADICIONAR**    | `avatar_url`     | `VARCHAR(500)`   | NULL                    | Foto do perfil (Google picture)     |
| **ADICIONAR**    | `email_verified` | `BOOLEAN`        | NOT NULL DEFAULT FALSE  | Controle de verificação de email    |
| **ADICIONAR**    | `updated_at`     | `TIMESTAMP`      | NULL                    | Rastreio de atualização             |

> **Nota:** o campo `auth_provider`/enum `AuthProvider` **NÃO será criado**. Um mesmo usuário pode ter ambas as formas de login. A capacidade é derivada dos dados: `passwordHash != null` → login por email/senha; `googleId != null` → login por Google.

### Migration Flyway obrigatória: `V008__Alter_user_table_for_authentication.sql`

```sql
-- Remover coluna username (login será via email)
ALTER TABLE user DROP COLUMN username;

-- Adicionar novos campos de autenticação
ALTER TABLE user ADD COLUMN name VARCHAR(100) NOT NULL DEFAULT '' AFTER id;
ALTER TABLE user ADD COLUMN google_id VARCHAR(255) NULL AFTER password_hash;
ALTER TABLE user ADD COLUMN avatar_url VARCHAR(500) NULL AFTER google_id;
ALTER TABLE user ADD COLUMN email_verified BOOLEAN NOT NULL DEFAULT FALSE AFTER avatar_url;
ALTER TABLE user ADD COLUMN updated_at TIMESTAMP NULL AFTER created_at;

-- Tornar password_hash nullable para usuários Google
ALTER TABLE user MODIFY COLUMN password_hash VARCHAR(255) NULL;

-- Índice único para google_id (permitindo NULL)
CREATE UNIQUE INDEX idx_user_google_id ON user(google_id);

-- Atualizar usuário admin existente
UPDATE user SET name = 'Administrador', email_verified = TRUE WHERE email = 'willian.vag@gmail.com';
```

> **Atenção:** o número da migration (`V008`) deve ser verificado antes de criar o arquivo. Caso já exista uma V008, usar o próximo número sequencial disponível.

### Impacto da remoção de `username` em arquivos existentes

Os seguintes arquivos precisam ser atualizados para remover referências ao `username`:

| Arquivo | Alteração |
|---------|-----------|
| `features/access/user/User.java` | Remover campo `username`, adicionar novos campos com Bean Validation |
| `features/access/user/UserInput.java` | Remover campo `username`, adicionar `name` |
| `features/access/user/UserOutput.java` | Remover campo `username`, adicionar `name` |
| `features/access/user/UserRepository.java` | Remover `findByUsername()`, adicionar `findByEmail()` e `findByGoogleId()` |
| `security/DatabaseUserDetailsService.java` | Alterar `findByUsername()` → `findByEmail()`, usar `email` no `.withUsername()` |

---

## Bean Validation na entidade User (MODIFICADA)

A entidade `User` deve ter anotações de Bean Validation diretamente nos campos:

```java
@Entity
@Table(name = "user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank
    @Email
    @Size(max = 255)
    @Column(unique = true, nullable = false)
    private String email;

    @Size(max = 255)
    @Column(name = "password_hash")
    private String passwordHash;  // nullable — Google users não têm senha

    @Size(max = 255)
    @Column(name = "google_id", unique = true)
    private String googleId;  // nullable — email/senha users não têm Google ID

    @Size(max = 500)
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    /**
     * Retorna true se o usuário pode fazer login com email/senha.
     */
    public boolean hasPasswordLogin() {
        return this.passwordHash != null;
    }

    /**
     * Retorna true se o usuário pode fazer login com Google.
     */
    public boolean hasGoogleLogin() {
        return this.googleId != null;
    }
}
```

---

## Estrutura de pacotes esperada

```
src/main/java/dev/williancorrea/manhwa/reader/
├── config/
│   ├── SecurityConfig.java              ← MODIFICAR (já existe)
│   ├── JwtProperties.java              ← NOVO
│   ├── GoogleProperties.java           ← NOVO
│   └── CorsProperties.java             ← NOVO
├── security/
│   └── DatabaseUserDetailsService.java  ← MODIFICAR (já existe)
├── exception/
│   ├── ApiError.java                    ← JÁ EXISTE (não alterar)
│   ├── GlobalExceptionHandler.java     ← MODIFICAR (adicionar handlers de auth)
│   └── custom/
│       ├── BusinessException.java       ← JÁ EXISTE (reutilizar)
│       ├── NotFoundException.java       ← JÁ EXISTE (reutilizar)
│       ├── ObjectNotFoundException.java ← JÁ EXISTE (não alterar)
│       └── ConflictException.java      ← NOVO (para 409)
├── features/
│   ├── auth/                            ← NOVO (feature de autenticação)
│   │   ├── AuthResource.java
│   │   ├── AuthService.java
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── GoogleTokenVerifier.java
│   │   └── dto/
│   │       ├── LoginInput.java
│   │       ├── RegisterInput.java
│   │       ├── GoogleLoginInput.java
│   │       └── AuthOutput.java
│   ├── access/
│   │   └── user/                        ← MODIFICAR (já existe)
│   │       ├── User.java               ← Remover username, adicionar novos campos + Bean Validation
│   │       ├── UserRepository.java     ← Remover findByUsername, adicionar findByEmail/findByGoogleId
│   │       ├── UserInput.java          ← Remover username, adicionar name
│   │       ├── UserOutput.java         ← Remover username, adicionar name
│   │       ├── UserService.java        ← Existente (sem alteração)
│   │       └── UserResource.java       ← Existente (sem alteração)
│   ...
```

---

## Contratos de API a implementar

### `POST /api/v1/auth/register`

Request body:
```json
{
  "name": "string (obrigatório, 2-100 chars)",
  "email": "string (obrigatório, email válido)",
  "password": "string (obrigatório, min 8 chars)"
}
```

Response `201 Created`:
```json
{
  "accessToken": "string",
  "expiresIn": 900
}
```
- O `refreshToken` vai no header `Set-Cookie` (httpOnly, Secure, SameSite conforme profile, Path=/api/v1/auth).
- Se o email já existe, retorna `409 Conflict` via `ConflictException("auth.error.email-already-exists", null)` → formato `ApiError`.

### `POST /api/v1/auth/login`

Request body:
```json
{
  "email": "string",
  "password": "string"
}
```

Response `200 OK`: mesmo formato do register.

Erros:
- `401` via `AuthenticationException` → `ApiError` com key `auth.error.invalid-credentials`.
- `400` via `BusinessException("auth.error.google-account", null)` se o usuário não tem `passwordHash` (só tem login Google).

### `POST /api/v1/auth/google`

Request body:
```json
{
  "idToken": "string"
}
```

Response `200 OK`:
```json
{
  "accessToken": "string",
  "expiresIn": 900,
  "isNewUser": true
}
```
- `isNewUser` indica se o usuário foi criado neste momento (para redirecionamento de onboarding no frontend).
- Mesma lógica de `Set-Cookie` para o refresh token.
- **Account linking:** se já existe um usuário com o mesmo email e o Google retorna `email_verified: true`, vincula o `googleId` ao usuário existente (`isNewUser: false`). Se `email_verified: false`, retorna `400` via `BusinessException("auth.error.google-email-not-verified", null)`.
- **Novo usuário:** cria com `name` do Google, `email`, `googleId`, `avatarUrl` (picture), `emailVerified = true`, `passwordHash = null`.

### `POST /api/v1/auth/refresh`

Sem request body. O refresh token vem do cookie automaticamente.

Response `200 OK`:
```json
{
  "accessToken": "string",
  "expiresIn": 900
}
```
- `400` via `BusinessException("auth.error.invalid-refresh-token", null)` se o cookie não existe ou o token é inválido/expirado.

### `POST /api/v1/auth/logout`

Response `204 No Content`.
- Limpa o cookie do refresh token (Set-Cookie com maxAge=0).

---

## Requisitos por componente

### `SecurityConfig.java` (MODIFICAR — já existe)

O arquivo atual usa HTTP Basic Auth. Deve ser **refatorado** para JWT:

- **Remover** `.httpBasic(Customizer.withDefaults())`.
- Manter CSRF desabilitado e session `STATELESS` (já existentes).
- Adicionar configuração de CORS usando as propriedades de `CorsProperties`.
- Endpoints públicos:
  - `/api/v1/auth/**`
  - `/actuator/health`
  - `/actuator/info`
  - `/error`
- Todos os demais endpoints exigem autenticação.
- Registrar o `JwtAuthenticationFilter` antes do `UsernamePasswordAuthenticationFilter`.
- Expor o `AuthenticationManager` como bean (necessário para o `AuthService`).
- Manter o `BCryptPasswordEncoder` como bean (já existe).
- Manter `@EnableMethodSecurity` (já existe — usado pelo RBAC em `@PreAuthorize`).

### `CorsProperties.java` (NOVO)

- `@ConfigurationProperties(prefix = "app.cors")`.
- Campo: `allowedOrigins` (List<String>).
- Necessário pois `credentials: true` no CORS proíbe o uso de `*` como origin.

### `JwtProperties.java` (NOVO)

- `@ConfigurationProperties(prefix = "app.jwt")`.
- Campos: `secret` (String), `accessTokenExpiration` (Duration, default 15m), `refreshTokenExpiration` (Duration, default 7d).
- Registrar com `@EnableConfigurationProperties` na classe de configuração ou diretamente no `SecurityConfig`.

### `GoogleProperties.java` (NOVO)

- `@ConfigurationProperties(prefix = "app.google")`.
- Campo: `clientId` (String).

### `User.java` (MODIFICAR — já existe)

Ver seção "Bean Validation na entidade User" acima para o código completo.

Resumo das alterações:
- **Remover:** campo `username`.
- **Adicionar:** `name`, `googleId`, `avatarUrl`, `emailVerified`, `updatedAt`.
- **Alterar:** `passwordHash` agora é nullable (remover `nullable = false`).
- **Adicionar:** Bean Validation annotations (`@NotBlank`, `@Email`, `@Size`) diretamente na entidade.
- **Adicionar:** métodos utilitários `hasPasswordLogin()` e `hasGoogleLogin()`.

### `UserRepository.java` (MODIFICAR — já existe)

Remover:
```java
Optional<User> findByUsername(String username);
```

Adicionar:
```java
Optional<User> findByEmail(String email);
Optional<User> findByGoogleId(String googleId);
boolean existsByEmail(String email);
```

### `DatabaseUserDetailsService.java` (MODIFICAR — já existe em `security/`)

O arquivo já existe em `security/DatabaseUserDetailsService.java` e **já carrega authorities do RBAC corretamente** (via `UserAccessGroupRepository` → `AccessGroupPermissionRepository`).

Alterações necessárias:
- Trocar `userRepository.findByUsername(username)` por `userRepository.findByEmail(username)` (o parâmetro do Spring Security se chama `username` mas receberá o email).
- Trocar `.withUsername(user.getUsername())` por `.withUsername(user.getEmail())`.
- A mensagem de erro `"Usuario nao encontrado: "` deve usar `MessageSource` com a chave `auth.error.user-not-found`.

> **NÃO criar** um novo `UserDetailsServiceImpl`. O `DatabaseUserDetailsService` já faz o trabalho e já integra com o RBAC.

### `JwtTokenProvider.java` (NOVO)

- Gerar access token com claims: `sub` = user ID (UUID), `email`, `name`.
- Gerar refresh token com claims mínimos: `sub` = user ID, `type` = "refresh".
- Validar token: verificar assinatura, expiração, e claim `type` quando aplicável.
- Extrair user ID do token.
- Usar a biblioteca `io.jsonwebtoken:jjwt` (versão 0.12+).
- Localização: `features/auth/JwtTokenProvider.java`.

### `JwtAuthenticationFilter.java` (NOVO)

- Extends `OncePerRequestFilter`.
- Extrair token do header `Authorization: Bearer <token>`.
- Validar o token.
- Se válido, carregar `UserDetails` via `DatabaseUserDetailsService` e setar `UsernamePasswordAuthenticationToken` no `SecurityContextHolder` **com as authorities carregadas do RBAC**.
- Se inválido ou ausente, não setar nada (deixar o Spring Security decidir).
- Não lançar exceção para tokens ausentes — requests a endpoints públicos não terão token.
- Localização: `features/auth/JwtAuthenticationFilter.java`.

### `GoogleTokenVerifier.java` (NOVO)

- Usar `com.google.api-client:google-api-client`.
- Validar o `id_token`: verificar assinatura com chaves públicas do Google, validar `aud` contra o Client ID configurado, verificar `iss` (accounts.google.com), verificar expiração.
- Extrair: `sub` (Google ID), `email`, `email_verified`, `name`, `picture`.
- O Client ID do Google deve vir de `GoogleProperties` (`app.google.client-id`).
- Localização: `features/auth/GoogleTokenVerifier.java`.

### `AuthService.java` (NOVO)

- Usar as exceções existentes do projeto (`BusinessException`, `ConflictException`, `NotFoundException`) com chaves do `messages.properties`. Não criar exceções específicas de auth.
- Método `register(RegisterInput)`: validar duplicidade de email (`existsByEmail` → `ConflictException`), salvar com BCrypt, `emailVerified = false`, gerar tokens, retornar output com cookie.
- Método `login(LoginInput)`: buscar user por email, verificar `hasPasswordLogin()` (senão → `BusinessException` com `auth.error.google-account`), autenticar via `AuthenticationManager`, gerar tokens.
- Método `googleLogin(GoogleLoginInput)`: validar ID token via `GoogleTokenVerifier`, buscar usuário por `googleId` ou `email`, aplicar lógica de account linking (verificar `email_verified` do Google), fazer upsert, gerar tokens, retornar com flag `isNewUser`.
- Método `refresh(String refreshToken)`: validar refresh token, gerar novo access token (não rotacionar o refresh token).
- Método `logout()`: retornar cookie de limpeza.
- Localização: `features/auth/AuthService.java`.

### `AuthResource.java` (NOVO)

- Todos os endpoints sob `/api/v1/auth`.
- O refresh token cookie deve ter: `httpOnly=true`, `secure=<conforme profile>`, `sameSite=<conforme profile>`, `path=/api/v1/auth`, `maxAge` = duração do refresh token.
- Usar `ResponseCookie` do Spring para montar o cookie.
- Validação de request via `@Valid` + Bean Validation annotations nos DTOs (Input).
- Localização: `features/auth/AuthResource.java`.

### `GlobalExceptionHandler.java` (MODIFICAR — já existe em `exception/`)

O handler global já existe e trata 15+ exceções. **ADICIONAR** apenas:
1. Handler para `AuthenticationException` → retorna `401` com `ApiError`.
2. Handler para `ConflictException` → retorna `409` com `ApiError`.

Ver seção "Framework de exceções existente" acima para o código dos handlers.

### DTOs (NOVOS)

Usar Java Records (Java 25):

**`LoginInput.java`:**
```java
public record LoginInput(
    @NotBlank @Email String email,
    @NotBlank String password
) {}
```

**`RegisterInput.java`:**
```java
public record RegisterInput(
    @NotBlank @Size(min = 2, max = 100) String name,
    @NotBlank @Email @Size(max = 255) String email,
    @NotBlank @Size(min = 8, max = 100) String password
) {}
```

**`GoogleLoginInput.java`:**
```java
public record GoogleLoginInput(
    @NotBlank String idToken
) {}
```

**`AuthOutput.java`:**
```java
public record AuthOutput(
    String accessToken,
    long expiresIn,
    Boolean isNewUser  // nullable, só presente no google login
) {}
```

Localização: `features/auth/dto/`.

---

## Dependências Maven necessárias

Adicionar ao `pom.xml` na seção `<properties>`:
```xml
<jjwt.version>0.12.6</jjwt.version>
<google-api-client.version>2.7.2</google-api-client.version>
```

Adicionar na seção `<dependencies>`:
```xml
<!-- JWT -->
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-api</artifactId>
  <version>${jjwt.version}</version>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-impl</artifactId>
  <version>${jjwt.version}</version>
  <scope>runtime</scope>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-jackson</artifactId>
  <version>${jjwt.version}</version>
  <scope>runtime</scope>
</dependency>

<!-- Google OAuth2 validation -->
<dependency>
  <groupId>com.google.api-client</groupId>
  <artifactId>google-api-client</artifactId>
  <version>${google-api-client.version}</version>
</dependency>
```

> **Nota:** verificar as versões mais recentes compatíveis no momento da implementação. As versões acima são referência.

---

## Configuração esperada (application.yaml)

Adicionar ao `application.yaml` existente:

```yaml
app:
  jwt:
    secret: ${JWT_SECRET}
    access-token-expiration: 15m
    refresh-token-expiration: 7d
  google:
    client-id: ${GOOGLE_CLIENT_ID}
  cors:
    allowed-origins: ${CORS_ALLOWED_ORIGINS:http://localhost:4200}
```

### Profile de desenvolvimento (`application-dev.yaml`) — NOVO

```yaml
app:
  cors:
    allowed-origins: http://localhost:4200,http://localhost:4201
  auth:
    cookie:
      secure: false
      same-site: Lax
```

### Profile de produção (`application-prod.yaml`) — NOVO

```yaml
app:
  auth:
    cookie:
      secure: true
      same-site: Strict
```

---

## Impacto nos componentes existentes

### Endpoints existentes (`/features/*`)

- Todos os endpoints sob `/features/*` atualmente exigem autenticação via HTTP Basic.
- Após a migração para JWT, eles passarão a exigir `Authorization: Bearer <token>`.
- Os `@PreAuthorize("hasAuthority('ADMINISTRATOR')")` existentes **continuarão funcionando** porque o `DatabaseUserDetailsService` já carrega as authorities do RBAC.
- **Nenhuma alteração é necessária nos Resources existentes** (exceto aqueles que referenciam `username`).

### `UserResource.java` e `UserService.java` existentes

- O `UserResource` gerencia CRUD de usuários (admin).
- O endpoint de criação de usuário no `UserResource` será impactado pela remoção de `username` do `UserInput` — o admin criará usuários com `name` + `email` + `password`.
- O `AuthService` deve usar o `UserRepository` diretamente (não o `UserService`).

### `DatabaseUserDetailsService.java` (em `security/`)

- Já existe e já faz a carga de authorities do RBAC corretamente.
- Apenas trocar a busca de `findByUsername` para `findByEmail`.
- Usar `MessageSource` para a mensagem de `UsernameNotFoundException`.

---

## O que NÃO faz parte deste escopo

- Frontend (Angular) — tratado em prompt separado.
- Rate limiting — tratado em prompt separado.
- Autorização (roles/permissions) — **já implementado** via RBAC existente (tabelas `permission`, `access_group`, etc.). A autenticação deve apenas integrar-se a ele.
- Envio de emails (confirmação, reset de senha) — será implementada em outro momento.
- Refresh token rotation ou blacklist em Redis — não implementar agora, mas deixar o design extensível para isso.

---

## Testes manuais via HTTP Client

O arquivo `artifacts/dev/http/authentication.http` contém todos os requests para testar os endpoints de autenticação via IntelliJ HTTP Client.

### Variáveis de ambiente (`http-client.env.json`)

O arquivo `http-client.env.json` existente precisa ser atualizado para incluir a variável `accessToken`:

```json
{
  "dev": {
    "host": "http://localhost:8080",
    "contentType": "application/json"
  }
}
```

> **Nota:** `authUser` e `authPass` podem ser removidos após a migração para JWT, pois HTTP Basic Auth não será mais utilizado. A variável `accessToken` é populada automaticamente pelos response handlers dos requests de login/register.

### Cenários cobertos no `authentication.http`

| # | Request | Cenário | HTTP esperado |
|---|---------|---------|---------------|
| 1 | `POST /api/v1/auth/register` | Registro com sucesso | `201` |
| 2 | `POST /api/v1/auth/register` | Email duplicado | `409` |
| 3 | `POST /api/v1/auth/register` | Campos inválidos (validação) | `400` |
| 4 | `POST /api/v1/auth/login` | Login com credenciais válidas | `200` |
| 5 | `POST /api/v1/auth/login` | Senha incorreta | `401` |
| 6 | `POST /api/v1/auth/login` | Usuário inexistente | `401` |
| 7 | `POST /api/v1/auth/login` | Login admin existente | `200` |
| 8 | `POST /api/v1/auth/google` | Login Google (token real) | `200` |
| 9 | `POST /api/v1/auth/google` | Token Google inválido | `400` |
| 10 | `POST /api/v1/auth/refresh` | Refresh token válido (cookie) | `200` |
| 11 | `POST /api/v1/auth/refresh` | Sem cookie de refresh | `400` |
| 12 | `POST /api/v1/auth/logout` | Logout (limpa cookie) | `204` |
| 13 | `GET /features/user` | Endpoint protegido com Bearer token | `200` |
| 14 | `GET /features/user` | Endpoint protegido sem token | `401` |
| 15 | `GET /features/user` | Endpoint protegido com token inválido | `401` |

### Fluxo de teste recomendado

1. Executar request #1 (register) — salva `accessToken` automaticamente.
2. Executar request #2 (register duplicado) — valida erro 409.
3. Executar request #4 (login) — salva `accessToken`.
4. Executar request #13 (endpoint protegido) — valida JWT funciona.
5. Executar request #12 (logout) — limpa refresh cookie.
6. Executar request #10 (refresh sem cookie) — valida erro.

---

## Critérios de qualidade

1. Todo código deve compilar e funcionar. Nada de stubs ou TODOs.
2. Usar records para DTOs quando possível (Java 25).
3. Usar Lombok nas entidades e serviços conforme padrão do projeto.
4. Logs estruturados com `@Slf4j` (Lombok) nos pontos críticos (login bem-sucedido, falha de autenticação, token inválido, conta vinculada).
5. Não expor informações sensíveis nos logs (nunca logar senha, token completo, etc).
6. Todas as mensagens visíveis ao usuário devem vir do `messages.properties` via `MessageSource`, usando escaping unicode.
7. Bean Validation annotations diretamente nas entidades (não apenas nos DTOs).
8. Usar as exceções existentes do projeto (`BusinessException`, `NotFoundException`, nova `ConflictException`). Não criar exceções específicas de auth.
9. Testes unitários para: `JwtTokenProvider` (geração, validação, expiração), `AuthService` (cenários de register, login, google login com account linking).
10. A migration Flyway deve ser idempotente-safe e não quebrar dados existentes (o admin seed da V006 deve continuar válido).
11. Manter compatibilidade total com os endpoints `/features/*` existentes e suas regras de `@PreAuthorize`.

---

## Ordem de implementação sugerida

1. Migration Flyway (V008) — alterar tabela `user` (remover `username`, adicionar novos campos).
2. Atualizar entidade `User.java` com Bean Validation + novos campos + métodos utilitários.
3. Atualizar `UserRepository`, `UserInput`, `UserOutput` (remover `username`).
4. Adicionar chaves de auth ao `messages.properties` existente.
5. Criar `ConflictException` em `exception/custom/`.
6. Adicionar handlers de `AuthenticationException` e `ConflictException` no `GlobalExceptionHandler`.
7. `JwtProperties`, `GoogleProperties`, `CorsProperties` + `application.yaml`.
8. `JwtTokenProvider` + testes unitários.
9. Modificar `DatabaseUserDetailsService` (`findByEmail`, `MessageSource`).
10. `JwtAuthenticationFilter`.
11. Refatorar `SecurityConfig` (remover HTTP Basic, adicionar JWT filter, CORS).
12. `GoogleTokenVerifier`.
13. DTOs (`LoginInput`, `RegisterInput`, `GoogleLoginInput`, `AuthOutput`).
14. `AuthService` + testes unitários.
15. `AuthResource`.
16. Testes de integração (opcional, mas recomendado).
