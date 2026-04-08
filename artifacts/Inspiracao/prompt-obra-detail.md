# Prompt: Pagina de Detalhes da Obra (Work Detail)

## Referencia Visual

A imagem `Obra.png` mostra uma pagina de detalhes de uma obra (manhwa/manga) com o seguinte layout:

### Header da Obra
- **Capa** da obra exibida no lado esquerdo (imagem vertical/poster)
- **Titulo principal** da obra em destaque (ex: "O Comeco Depois do Fim")
- **Titulo alternativo** abaixo do titulo principal (nome original/alternativo)
- **Metadados** exibidos em linha:
  - Tipo (Manhwa, Manga, Manhua, Novel)
  - Status (Ongoing, Completed, Cancelled, Hiatus)
  - Ano de lancamento
  - Classificacao de conteudo (Safe, Suggestive, etc.)
- **Tags/Generos** exibidos como badges/chips coloridos (Action, Fantasy, Adventure, etc.)
- **Autores** listados com seus papeis (Writer, Artist, Author)
- **Sinopse/Descricao** da obra em texto

### Barra de Acao
- Botoes de acao com icones:
  - Favoritar / Adicionar a biblioteca
  - Marcar como lido
  - Compartilhar
  - Links externos (MangaDex, MyAnimeList, etc.)

### Lista de Capitulos
- Tabela/lista com colunas:
  - **Numero do capitulo** (com link para leitura)
  - **Titulo do capitulo** (quando disponivel)
  - **Scanlator/Grupo** que traduziu
  - **Data de publicacao**
  - **Status de leitura** (indicador visual: lido, nao lido, em progresso)
- Ordenacao: capitulos mais recentes primeiro (descendente)
- scroll infinito para listas longas
- Botoes de acao por capitulo:
  - Marcar como lido (verde)
  - Fazer download (azul)
  - Marcar como nao lido (vermelho)

---

## Backend (Spring Boot / Java)

### Entidades ja existentes (nao criar novamente)
- `Work` - Entidade principal da obra (pacote `features.work`)
- `WorkTitle` - Titulos da obra (multilingual via relacao ManyToOne com `Language`, com flag `isOfficial`)
- `WorkSynopsis` - Sinopses (multilingual via relacao ManyToOne com `Language`, campo `description`)
- `WorkTag` - Relaciona `Work` <-> `Tag` (ManyToOne para ambos)
- `WorkAuthor` - Relaciona `Work` <-> `Author` (ManyToOne para ambos)
- `WorkCover` - Capas da obra (com `isOfficial`, `fileName`, `origin`, `size`)
- `WorkLink` - Links externos (ManyToOne para `Site`, campo `link` para a URL, enum `SiteType code`)
- `Chapter` - Capitulos (campos: `number`, `numberFormatted`, `numberVersion`, `title`, `releaseDate`, `publishedAt`, `synced`, `disabled`)
  - `language` e ManyToOne para `Language` (acessar via `chapter.getLanguage().getCode()`)
  - `scanlator` e ManyToOne para `Scanlator` (acessar via `chapter.getScanlator().getName()`)
  - `volume` e ManyToOne para `Volume` (acessar via `chapter.getVolume().getNumber()`)
  - Metodo `getNumberWithVersionInteger()` retorna numero formatado com versao (ex: "123", "124.1")
- `Tag` - Tags com `group` (TagGroupType enum) e `name` + aliases (`alias1`, `alias2`, `alias3`)
- `Author` - Autores com `type` (AuthorType enum), `name`, `biography` e dados sociais (twitter, pixiv, etc.)
- `Site` - Entidade de sites externos com `code` (String) e `url`
- `Scanlator` - Entidade com `name`, `code`, `website`, `synchronization`
- `Volume` - Entidade com `number` (Integer), `title`, relacao ManyToOne com `Work`
- `Language` - Entidade com `code` (ex: "pt-br", "en", "ko") e `name`
- `ReadingProgress` - Progresso de leitura (vinculado a `User` + `Chapter`, unique constraint em `user_id`+`chapter_id`). Campos: `pageNumber`, `lastReadAt`
- `Library` - Biblioteca do usuario (vinculado a `User` + `Work`, unique constraint em `user_id`+`work_id`). Campos: `status` (LibraryStatus enum)
- `Rating` - Avaliacao do usuario (vinculado a `User` + `Work`, unique constraint em `user_id`+`work_id`). Campos: `score` (Integer), `createdAt`
- `Comment` - Comentarios
- `User` - Entidade do usuario autenticado (pacote `features.access.user`). Campos: `id`, `name`, `email`, `passwordHash`, `googleId`, `avatarUrl`

