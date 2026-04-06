# Plano de Execução — Listagem do Catálogo

## Contexto

- **Backend:** Spring Boot 3.5.7 + Java 25, Spring Security com JWT, Spring Data JPA
- **Frontend:** Angular 21.2.0 com SSR, Tailwind CSS, RxJS
- **Entidade principal:** `Work` (já existente com campos: id, type, status, slug, publicationDemographic, covers, titles, chapters, etc.)
- **Padrão de Resource:** `@RestController` mapeado em `/api/v1/{feature}` (ex: `AuthResource`)
- **Padrão de DTO:** Java Records (ex: `LoginInput`, `AuthOutput`)
- **Padrão de Auth:** `@PreAuthorize("isAuthenticated()")` por método
- **Frontend Home:** Componente `home.ts` com dados mockados, já utiliza `ManhwaCardComponent`

---

## BACKEND

### Etapa 1 — DTO de Saída (`WorkCatalogOutput`)
**Arquivo:** `backend/src/main/java/dev/williancorrea/manhwa/reader/features/work/dto/WorkCatalogOutput.java`

- Criar record com os campos solicitados:
  - `String titulo` — extraído de `WorkTitle` (título principal)
  - `String urlCapa` — extraído de `WorkCover` (capa principal)
  - `String demografia` — valor do enum `WorkPublicationDemographic`
  - `String status` — valor do enum `WorkStatus`
  - `Long quantidadeCapitulos` — contagem de `Chapter` vinculados ao `Work`
- Criar método estático `fromEntity(Work work)` ou usar um mapper para converter a entidade no DTO

### Etapa 2 — Repositório (`WorkRepository`)
**Arquivo:** `backend/src/main/java/dev/williancorrea/manhwa/reader/features/work/WorkRepository.java`

- Adicionar query method que retorne `Page<Work>` com suporte a `Pageable`
- Garantir que o fetch das relações necessárias (titles, covers, chapters count) seja eficiente (evitar N+1)
- Considerar `@EntityGraph` ou `@Query` com JOIN FETCH para titles e covers
- Para contagem de capítulos, usar subquery ou `@Formula` na entidade

### Etapa 3 — Service (`WorkService`)
**Arquivo:** `backend/src/main/java/dev/williancorrea/manhwa/reader/features/work/WorkService.java`

- Criar método `Page<WorkCatalogOutput> listarCatalogo(Pageable pageable)`
- Chamar o repositório com `Pageable` (pageSize padrão = 20)
- Converter cada `Work` para `WorkCatalogOutput`

### Etapa 4 — Resource/Controller (`WorkResource`)
**Arquivo:** `backend/src/main/java/dev/williancorrea/manhwa/reader/features/work/WorkResource.java` *(criar)*

- Anotar com `@RestController` e `@RequestMapping("/api/v1/works")`
- Endpoint: `GET /api/v1/works`
  - Parâmetros: `@RequestParam(defaultValue = "0") int page`, `@RequestParam(defaultValue = "20") int size`
  - Construir `PageRequest.of(page, size)` e passar para o service
  - Retornar `Page<WorkCatalogOutput>` (Spring serializa automaticamente com metadados de paginação)
- Anotar o endpoint com `@PreAuthorize("isAuthenticated()")`

### Etapa 5 — Testes / Validação Backend
- Criar os testes unitarios
- Verificar resposta paginada com estrutura: `{ content: [...], totalElements, totalPages, number, size }`

---

## FRONTEND

### Etapa 6 — Model/Interface (`WorkCatalog`)
**Arquivo:** `frontend/src/app/features/catalog/models/catalog.models.ts` *(criar)*

- Criar interface `WorkCatalogItem`:
  ```typescript
  interface WorkCatalogItem {
    titulo: string;
    urlCapa: string;
    demografia: string;
    status: string;
    quantidadeCapitulos: number;
  }
  ```
- Criar interface `PageResponse<T>` genérica para resposta paginada:
  ```typescript
  interface PageResponse<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    number: number;
    size: number;
  }
  ```

### Etapa 7 — Service (`CatalogService`)
**Arquivo:** `frontend/src/app/features/catalog/services/catalog.service.ts` *(criar)*

