# Prompt: implementação do sistema de autenticação — frontend

## Contexto do projeto

Estou construindo o frontend de um **manga reader**. Preciso implementar o módulo de autenticação completo com dois fluxos: login por **email/senha** e login via **Google** (usando Google Identity Services SDK).

O backend é Spring Boot e já está implementado com os endpoints documentados abaixo. Este prompt cobre exclusivamente o frontend Angular.

---

## Decisões arquiteturais já definidas

1. **Access token:** armazenado **em memória** (variável no `TokenService` via `BehaviorSubject`). Nunca em `localStorage` ou `sessionStorage`.
2. **Refresh token:** gerenciado pelo browser via cookie `httpOnly`. O Angular não lê nem escreve esse cookie — ele é enviado automaticamente pelo browser quando `withCredentials: true`.
3. **Google Login:** o Angular usa o Google Identity Services SDK para obter o `id_token`, que é enviado ao backend via `POST /api/v1/auth/google`. O backend valida e retorna os tokens da aplicação.
4. **Interceptor de refresh:** ao receber `401`, o interceptor tenta refresh automático. Se falhar, faz logout e redireciona para `/login`. Requests paralelas que falharam com 401 ficam em fila aguardando o novo token.
5. **Sem lazy loading para o módulo de auth** — os componentes de login/register são leves e precisam carregar rápido.

---

## Endpoints do backend (já implementados)

### `POST /api/v1/auth/register`
```
Request:  { "name": string, "email": string, "password": string }
Response: { "accessToken": string, "expiresIn": number }
Cookie:   refreshToken (httpOnly, automático)
Erros:    409 { "error": "EMAIL_ALREADY_EXISTS" }
```

### `POST /api/v1/auth/login`
```
Request:  { "email": string, "password": string }
Response: { "accessToken": string, "expiresIn": number }
Cookie:   refreshToken (httpOnly, automático)
Erros:    401 { "error": "INVALID_CREDENTIALS" }
          401 { "error": "GOOGLE_ACCOUNT", "message": "..." }
```

### `POST /api/v1/auth/google`
```
Request:  { "idToken": string }
Response: { "accessToken": string, "expiresIn": number, "isNewUser": boolean }
Cookie:   refreshToken (httpOnly, automático)
```

### `POST /api/v1/auth/refresh`
```
Request:  (sem body — cookie httpOnly enviado pelo browser)
Response: { "accessToken": string, "expiresIn": number }
Erros:    401 { "error": "INVALID_REFRESH_TOKEN" }
```

### `POST /api/v1/auth/logout`
```
Response: 204 No Content (cookie limpo pelo backend)
```

---

## Estrutura de arquivos esperada

```
src/app/
├── core/
│   ├── auth/
│   │   ├── services/
│   │   │   ├── auth.service.ts
│   │   │   ├── token.service.ts
│   │   │   └── google-auth.service.ts
│   │   ├── interceptors/
│   │   │   └── auth.interceptor.ts
│   │   ├── guards/
│   │   │   ├── auth.guard.ts
│   │   │   └── guest.guard.ts
│   │   └── models/
│   │       └── auth.models.ts
│   └── services/
│       └── api.service.ts
├── features/
│   ├── auth/
│   │   ├── login/
│   │   │   └── login.component.ts
│   │   └── register/
│   │       └── register.component.ts
│   └── home/
│       └── home.component.ts
├── app.routes.ts
├── app.config.ts
└── app.component.ts
```

---

## Requisitos por componente

### `auth.models.ts`

Interfaces TypeScript para os contratos de API:

```typescript
export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface GoogleLoginRequest {
  idToken: string;
}

export interface AuthResponse {
  accessToken: string;
  expiresIn: number;
  isNewUser?: boolean;
}

export interface AuthError {
  error: string;
  message?: string;
}
```

### `token.service.ts`