### Entidades de relacionamento importantes
- `WorkTag.tag` -> `Tag` (ManyToOne LAZY) - Para acessar nome/grupo: `workTag.getTag().getName()`, `workTag.getTag().getGroup()`
- `WorkAuthor.author` -> `Author` (ManyToOne LAZY) - Para acessar nome/tipo: `workAuthor.getAuthor().getName()`, `workAuthor.getAuthor().getType()`
- `WorkLink.site` -> `Site` (ManyToOne LAZY) - Para acessar nome do site: `workLink.getSite().getCode()`
- `WorkTitle.language` -> `Language` (ManyToOne LAZY) - Para acessar codigo do idioma: `workTitle.getLanguage().getCode()`
- `WorkSynopsis.language` -> `Language` (ManyToOne LAZY) - Para acessar codigo do idioma: `workSynopsis.getLanguage().getCode()`
- `Work.originalLanguage` -> `Language` (ManyToOne LAZY) - Para acessar codigo: `work.getOriginalLanguage().getCode()`

### Enums ja existentes
- `WorkType`: MANGA, MANHWA, MANHUA, NOVEL
- `WorkStatus`: ONGOING, COMPLETED, CANCELLED, HIATUS
- `WorkContentRating`: SAFE, SUGGESTIVE, EROTICA, PORNOGRAPHIC
- `WorkPublicationDemographic`: SHOUNEN, SEINEN, JOSEI, SHOUJO, YAOI, YURI, HENTAI, COMIC, NOVEL, MANGA, UNKNOWN
- `TagGroupType`: THEME, GENRE, FORMAT, CONTENT
- `AuthorType`: WRITER, ARTIST, AUTHOR
- `LibraryStatus`: READING, COMPLETED, PLAN_TO_READ, DROPPED
- `SiteType`: ANI_LIST, ANIME_PLANET, NOVEL_UPDATES, MY_ANIME_LIST, MANGA_UPDATES, KITSU, MANGADEX

### Repositories existentes (precisam de metodos adicionais)
- `WorkRepository` - Tem `findBySlug(String slug)`. Extende `JpaRepository` e `JpaSpecificationExecutor`
- `ChapterRepository` - Tem `findAllByWork_Id(UUID)`. **Precisa adicionar**: query paginada por `work.slug` com ordenacao por `numberFormatted`, filtrando `disabled = false`
- `LibraryRepository` - Vazio. **Precisa adicionar**: `findByUserIdAndWorkId(UUID userId, UUID workId)`, `deleteByUserIdAndWorkId(UUID userId, UUID workId)`
- `RatingRepository` - Vazio. **Precisa adicionar**: `findByUserIdAndWorkId(UUID userId, UUID workId)`
- `ReadingProgressRepository` - Tem `findAllByUser_Id(UUID)`. **Precisa adicionar**: `findByUserIdAndChapterId(UUID userId, UUID chapterId)`, `deleteByUserIdAndChapterId(UUID userId, UUID chapterId)`, e query otimizada para bulk (LEFT JOIN com lista de chapter IDs para evitar N+1)
- `UserRepository` - Tem `findByEmail(String email)`

### Services existentes (precisam de metodos adicionais)
- `WorkService` - Tem `findBySlug(String slug)` que retorna `Optional<Work>`
- `LibraryService` - Tem CRUD basico. **Precisa adicionar**: `findByUserAndWork`, `saveOrUpdate`, `deleteByUserAndWork`
- `RatingService` - Tem CRUD basico. **Precisa adicionar**: `findByUserAndWork`
- `ReadingProgressService` - Tem CRUD basico. **Precisa adicionar**: `findByUserAndChapter`, `saveOrUpdate`, `deleteByUserAndChapter`, `findAllByUserAndChapterIds`
- `ChapterService` - Sem metodos customizados. **Precisa adicionar**: busca paginada por work slug

