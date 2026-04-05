# Prompt: implementação do sistema de autenticação — frontend

## Contexto do projeto

Estou construindo o frontend de um **manga reader** com **Angular 21.2** (standalone components). Preciso implementar o módulo de autenticação completo com dois fluxos: login por **email/senha** e login via **Google** (usando Google Identity Services SDK).

O backend é Spring Boot e já está implementado com os endpoints documentados abaixo. Este prompt cobre exclusivamente o frontend Angular.

> **Importante:** o projeto já possui SSR habilitado (`@angular/ssr`). Qualquer código que acesse APIs do browser (`document`, `window`, `localStorage`) precisa verificar a plataforma via `isPlatformBrowser` ou `afterNextRender`.

---

## Decisões arquiteturais já definidas

1. **Access token:** armazenado **em memória** (variável no `TokenService` via `BehaviorSubject`). Nunca em `localStorage` ou `sessionStorage`.
2. **Refresh token:** gerenciado pelo browser via cookie `httpOnly`. O Angular não lê nem escreve esse cookie — ele é enviado automaticamente pelo browser quando `withCredentials: true`.
3. **Google Login:** o Angular usa o Google Identity Services SDK para obter o `id_token`, que é enviado ao backend via `POST /api/v1/auth/google`. O backend valida e retorna os tokens da aplicação.
4. **Interceptor de refresh:** ao receber `401`, o interceptor tenta refresh automático. Se falhar, faz logout e redireciona para `/auth/login`. Requests paralelas que falharam com 401 ficam em fila aguardando o novo token.
5. **Lazy loading para auth:** as rotas de auth já usam `loadChildren` / `loadComponent` sob o prefixo `/auth`.
6. **SSR:** o projeto usa `@angular/ssr` com hydration. Serviços que acessam APIs do browser devem ser compatíveis com SSR.

---

## Estado atual do frontend

O projeto já possui:

- **Rotas:** `app.routes.ts` com lazy loading para `home` e `auth` (prefixo `/auth`).
- **Auth routes:** `auth.routes.ts` com rota `login` (component lazy-loaded). Falta rota de `register`.
- **Login component:** `features/auth/login/login.ts` com formulário placeholder (template-driven, sem integração com serviço). Precisa ser reescrito para reactive forms.
- **Home component:** `features/home/home.ts` com dados mockados.
- **Shared components:** `navbar` e `manhwa-card`.
- **Estilos globais:** `styles.css` com design system dark theme + Tailwind CSS 4.
- **SSR:** configurado com `provideClientHydration(withEventReplay())`.
- **app.config.ts:** falta `provideHttpClient` com interceptors.
- **Sem environment files** — precisam ser criados.
- **Sem core/ directory** — serviços e interceptors ainda não existem.
- **Convenção de nomes:** arquivos usam `login.ts` (sem sufixo `.component`), com templates separados em `.html` e `.css`.

---

## Endpoints do backend (já implementados)

### Formato de erro padrão (todas as respostas de erro)

```json
{
  "origin": "/api/v1/auth/login",
  "method": "POST",
  "status": {
    "code": 401,
    "description": "Unauthorized"
  },
  "dateTime": "2026-04-05T12:00:00.000-03:00",
  "items": [
    {
      "key": "auth.error.invalid-credentials",
      "message": "Credenciais inválidas",
      "detail": null
    }
  ]
}
```

O frontend deve ler `items[0].key` para identificar o tipo de erro e exibir mensagem adequada.

---

### `POST /api/v1/auth/register`
```
Request:  { "name": string, "email": string, "password": string }
Response: { "accessToken": string, "expiresIn": number, "isNewUser": null }
Status:   201 Created
Cookie:   refreshToken (httpOnly, automático)
Erros:    409 → items[0].key = "auth.error.email-already-exists"
```