- Armazena o access token em memória usando `BehaviorSubject<string | null>`.
- Expõe `token$` (Observable) e `getToken()` (valor síncrono).
- Métodos: `setToken(token: string)`, `clearToken()`, `isAuthenticated(): boolean`.
- Decodifica o JWT para extrair claims (sub, email, name, exp) sem biblioteca externa — usar `atob()` + `JSON.parse()` no payload.
- Método `isTokenExpired(): boolean` baseado no claim `exp`.
- **Não depender de nenhum estado persistido.** Ao recarregar a página, o token some. O `auth.interceptor` vai tentar refresh automaticamente na primeira request protegida.

### `auth.service.ts`

- Injeta `TokenService`, `HttpClient`, `Router`.
- `API_URL` configurável via `environment.ts`.
- Todos os métodos HTTP usam `{ withCredentials: true }` para enviar o cookie.
- Métodos:
  - `register(data: RegisterRequest): Observable<AuthResponse>` — chama o endpoint, faz `tap()` para salvar o token no `TokenService` em caso de sucesso.
  - `login(data: LoginRequest): Observable<AuthResponse>` — mesma lógica.
  - `googleLogin(idToken: string): Observable<AuthResponse>` — mesma lógica.
  - `refresh(): Observable<AuthResponse>` — chama `/auth/refresh`, salva novo token.
  - `logout(): void` — chama `/auth/logout`, limpa o `TokenService`, redireciona para `/login`.
  - `isAuthenticated(): boolean` — delega para `TokenService`.

### `google-auth.service.ts`

- Carrega o Google Identity Services SDK (`accounts.google.com/gsi/client`) via script tag dinâmico.
- Inicializa o client com o `clientId` configurado em `environment.ts`.
- Método `initialize(): void` — chamado no `login.component` no `ngOnInit`.
- Método `prompt(): void` — dispara o popup/One Tap do Google.
- Callback do Google retorna o `credential` (id_token) que deve ser passado para `AuthService.googleLogin()`.
- Tratar o caso em que o usuário fecha o popup (não é erro, apenas ignora).

### `auth.interceptor.ts` (functional interceptor)

Este é o componente mais crítico e mais propenso a bugs. Requisitos detalhados:

1. Intercepta toda request HTTP.
2. Se existe access token válido no `TokenService`, adiciona `Authorization: Bearer <token>` no header.
3. Se a response retorna `401`:
   a. Verifica se a request é para `/auth/refresh` ou `/auth/login` ou `/auth/register` — se for, **não** tenta refresh (evita loop infinito).
   b. Verifica se já existe um refresh em andamento (flag booleana ou `Subject`).
   c. Se não existe refresh em andamento:
      - Marca flag como "refreshing".
      - Dispara `AuthService.refresh()`.
      - Em caso de sucesso: atualiza token, reenvia a request original, processa a fila de requests pendentes.
      - Em caso de falha: faz logout, rejeita todas as requests na fila.
   d. Se já existe refresh em andamento: enfileira a request em um `Subject` e aguarda o resultado do refresh.

Padrão recomendado para a fila:
```typescript
private isRefreshing = false;
private refreshTokenSubject = new BehaviorSubject<string | null>(null);
```

4. **Não interceptar requests para URLs externas** (ex: Google APIs). Verificar se a URL começa com a `API_URL` antes de adicionar o header.

### `auth.guard.ts`

- Functional guard (`CanActivateFn`).
- Verifica `TokenService.isAuthenticated()`.
- Se não autenticado, tenta `AuthService.refresh()`. Se o refresh funcionar, permite navegação. Se falhar, redireciona para `/login` com `queryParams: { returnUrl: state.url }`.
- Isso resolve o caso de page reload: o token em memória sumiu, mas o cookie de refresh ainda existe.

### `guest.guard.ts`

- Functional guard.
- Se já autenticado, redireciona para `/home` (ou a rota padrão).
- Usado nas rotas de `/login` e `/register` para evitar que o usuário logado veja essas telas.

### `login.component.ts`