### Autenticacao - Padrao do projeto
- `UserDetails` do Spring Security usa o **email** como `username` (ver `DatabaseUserDetailsService`)
- Para obter o `User` a partir do `@AuthenticationPrincipal UserDetails`: usar `userDetails.getUsername()` (retorna email) e buscar com `userRepository.findByEmail(email)`
- Padrao existente em `AuthResource`: `@AuthenticationPrincipal UserDetails userDetails`

### Endpoint 1: GET /api/v1/works/{slug}
Retorna os detalhes completos de uma obra pelo slug.

**Response DTO: `WorkDetailOutput`**
```
{
  "id": "uuid",
  "slug": "o-comeco-depois-do-fim",
  "title": "O Comeco Depois do Fim",
  "alternativeTitles": [
    { "title": "The Beginning After the End", "language": "en", "isOfficial": true },
    { "title": "끝이 아닌 시작", "language": "ko", "isOfficial": false }
  ],
  "synopsis": "Descricao da obra...",
  "coverUrl": "https://storage.example.com/shounen/o-comeco-depois-do-fim/covers/cover.jpg",
  "type": "MANHWA",
  "status": "ONGOING",
  "releaseYear": 2018,
  "contentRating": "SAFE",
  "publicationDemographic": "SHOUNEN",
  "originalLanguage": "ko",
  "tags": [
    { "name": "Action", "group": "GENRE" },
    { "name": "Fantasy", "group": "GENRE" },
    { "name": "Adventure", "group": "THEME" }
  ],
  "authors": [
    { "name": "TurtleMe", "type": "WRITER" },
    { "name": "Fuyuki23", "type": "ARTIST" }
  ],
  "links": [
    { "siteCode": "MANGADEX", "url": "https://mangadex.org/title/..." },
    { "siteCode": "MY_ANIME_LIST", "url": "https://myanimelist.net/manga/..." }
  ],
  "chapterCount": 210,
  "userLibraryStatus": "READING",  // null se nao estiver na biblioteca
  "userRating": 9                   // null se nao avaliou
}
```

**Implementacao:**
- Criar `WorkDetailOutput` record em `features/work/dto/`
- Sub-records necessarios: `AlternativeTitleOutput`, `TagOutput`, `AuthorOutput`, `LinkOutput`
- Adicionar metodo `findBySlug` no `WorkResource` com `@GetMapping("/{slug}")`
- Usar `WorkService.findBySlug(slug)` ja existente (retorna `Optional<Work>`)
- **Obter o usuario autenticado** via `@AuthenticationPrincipal UserDetails userDetails` + `userRepository.findByEmail(userDetails.getUsername())`
- Consultar `Library` filtrando por `user_id` + `work_id` para obter `userLibraryStatus`
- Consultar `Rating` filtrando por `user_id` + `work_id` para obter `userRating`
- Se o usuario nao tiver registro em Library ou Rating, retornar `null` nos campos correspondentes
- Montar a URL da capa usando MinIO config (mesmo padrao do `WorkCatalogOutput`: `minioUrl + "/" + bucketName + work.getCoverUrl()`)
- **Mapeamentos de entidades relacionais**:
  - `work.getTitles()` -> iterar `WorkTitle`, acessar `workTitle.getTitle()`, `workTitle.getLanguage().getCode()`, `workTitle.getIsOfficial()`
  - `work.getSynopses()` -> priorizar idioma do usuario, fallback pt-br, depois en. Acessar `workSynopsis.getDescription()`
  - `work.getTags()` -> iterar `WorkTag`, acessar `workTag.getTag().getName()`, `workTag.getTag().getGroup().name()`
  - `work.getAuthors()` -> iterar `WorkAuthor`, acessar `workAuthor.getAuthor().getName()`, `workAuthor.getAuthor().getType().name()`
  - `work.getLinks()` -> iterar `WorkLink`, acessar `workLink.getCode().name()` para siteCode, `workLink.getLink()` para url
  - `work.getOriginalLanguage()` -> acessar `work.getOriginalLanguage().getCode()`
  - `work.getChapterCount()` -> campo `@Formula` que retorna `Long` (MAX do numero, nao COUNT)

