# Plano de Execucao - Filtro na Lista de Catalogo

## Contexto do Projeto

- **Backend:** Spring Boot 3.5.7 (Java 25), Spring Data JPA, MariaDB, Flyway
- **Frontend:** Angular 21.2, TypeScript, Tailwind CSS
- **Endpoint atual:** `GET /api/v1/works?page=0&size=20` (sem filtros)

---

## Plano de Execucao

### ETAPA 1 - Backend: Criar DTO de filtro e query dinamica

**1.1 - Criar classe `WorkCatalogFilter`**
- Arquivo: `backend/src/main/java/dev/williancorrea/manhwa/reader/features/work/dto/WorkCatalogFilter.java`
- Record com campos:
  - `String title` (busca parcial no titulo, case-insensitive)
  - `WorkType type` (MANGA, MANHWA, MANHUA, NOVEL)
  - `WorkPublicationDemographic publicationDemographic` (SHOUNEN, SEINEN, JOSEI, etc.)
  - `WorkStatus status` (ONGOING, COMPLETED, CANCELLED, HIATUS)
  - `String sort` (valores aceitos: `updated_at_desc`, `updated_at_asc`, `title_asc`, `title_desc`; default: `updated_at_desc`)

**1.2 - Adicionar query dinamica no `WorkRepository`**
- Usar `JpaSpecificationExecutor<Work>` no `WorkRepository` para suportar `Specification`
- Criar classe `WorkSpecification` com metodos estaticos que constroem predicados:
  - `withTitle(String title)` - JOIN com `work_title`, LIKE case-insensitive
  - `withType(WorkType type)` - igualdade no campo `type`
  - `withPublicationDemographic(WorkPublicationDemographic demographic)` - igualdade no campo `publicationDemographic`
  - `withStatus(WorkStatus status)` - igualdade no campo `status`
- Combinar specifications com `and()`

**1.3 - Atualizar `WorkService.findAllWorks`**
- Alterar assinatura para `findAllWorks(WorkCatalogFilter filter, Pageable pageable)`
- Construir Specification a partir do filtro
- Converter `sort` do filtro para `Sort` do Spring:
  - `updated_at_desc` -> `Sort.by(Sort.Direction.DESC, "updatedAt")`
  - `updated_at_asc` -> `Sort.by(Sort.Direction.ASC, "updatedAt")`
  - `title_asc` / `title_desc` -> Sort pelo titulo (via JOIN com work_title)
- Criar PageRequest com sort + paginacao

**1.4 - Atualizar `WorkResource`**
- Adicionar `@RequestParam` opcionais para cada filtro: `title`, `type`, `publicationDemographic`, `status`, `sort`
- Limitar `size` a no maximo 50: `size = Math.min(size, 50)`
- Montar `WorkCatalogFilter` e passar para o service

**1.5 - Adicionar testes unitarios
- Criar classe `WorkCatalogFilterTest` com testes unitarios para `WorkCatalogFilter`
- Testar cada metodo individualmente, incluindo validacao de valores invalidos e bordas

### ETAPA 2 - Frontend: Remover busca global da navbar

**2.1 - Remover busca da navbar**
- Arquivo: `frontend/src/app/shared/components/navbar/navbar.html`
  - Remover o bloco `<div class="navbar__search">` inteiro (linhas 12-28)
- Arquivo: `frontend/src/app/shared/components/navbar/navbar.ts`
  - Remover propriedades `isSearchOpen` e `searchQuery`
  - Remover metodo `toggleSearch()`
  - Remover import do `FormsModule` (se nao usado em outro lugar)
- Arquivo: `frontend/src/app/shared/components/navbar/navbar.css`
  - Remover estilos relacionados a `.navbar__search`

### ETAPA 3 - Frontend: Criar modelo e service de filtro

**3.1 - Atualizar models**
- Arquivo: `frontend/src/app/features/catalog/models/catalog.models.ts`
- Adicionar interface `CatalogFilter`:
  ```typescript
  export interface CatalogFilter {
    title?: string;
    type?: string;
    publicationDemographic?: string;
    status?: string;
    sort?: string;
  }
  ```
- Adicionar constantes para as opcoes de select:
  ```typescript
  export const WORK_TYPES = ['MANGA', 'MANHWA', 'MANHUA', 'NOVEL'];
  export const WORK_STATUSES = ['ONGOING', 'COMPLETED', 'CANCELLED', 'HIATUS'];
  export const WORK_DEMOGRAPHICS = ['SHOUNEN', 'SEINEN', 'JOSEI', 'SHOUJO', 'YAOI', 'YURI', 'HENTAI', 'COMIC', 'NOVEL', 'MANGA', 'UNKNOWN'];
  export const SORT_OPTIONS = [
    { value: 'updated_at_desc', label: 'Mais recente' },
    { value: 'updated_at_asc', label: 'Mais antigo' },
    { value: 'title_asc', label: 'Alfabetica A-Z' },
    { value: 'title_desc', label: 'Alfabetica Z-A' },
  ];
  ```