- Standalone component.
- Template com:
  - Form reativo (email + senha) com validação (required, email format, min length).
  - Botão de submit.
  - Botão/div de "Login com Google" (renderizado pelo Google Identity Services SDK ou customizado).
  - Link para `/register`.
  - Área de feedback de erro (mensagem dinâmica baseada no tipo de erro retornado pela API).
- Tratamento de erros específicos:
  - `INVALID_CREDENTIALS` → "Email ou senha inválidos".
  - `GOOGLE_ACCOUNT` → "Esta conta foi criada via Google. Use o login com Google."
  - Erro genérico → "Ocorreu um erro. Tente novamente."
- Após login bem-sucedido, redirecionar para `returnUrl` (do queryParam) ou `/home`.

### `register.component.ts`

- Standalone component.
- Form reativo: name, email, password, confirmação de senha.
- Validação: name (2-100 chars), email (format), password (min 8 chars), confirmação (match).
- Custom validator para confirmar que `password` e `confirmPassword` são iguais.
- Tratamento do erro `EMAIL_ALREADY_EXISTS` → "Este email já está cadastrado."
- Após registro bem-sucedido, redirecionar para `/home`.

### `app.routes.ts`

```typescript
export const routes: Routes = [
  { path: 'login', component: LoginComponent, canActivate: [guestGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [guestGuard] },
  { path: 'home', component: HomeComponent, canActivate: [authGuard] },
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: '**', redirectTo: 'home' }
];
```

### `app.config.ts`

- Registrar `provideHttpClient(withInterceptors([authInterceptor]))`.
- Incluir `withFetch()` se desejar usar a Fetch API ao invés de XMLHttpRequest.
- Registrar `provideRouter(routes)`.

### `environment.ts`

```typescript
export const environment = {
  apiUrl: 'http://localhost:8080/api/v1',
  googleClientId: 'SEU_CLIENT_ID.apps.googleusercontent.com'
};
```

---

## Comportamento esperado em cenários-chave

### Page reload (F5)
1. Token em memória é perdido.
2. Usuário tenta acessar `/home`.
3. `authGuard` detecta que não está autenticado.
4. Guard tenta `refresh()` — o cookie httpOnly é enviado automaticamente.
5. Se refresh retorna 200 → novo token em memória → navegação permitida.
6. Se refresh retorna 401 → redireciona para `/login`.

### Múltiplos 401 simultâneos
1. Três requests falham com 401 ao mesmo tempo.
2. A primeira dispara o refresh e marca `isRefreshing = true`.
3. As outras duas entram na fila via `refreshTokenSubject`.
4. Refresh retorna novo token → as três requests são reenviadas com o novo token.

### Login com Google → conta já existe com senha
1. Backend retorna 200 com account linking (se email_verified).
2. Frontend trata normalmente — salva token, redireciona.
3. A partir de agora o usuário pode fazer login por ambos os métodos.

---

## O que NÃO faz parte deste escopo

- Backend (Spring Boot) — tratado em prompt separado.
- Testes e2e — futuro. Mas os componentes devem ser testáveis (injeção de dependências, sem acoplamento direto).

---

## Critérios de qualidade

1. Todo código deve compilar e funcionar com Angular 21+ standalone components.
2. Usar signals onde fizer sentido (ex: estado de loading, mensagens de erro no template).
3. Usar reactive forms (não template-driven).
4. O interceptor deve ser um functional interceptor (`HttpInterceptorFn`), não class-based.
5. Guards devem ser functional (`CanActivateFn`).
6. Não usar `any` — tipar tudo.
7. Não usar `subscribe()` direto nos components para chamadas HTTP simples — usar `async` pipe ou `toSignal()` quando possível. Para side effects (salvar token, redirecionar), `subscribe()` ou `.pipe(tap())` no service é aceitável.
8. O `google-auth.service.ts` deve funcionar mesmo se o script do Google falhar ao carregar (não quebrar a tela de login — apenas esconde o botão de Google).