### Endpoint 2: GET /api/v1/works/{slug}/chapters
Retorna a lista paginada de capitulos de uma obra.

**Query Params:**
- `page` (default: 0)
- `size` (default: 50, max: 100)
- `sort` (default: "desc" - mais recente primeiro; "asc" - mais antigo primeiro)
- `language` (opcional - filtrar por codigo do idioma, ex: "pt-br")

**Response DTO: `ChapterListOutput`**
```
{
  "content": [
    {
      "id": "uuid",
      "number": "210",
      "numberFormatted": "0210",
      "numberWithVersion": "210",
      "title": "Titulo do capitulo",
      "language": "pt-br",
      "releaseDate": "2024-01-15",
      "scanlator": "Nome do Scanlator",
      "volume": 1,
      "isRead": true,
      "readProgress": 100,
      "publishedAt": "2024-01-15T10:00:00Z"
    }
  ],
  "page": {
    "number": 0,
    "size": 50,
    "totalElements": 210,
    "totalPages": 5
  }
}
```

**Implementacao:**
- Criar `ChapterListOutput` record em `features/chapter/dto/`
- Criar `ChapterResource` em `features/chapter/` com `@GetMapping("/api/v1/works/{slug}/chapters")`
- Adicionar query paginada no `ChapterRepository`:
  - Filtrar por `work.slug = :slug` AND `disabled = false`
  - Ordenar por `numberFormatted` DESC (default) ou ASC
  - Filtro opcional por `language.code = :language`
  - Usar `Page<Chapter>` com `Pageable`
- **Mapeamentos de entidades relacionais no DTO**:
  - `chapter.getLanguage().getCode()` para o campo `language` (String)
  - `chapter.getScanlator().getName()` para o campo `scanlator` (String)
  - `chapter.getVolume() != null ? chapter.getVolume().getNumber() : null` para o campo `volume` (Integer|null)
  - `chapter.getNumberWithVersionInteger()` para o campo `numberWithVersion` (inclui versao se != "0000")
- **Obter o usuario autenticado** para consultar `ReadingProgress` por `user_id` + `chapter_id`
- **Query otimizada para ReadingProgress**: Em vez de N queries individuais, fazer uma unica query com LEFT JOIN ou `WHERE chapter_id IN (:chapterIds) AND user_id = :userId` para todos os capitulos da pagina
- Para cada capitulo retornado, verificar se existe registro em `ReadingProgress` para o usuario logado:
  - `isRead`: true se existe registro em `ReadingProgress`
  - `readProgress`: baseado no `pageNumber` do `ReadingProgress` (0 se nao houver registro)
  - Se nao houver registro, `isRead = false` e `readProgress = 0`

### Endpoint 3: POST /api/v1/works/{slug}/library
Adiciona/atualiza a obra na biblioteca do usuario autenticado.

**Request Body:**
```
{
  "status": "READING"  // READING, COMPLETED, PLAN_TO_READ, DROPPED (enum LibraryStatus)
}
```

**Implementacao:**
- **Obter o usuario autenticado** via `@AuthenticationPrincipal UserDetails` + `userRepository.findByEmail()`
- Buscar a `Work` pelo slug via `workService.findBySlug(slug)`
- Buscar `Library` existente por `user_id` + `work_id` (unique constraint)
  - Se existir: atualizar o `status`
  - Se nao existir: criar novo registro com `user`, `work` e `status`
- Retornar 200 com o status atualizado

### Endpoint 4: DELETE /api/v1/works/{slug}/library
Remove a obra da biblioteca do usuario autenticado.

**Implementacao:**
- **Obter o usuario autenticado**
- Buscar `Library` por `user_id` + `work_id`
- Se existir: deletar o registro
- Se nao existir: retornar 204 (idempotente)

### Endpoint 5: POST /api/v1/works/{slug}/chapters/{chapterId}/read
Marca um capitulo como lido para o usuario autenticado.

**Implementacao:**
- **Obter o usuario autenticado**
- Buscar `ReadingProgress` por `user_id` + `chapter_id` (unique constraint)
  - Se existir: atualizar `lastReadAt` para agora
  - Se nao existir: criar novo registro com `user`, `chapter`, `pageNumber = null`, `lastReadAt = now()`