### `POST /api/v1/auth/login`
```
Request:  { "email": string, "password": string }
Response: { "accessToken": string, "expiresIn": number, "isNewUser": null }
Status:   200 OK
Cookie:   refreshToken (httpOnly, automático)
Erros:    401 → items[0].key = "auth.error.invalid-credentials"
          400 → items[0].key = "auth.error.google-account"
```

### `POST /api/v1/auth/google`
```
Request:  { "idToken": string }
Response: { "accessToken": string, "expiresIn": number, "isNewUser": boolean }
Status:   200 OK
Cookie:   refreshToken (httpOnly, automático)
Erros:    400 → items[0].key = "auth.error.invalid-google-token"
          400 → items[0].key = "auth.error.google-email-not-verified"
```

### `POST /api/v1/auth/refresh`
```
Request:  (sem body — cookie httpOnly enviado pelo browser)
Response: { "accessToken": string, "expiresIn": number, "isNewUser": null }
Status:   200 OK
Erros:    400 → items[0].key = "auth.error.invalid-refresh-token"
```

### `POST /api/v1/auth/logout`
```
Response: 204 No Content (cookie limpo pelo backend via Set-Cookie maxAge=0)
```

---

## JWT Claims (access token)

O access token contém:
- `sub`: UUID do usuário
- `email`: email do usuário
- `name`: nome do usuário
- `exp`: timestamp de expiração (15 min padrão)
- `iat`: timestamp de emissão

O refresh token contém:
- `sub`: UUID do usuário
- `type`: "refresh"
- `exp`: timestamp de expiração (7 dias padrão)

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
│   └── (outros serviços core no futuro)
├── features/
│   ├── auth/
│   │   ├── auth.routes.ts          ← já existe, atualizar
│   │   ├── login/
│   │   │   ├── login.ts            ← já existe, reescrever
│   │   │   ├── login.html          ← já existe, atualizar
│   │   │   └── login.css           ← já existe, atualizar
│   │   └── register/
│   │       ├── register.ts         ← criar
│   │       ├── register.html       ← criar
│   │       └── register.css        ← criar
│   └── home/
│       └── (já existe)
├── app.routes.ts                   ← já existe, verificar guards
├── app.config.ts                   ← já existe, adicionar provideHttpClient
└── app.ts                          ← já existe
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
  isNewUser: boolean | null;
}

export interface ApiErrorItem {
  key: string;
  message: string;
  detail: string | null;
}