**3.2 - Atualizar `CatalogService`**
- Arquivo: `frontend/src/app/features/catalog/services/catalog.service.ts`
- Alterar metodo `listar` para aceitar `CatalogFilter`:
  ```typescript
  listar(page: number = 0, size: number = 20, filter?: CatalogFilter): Observable<PageResponse<WorkCatalogItem>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (filter?.title) params = params.set('title', filter.title);
    if (filter?.type) params = params.set('type', filter.type);
    if (filter?.publicationDemographic) params = params.set('publicationDemographic', filter.publicationDemographic);
    if (filter?.status) params = params.set('status', filter.status);
    if (filter?.sort) params = params.set('sort', filter.sort);
    return this.http.get<PageResponse<WorkCatalogItem>>(`${environment.apiUrl}/works`, { params });
  }
  ```

### ETAPA 4 - Frontend: Adicionar painel de filtros na pagina de catalogo

**4.1 - Atualizar `CatalogListComponent`**
- Arquivo: `frontend/src/app/features/catalog/catalog-list/catalog-list.ts`
- Adicionar signals para cada filtro: `filterTitle`, `filterType`, `filterDemographic`, `filterStatus`, `filterSort`
- Importar `FormsModule` para binding dos inputs
- Criar metodo `aplicarFiltros()` que reseta a pagina para 0 e chama `carregarPagina(0)`
- Criar metodo `limparFiltros()` que reseta todos os filtros e recarrega
- Atualizar `carregarPagina()` para passar os filtros ao service
- Adicionar debounce no campo de titulo (300ms) para evitar requisicoes excessivas

**4.2 - Atualizar template `catalog-list.html`**
- Arquivo: `frontend/src/app/features/catalog/catalog-list/catalog-list.html`
- Adicionar painel de filtros acima do grid, com:
  - Input de texto para titulo (com placeholder "Buscar por titulo...")
  - Select para tipo (Manga, Manhwa, Manhua, Novel)
  - Select para formato/demografia (Shounen, Seinen, Josei, etc.)
  - Select para status (Em andamento, Completo, Cancelado, Hiato)
  - Select para ordenacao (Mais recente, Mais antigo, Alfabetica A-Z, Alfabetica Z-A)
  - Botao "Limpar filtros"
- Layout responsivo: filtros em linha no desktop, empilhados no mobile

**4.3 - Atualizar estilos `catalog-list.css`**
- Adicionar estilos para o painel de filtros
- Garantir responsividade (grid/flex que adapta)

---

## Arquivos Impactados

### Backend (criar/editar)
| Arquivo | Acao |
|---------|------|
| `work/dto/WorkCatalogFilter.java` | Criar |
| `work/WorkSpecification.java` | Criar |
| `work/WorkRepository.java` | Editar (add JpaSpecificationExecutor) |
| `work/WorkService.java` | Editar (add filtro no findAllWorks) |
| `work/WorkResource.java` | Editar (add @RequestParam filtros + limit size 50) |

### Frontend (editar)
| Arquivo | Acao |
|---------|------|
| `shared/components/navbar/navbar.html` | Editar (remover busca) |
| `shared/components/navbar/navbar.ts` | Editar (remover busca) |
| `shared/components/navbar/navbar.css` | Editar (remover estilos busca) |
| `features/catalog/models/catalog.models.ts` | Editar (add CatalogFilter + constantes) |
| `features/catalog/services/catalog.service.ts` | Editar (add filtros na chamada) |
| `features/catalog/catalog-list/catalog-list.ts` | Editar (add logica filtros) |
| `features/catalog/catalog-list/catalog-list.html` | Editar (add painel filtros) |
| `features/catalog/catalog-list/catalog-list.css` | Editar (add estilos filtros) |

---

## Ordem de Execucao

1. Backend: Criar `WorkCatalogFilter` + `WorkSpecification`
2. Backend: Editar `WorkRepository` + `WorkService` + `WorkResource`
3. Frontend: Remover busca global da navbar
4. Frontend: Atualizar models + service com filtros
5. Frontend: Atualizar componente e template do catalogo

---

## Prompt de Execucao

Execute a atividade de adicionar filtros na lista de catalogo do ManhwaReader seguindo este plano:

### Backend