- Retornar 200

### Endpoint 6: DELETE /api/v1/works/{slug}/chapters/{chapterId}/read
Marca um capitulo como nao lido para o usuario autenticado.

**Implementacao:**
- **Obter o usuario autenticado**
- Buscar `ReadingProgress` por `user_id` + `chapter_id`
- Se existir: deletar o registro
- Se nao existir: retornar 204 (idempotente)

---

## Frontend (Angular 19+ / SSR)

### Rota
- Path: `/work/:slug`
- Lazy loaded module
- Guard: `authGuard` (importar de `core/auth/guards/auth.guard`)
- Adicionar em `app.routes.ts` (seguir padrao existente de `home` e `catalog`)

### Estrutura de Arquivos
```
frontend/src/app/features/work/
  work.routes.ts
  work-detail/
    work-detail.ts        (componente)
    work-detail.html      (template)
    work-detail.css        (estilos)
  services/
    work.service.ts        (chamadas HTTP)
  models/
    work.models.ts         (interfaces/types)
```

### Interfaces compartilhadas
- `PageResponse<T>` ja existe em `features/catalog/models/catalog.models.ts` - **mover para `shared/models/` ou importar diretamente** para reutilizacao no `WorkService`

### Componente: WorkDetailComponent

**Secao 1 - Header da Obra (topo)**
- Layout: imagem da capa a esquerda, informacoes a direita
- Capa: imagem com aspect-ratio de poster (~2:3), com fallback para placeholder
- Titulo principal em `<h1>` com fonte grande
- Titulos alternativos em texto menor abaixo
- Badges para: tipo, status, ano, content rating, demographic
- Lista de tags como chips clicaveis (cores por grupo - usar `GENRE_COLORS` de `shared/components/manhwa-card/manhwa-card.ts`)
- Autores com icone por tipo (caneta para writer, pincel para artist)
- Sinopse com "ver mais" / "ver menos" se texto longo (> 3 linhas)
- Background: blur da capa como fundo do header (gradiente escuro para legibilidade)

**Secao 2 - Barra de Acoes**
- Botao "Adicionar a Biblioteca" (dropdown com opcoes de status: READING, COMPLETED, PLAN_TO_READ, DROPPED)
- Botao "Avaliar" (estrelas ou nota 1-10)
- Links externos como icones clicaveis (mapear `siteCode` para icones/nomes legíveis)

**Secao 3 - Lista de Capitulos**
- Header da lista com:
  - Titulo "Capitulos" com contagem total
  - Toggle de ordenacao (mais recente / mais antigo)
  - Filtro de idioma (se houver multiplos)
- Cada linha de capitulo mostra:
  - Numero do capitulo (link para pagina de leitura)
  - Titulo (se houver)
  - Nome do scanlator
  - Data de publicacao (formato relativo: "ha 2 dias", "ha 1 semana")
  - Indicador de status: verde (lido), cinza (nao lido)
  - Botoes de acao (marcar lido/nao lido)
- Scroll infinito (reutilizar padrao do `CatalogListComponent` com IntersectionObserver)
- Skeleton loading para items carregando

### Servico: WorkService
```typescript
@Injectable({ providedIn: 'root' })
export class WorkService {
  private readonly http = inject(HttpClient);

  getWorkDetail(slug: string): Observable<WorkDetail> {
    return this.http.get<WorkDetail>(`${environment.apiUrl}/works/${slug}`);
  }

  getChapters(slug: string, page: number, size: number, sort: string, language?: string): Observable<PageResponse<ChapterItem>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', sort);
    if (language) params = params.set('language', language);
    return this.http.get<PageResponse<ChapterItem>>(`${environment.apiUrl}/works/${slug}/chapters`, { params });
  }

  addToLibrary(slug: string, status: string): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/works/${slug}/library`, { status });
  }

  removeFromLibrary(slug: string): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/works/${slug}/library`);
  }

  markChapterRead(slug: string, chapterId: string): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/works/${slug}/chapters/${chapterId}/read`, {});
  }

  markChapterUnread(slug: string, chapterId: string): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/works/${slug}/chapters/${chapterId}/read`);
  }
}
```