export interface ApiError {
  origin: string;
  method: string;
  status: {
    code: number;
    description: string;
  };
  dateTime: string;
  items: ApiErrorItem[];
}
```

### `token.service.ts`

- Armazena o access token em memória usando `BehaviorSubject<string | null>`.
- Expõe `token$` (Observable) e `getToken()` (valor síncrono).
- Métodos: `setToken(token: string)`, `clearToken()`, `isAuthenticated(): boolean`.
- Decodifica o JWT para extrair claims (sub, email, name, exp) sem biblioteca externa — usar `atob()` + `JSON.parse()` no payload.
- Método `isTokenExpired(): boolean` baseado no claim `exp`.
- **Não depender de nenhum estado persistido.** Ao recarregar a página, o token some. O `auth.guard` vai tentar refresh automaticamente.
- **SSR:** métodos que usam `atob()` devem verificar se estão no browser ou retornar valores seguros no servidor.

### `auth.service.ts`

- Injeta `TokenService`, `HttpClient`, `Router`.
- `API_URL` configurável via `environment.ts`.
- Todos os métodos HTTP usam `{ withCredentials: true }` para enviar o cookie.
- Métodos:
  - `register(data: RegisterRequest): Observable<AuthResponse>` — chama o endpoint, faz `tap()` para salvar o token no `TokenService` em caso de sucesso.
  - `login(data: LoginRequest): Observable<AuthResponse>` — mesma lógica.
  - `googleLogin(idToken: string): Observable<AuthResponse>` — mesma lógica.
  - `refresh(): Observable<AuthResponse>` — chama `/auth/refresh`, salva novo token.
  - `logout(): void` — chama `/auth/logout`, limpa o `TokenService`, redireciona para `/auth/login`.
  - `isAuthenticated(): boolean` — delega para `TokenService`.

### `google-auth.service.ts`

- Carrega o Google Identity Services SDK (`accounts.google.com/gsi/client`) via script tag dinâmico.
- **SSR:** verificar `isPlatformBrowser` antes de manipular o DOM. No servidor, os métodos devem ser no-op.
- Inicializa o client com o `clientId` configurado em `environment.ts`.
- Método `initialize(): void` — chamado no `login.ts` no `ngOnInit` (ou `afterNextRender`).
- Método `prompt(): void` — dispara o popup/One Tap do Google.
- Callback do Google retorna o `credential` (id_token) que deve ser passado para `AuthService.googleLogin()`.
- Tratar o caso em que o usuário fecha o popup (não é erro, apenas ignora).
- Se o script do Google falhar ao carregar, não quebrar a tela de login — apenas esconder o botão de Google.

### `auth.interceptor.ts` (functional interceptor)

Este é o componente mais crítico e mais propenso a bugs. Requisitos detalhados:

1. Intercepta toda request HTTP.
2. Se existe access token válido no `TokenService`, adiciona `Authorization: Bearer <token>` no header.
3. Se a response retorna `401`:
   a. Verifica se a request é para `/auth/refresh` ou `/auth/login` ou `/auth/register` ou `/auth/google` — se for, **não** tenta refresh (evita loop infinito).
   b. Verifica se já existe um refresh em andamento (flag booleana ou `Subject`).
   c. Se não existe refresh em andamento:
      - Marca flag como "refreshing".
      - Dispara `AuthService.refresh()`.
      - Em caso de sucesso: atualiza token, reenvia a request original, processa a fila de requests pendentes.
      - Em caso de falha: faz logout, rejeita todas as requests na fila.
   d. Se já existe refresh em andamento: enfileira a request em um `Subject` e aguarda o resultado do refresh.

Padrão recomendado para a fila:
```typescript
let isRefreshing = false;
const refreshTokenSubject = new BehaviorSubject<string | null>(null);
```

4. **Não interceptar requests para URLs externas** (ex: Google APIs). Verificar se a URL começa com a `API_URL` antes de adicionar o header.

### `auth.guard.ts`

- Functional guard (`CanActivateFn`).
- Verifica `TokenService.isAuthenticated()`.
- Se não autenticado, tenta `AuthService.refresh()`. Se o refresh funcionar, permite navegação. Se falhar, redireciona para `/auth/login` com `queryParams: { returnUrl: state.url }`.
- Isso resolve o caso de page reload: o token em memória sumiu, mas o cookie de refresh ainda existe.

### `guest.guard.ts`

- Functional guard.
- Se já autenticado, redireciona para `/home` (ou a rota padrão).
- Usado nas rotas de `/auth/login` e `/auth/register` para evitar que o usuário logado veja essas telas.

### `login.ts` (reescrever o existente)

- Standalone component com template e estilos separados (`login.html`, `login.css`).
- Template com:
  - Form reativo (`ReactiveFormsModule`) com email + senha e validação (required, email format, min length).
  - Botão de submit com estado de loading.
  - Botão/div de "Login com Google" (renderizado pelo Google Identity Services SDK ou customizado).
  - Link para `/auth/register`.
  - Área de feedback de erro (mensagem dinâmica baseada no `key` do erro retornado pela API).
- Tratamento de erros específicos (ler `error.items[0].key`):
  - `auth.error.invalid-credentials` → "Email ou senha inválidos".
  - `auth.error.google-account` → "Esta conta foi criada via Google. Use o login com Google."
  - Erro genérico → "Ocorreu um erro. Tente novamente."
- Após login bem-sucedido, redirecionar para `returnUrl` (do queryParam) ou `/home`.

### `register.ts` (criar novo)

- Standalone component com template e estilos separados (`register.html`, `register.css`).
- Form reativo: name, email, password, confirmação de senha.
- Validação: name (2-100 chars), email (format), password (min 8 chars), confirmação (match).
- Custom validator para confirmar que `password` e `confirmPassword` são iguais.
- Tratamento do erro `auth.error.email-already-exists` → "Este email já está cadastrado."
- Após registro bem-sucedido, redirecionar para `/home`.

### `auth.routes.ts` (atualizar o existente)

```typescript
export const authRoutes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () => import('./login/login').then(m => m.LoginComponent),
    canActivate: [guestGuard]
  },
  {
    path: 'register',
    loadComponent: () => import('./register/register').then(m => m.RegisterComponent),
    canActivate: [guestGuard]
  }
];
```

### `app.routes.ts` (atualizar o existente)

Adicionar `authGuard` na rota `home`:

```typescript
export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  {
    path: 'home',
    loadChildren: () => import('./features/home/home.routes').then(m => m.homeRoutes),
    canActivate: [authGuard]
  },
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.authRoutes)
  },
  { path: '**', redirectTo: 'home' }
];
```

### `app.config.ts` (atualizar o existente)

```typescript
import { provideHttpClient, withInterceptors, withFetch } from '@angular/common/http';
import { authInterceptor } from './core/auth/interceptors/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor]), withFetch()),
    provideClientHydration(withEventReplay())
  ]
};
```

### `environment.ts` (criar)

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api/v1',
  googleClientId: 'SEU_CLIENT_ID.apps.googleusercontent.com'
};
```