1. **Crie `WorkCatalogFilter.java`** em `backend/src/main/java/dev/williancorrea/manhwa/reader/features/work/dto/WorkCatalogFilter.java`:
   - Record com campos opcionais: `String title`, `WorkType type`, `WorkPublicationDemographic publicationDemographic`, `WorkStatus status`, `String sort`

2. **Crie `WorkSpecification.java`** em `backend/src/main/java/dev/williancorrea/manhwa/reader/features/work/WorkSpecification.java`:
   - Classe com metodos estaticos retornando `Specification<Work>`:
     - `withTitle(String title)` - faz JOIN com `titles` (relacao `Work.titles`) e aplica LIKE case-insensitive no campo `title` da entidade `WorkTitle`
     - `withType(WorkType type)` - igualdade simples
     - `withPublicationDemographic(WorkPublicationDemographic d)` - igualdade simples
     - `withStatus(WorkStatus status)` - igualdade simples
   - Metodo `fromFilter(WorkCatalogFilter filter)` que combina todas as specs nao-nulas com `.and()`

3. **Edite `WorkRepository.java`**: adicione `extends JpaSpecificationExecutor<Work>` alem do `JpaRepository` existente

4. **Edite `WorkService.findAllWorks`**: receba `WorkCatalogFilter filter` e `Pageable pageable`. Construa a Specification via `WorkSpecification.fromFilter(filter)`. Resolva o Sort a partir de `filter.sort()`:
   - `updated_at_desc` (default) -> `Sort.by(DESC, "updatedAt")`
   - `updated_at_asc` -> `Sort.by(ASC, "updatedAt")`
   - `title_asc` / `title_desc` -> precisa de JOIN com work_title, use query customizada ou Sort pelo campo adequado
   - Crie `PageRequest.of(page, size, sort)` e passe para `repository.findAll(spec, pageable)`

5. **Edite `WorkResource`**:
   - Adicione `@RequestParam(required = false)` para: `title`, `type`, `publicationDemographic`, `status`, `sort`
   - Aplique `size = Math.min(size, 50)` antes de criar o Pageable
   - Monte `WorkCatalogFilter` e passe para o service

### Frontend

6. **Remova a busca global da navbar**:
   - Em `navbar.html`: remova todo o bloco `<div class="navbar__search">...</div>` (linhas 12-28)
   - Em `navbar.ts`: remova `isSearchOpen`, `searchQuery`, `toggleSearch()` e o import de `FormsModule`
   - Em `navbar.css`: remova estilos `.navbar__search*`

7. **Atualize `catalog.models.ts`**:
   - Adicione a interface `CatalogFilter` com campos opcionais: `title`, `type`, `publicationDemographic`, `status`, `sort`
   - Adicione constantes `WORK_TYPES`, `WORK_STATUSES`, `WORK_DEMOGRAPHICS`, `SORT_OPTIONS`

8. **Atualize `catalog.service.ts`**:
   - O metodo `listar` deve aceitar um parametro opcional `filter?: CatalogFilter`
   - Construa `HttpParams` dinamicamente com os campos preenchidos do filtro

9. **Atualize `catalog-list.ts`**:
   - Adicione signals para os filtros: `filterTitle`, `filterType`, `filterDemographic`, `filterStatus`, `filterSort`
   - Importe `FormsModule`
   - Crie metodo `aplicarFiltros()` que reseta page=0 e recarrega
   - Crie metodo `limparFiltros()` que reseta tudo
   - Atualize `carregarPagina()` para passar filtros ao service
   - Adicione debounce de 300ms no campo titulo

10. **Atualize `catalog-list.html`**:
    - Adicione um painel de filtros entre o header "Catalogo" e o grid, com:
      - Input texto para titulo
      - Select para tipo
      - Select para formato (demografia)
      - Select para status
      - Select para ordenacao
      - Botao "Limpar filtros"
    - Use classes CSS com prefixo `catalog__filter` para estilizacao
    - Layout responsivo

11. **Atualize `catalog-list.css`**:
    - Estilos para `.catalog__filters` (container), `.catalog__filter-input`, `.catalog__filter-select`, `.catalog__filter-btn`
    - Responsivo: linha no desktop, coluna no mobile

### Regras Importantes
- Todos os filtros sao opcionais no backend e frontend
- O `size` maximo e 50 no backend; se o valor passado for maior, substitui por 50
- A ordenacao padrao e `updated_at_desc` (mais recente primeiro)
- A busca por titulo deve ser case-insensitive e parcial (LIKE %termo%)
- Manter o estilo visual dark existente do projeto (tons escuros, roxo/azul como accent)
- Nao alterar funcionalidades existentes que nao estejam no escopo