- Injetar `HttpClient`
- Método `listar(page: number = 0, size: number = 20): Observable<PageResponse<WorkCatalogItem>>`
  - `GET` para `${environment.apiUrl}/works?page=${page}&size=${size}`
- O `authInterceptor` já existente adiciona o token automaticamente

### Etapa 8 — Componente de Listagem (`CatalogListComponent`)
**Arquivo:** `frontend/src/app/features/catalog/catalog-list/catalog-list.ts` *(criar)*

- Standalone component com imports: `CommonModule`, `ManhwaCardComponent`
- Propriedades:
  - `items: WorkCatalogItem[]`
  - `currentPage`, `totalPages`, `isLoading`
- Template:
  - Grid responsivo de cards (reutilizar ou adaptar `ManhwaCardComponent`)
  - Controles de paginação (anterior/próximo) com indicadores de página
  - Estado de loading (skeleton ou spinner)
- Ao inicializar, chamar `CatalogService.listar(0)`
- Métodos `nextPage()` e `previousPage()` para navegar entre páginas

### Etapa 9 — Rota do Catálogo
**Arquivo:** `frontend/src/app/features/catalog/catalog.routes.ts` *(criar)*

- Definir rota para `CatalogListComponent`
- Registrar no `app.routes.ts`:
  - Path: `'catalog'` → lazy load `catalogRoutes`
  - Proteger com `authGuard`

### Etapa 10 — Integração com a Home (Últimas Atualizações)
**Arquivo:** `frontend/src/app/features/home/home.ts` *(editar)*

- Remover dados mockados
- Injetar `CatalogService`
- No `ngOnInit`, chamar `catalogService.listar(0)` para obter a primeira página
- Usar os dados retornados para popular a seção "Últimas Atualizações"
- Adaptar o `ManhwaCardComponent` (ou criar mapeamento) para aceitar `WorkCatalogItem`

### Etapa 11 — Adaptar `ManhwaCardComponent`
**Arquivo:** `frontend/src/app/shared/components/manhwa-card/manhwa-card.ts` *(editar)*

- Atualizar o `@Input()` para aceitar tanto a interface `Manhwa` atual quanto `WorkCatalogItem`
- Ou criar uma interface unificada que atenda ambos os cenários
- Garantir que os campos mapeiem corretamente (titulo → title, urlCapa → coverUrl, etc.)

---

## Ordem de Execução Recomendada

```
Backend (sequencial):
  1. DTO de Saída (WorkCatalogOutput)
  2. Repositório (query paginada)
  3. Service (listarCatalogo)
  4. Resource/Controller (endpoint GET)
  5. Testes/Validação

Frontend (sequencial):
  6. Models/Interfaces
  7. Service (CatalogService)
  8. Componente de Listagem (CatalogListComponent)
  9. Rotas (catalog.routes + app.routes)
 10. Integração com Home
 11. Adaptação do ManhwaCardComponent
```

---

## Arquivos a Criar
| # | Caminho | Tipo |
|---|---------|------|
| 1 | `backend/.../features/work/dto/WorkCatalogOutput.java` | Record (DTO) |
| 2 | `backend/.../features/work/WorkResource.java` | Controller |
| 3 | `frontend/.../features/catalog/models/catalog.models.ts` | Interface |
| 4 | `frontend/.../features/catalog/services/catalog.service.ts` | Service |
| 5 | `frontend/.../features/catalog/catalog-list/catalog-list.ts` | Component |
| 6 | `frontend/.../features/catalog/catalog.routes.ts` | Routes |

## Arquivos a Editar
| # | Caminho | Alteração |
|---|---------|-----------|
| 1 | `backend/.../features/work/WorkRepository.java` | Adicionar query paginada |
| 2 | `backend/.../features/work/WorkService.java` | Adicionar método listarCatalogo |
| 3 | `frontend/src/app/app.routes.ts` | Adicionar rota /catalog |
| 4 | `frontend/src/app/features/home/home.ts` | Substituir mock por dados reais |
| 5 | `frontend/src/app/shared/components/manhwa-card/manhwa-card.ts` | Adaptar input |