### `environment.prod.ts` (criar)

```typescript
export const environment = {
  production: true,
  apiUrl: '/api/v1',
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
6. Se refresh retorna 400 → redireciona para `/auth/login`.

### Múltiplos 401 simultâneos
1. Três requests falham com 401 ao mesmo tempo.
2. A primeira dispara o refresh e marca `isRefreshing = true`.
3. As outras duas entram na fila via `refreshTokenSubject`.
4. Refresh retorna novo token → as três requests são reenviadas com o novo token.

### Login com Google → conta já existe com senha
1. Backend retorna 200 com account linking (se email_verified).
2. Frontend trata normalmente — salva token, redireciona.
3. A partir de agora o usuário pode fazer login por ambos os métodos.

### Login com email → conta criada via Google
1. Backend retorna 400 com `items[0].key = "auth.error.google-account"`.
2. Frontend exibe: "Esta conta foi criada via Google. Use o login com Google."

---

## O que NÃO faz parte deste escopo

- Backend (Spring Boot) — tratado em prompt separado.
- Testes e2e — futuro. Mas os componentes devem ser testáveis (injeção de dependências, sem acoplamento direto).

---

## Critérios de qualidade

1. Todo código deve compilar e funcionar com **Angular 21+** standalone components.
2. Usar **signals** onde fizer sentido (ex: estado de loading, mensagens de erro no template).
3. Usar **reactive forms** (`ReactiveFormsModule`), não template-driven.
4. O interceptor deve ser um **functional interceptor** (`HttpInterceptorFn`), não class-based.
5. Guards devem ser **functional** (`CanActivateFn`).
6. Não usar `any` — tipar tudo.
7. Não usar `subscribe()` direto nos components para chamadas HTTP simples — usar `async` pipe ou `toSignal()` quando possível. Para side effects (salvar token, redirecionar), `subscribe()` ou `.pipe(tap())` no service é aceitável.
8. O `google-auth.service.ts` deve funcionar mesmo se o script do Google falhar ao carregar (não quebrar a tela de login — apenas esconde o botão de Google).
9. **Compatível com SSR:** serviços que usam APIs do browser (`document`, `window`, `atob`) devem verificar `isPlatformBrowser` ou usar `afterNextRender`.
10. **Convenção de arquivos:** usar nomes sem sufixo `.component` (ex: `login.ts`, não `login.component.ts`). Templates e estilos em arquivos separados (`.html`, `.css`).
11. Tratar erros da API lendo `items[0].key` do corpo de erro (`ApiError`).