### Models/Interfaces
```typescript
export interface WorkDetail {
  id: string;
  slug: string;
  title: string;
  alternativeTitles: AlternativeTitle[];
  synopsis: string;
  coverUrl: string;
  type: string;
  status: string;
  releaseYear: number;
  contentRating: string;
  publicationDemographic: string;
  originalLanguage: string;
  tags: WorkTagItem[];
  authors: WorkAuthorItem[];
  links: WorkLinkItem[];
  chapterCount: number;
  userLibraryStatus: string | null;
  userRating: number | null;
}

export interface AlternativeTitle {
  title: string;
  language: string;    // codigo do idioma (ex: "en", "ko", "pt-br")
  isOfficial: boolean;
}

export interface WorkTagItem {
  name: string;
  group: string;       // GENRE, THEME, FORMAT, CONTENT
}

export interface WorkAuthorItem {
  name: string;
  type: string;        // WRITER, ARTIST, AUTHOR
}

export interface WorkLinkItem {
  siteCode: string;    // ANI_LIST, ANIME_PLANET, NOVEL_UPDATES, MY_ANIME_LIST, MANGA_UPDATES, KITSU, MANGADEX
  url: string;
}

export interface ChapterItem {
  id: string;
  number: string;
  numberFormatted: string;
  numberWithVersion: string;  // ex: "123", "124.1"
  title: string | null;
  language: string;           // codigo do idioma (ex: "pt-br")
  releaseDate: string;        // LocalDate ISO
  scanlator: string;          // nome do scanlator
  volume: number | null;      // numero do volume ou null
  isRead: boolean;
  readProgress: number;
  publishedAt: string;        // OffsetDateTime ISO
}
```

### Estilo Visual (seguir tema escuro do projeto)
- Background principal: `#0d0d0d` a `#1a1a1a`
- Cards/containers: `#1e1e2e` com border `#2a2a3a`
- Texto principal: `#e0e0e0`
- Texto secundario: `#8888a0`
- Accent color: laranja/vermelho (seguir paleta existente)
- Tags com cores por grupo (reutilizar `GENRE_COLORS` de `manhwa-card.ts`)
- Botoes de acao: verde (#22c55e) para lido, vermelho (#ef4444) para remover, azul (#3b82f6) para download
- Responsivo: mobile-first, stack vertical em telas pequenas
- Transicoes suaves para expandir sinopse e hover em capitulos

---

## Observacoes de Implementacao

1. **Seguir padroes existentes**: Usar o mesmo padrao de DTOs (records), services, resources e specifications ja utilizado no projeto
2. **MinIO Storage**: URLs de capa seguem o padrao `{minioUrl}/{bucketName}/{demographic}/{slug}/covers/{filename}` - reutilizar `Work.getCoverUrl()` que ja monta o path relativo
3. **Seguranca**: Endpoints protegidos com `@PreAuthorize("isAuthenticated()")`
4. **Usuario autenticado**: Usar `@AuthenticationPrincipal UserDetails userDetails` nos controllers. O `username` do UserDetails e o **email** do usuario. Buscar a entidade `User` via `userRepository.findByEmail(userDetails.getUsername())`. As entidades `Library`, `ReadingProgress` e `Rating` possuem unique constraints - sempre filtrar pelo usuario logado
5. **Performance**: Usar `@Transactional(readOnly = true)` nas queries. Para o Endpoint 1 (work detail), considerar JOIN FETCH para evitar N+1 nas colecoes (titles, synopses, tags, authors, links, covers). Para o Endpoint 2 (chapters), usar query otimizada com `WHERE chapter_id IN (:ids) AND user_id = :userId` no ReadingProgress
6. **Capitulos desabilitados**: Sempre filtrar `disabled = false` nas queries de capitulos
7. **SSR Angular**: Usar `isPlatformBrowser()` antes de acessar DOM/window (padrao ja usado no `CatalogListComponent`)
8. **Internacionalizacao**: Sinopse e titulos sao multilinguais via relacao com `Language` - priorizar idioma do usuario, fallback para portugues ("pt-br"), depois ingles ("en")
9. **Numero do capitulo com versao**: Usar `chapter.getNumberWithVersionInteger()` para exibicao (ex: "123", "124.1"), `numberFormatted` para ordenacao
