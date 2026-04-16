# Prompt para refatorar toda a experiencia do frontend

Atue como um especialista senior em UX/UI, Angular e arquitetura frontend para plataformas de leitura de manhwa/webtoon.

Quero que voce refatore a experiencia completa do frontend do projeto ManhwaReader, cobrindo todas as paginas e componentes HTML do frontend em `frontend/src/app/**/*.html`, com foco em leitura intensiva, navegacao mobile-first, consistencia visual, performance de imagens e internacionalizacao.

## Contexto do projeto

- Projeto frontend em Angular 21.
- Templates atuais em arquivos `.html` dentro de `frontend/src/app`.
- O site e uma plataforma para leitores de manhwa/webtoon.
- O usuario principal consome conteudo visual por longos periodos, principalmente no mobile.
- O projeto ja possui autenticacao, usuario logado, biblioteca, catalogo, leitura de capitulos e area administrativa.
- A rota `/admin` ja existe e deve continuar protegida por `authGuard` e `adminGuard`.
- Usuarios administradores sao identificados pelo perfil com role `ADMINISTRATOR`.

## Arquivos/paginas que devem ser considerados

Refatore a experiencia nas paginas e componentes do frontend:

- `frontend/src/app/app.html`
- `frontend/src/app/features/auth/login/login.html`
- `frontend/src/app/features/auth/register/register.html`
- `frontend/src/app/features/home/home.html`
- `frontend/src/app/features/catalog/catalog-list/catalog-list.html`
- `frontend/src/app/features/library/library-list/library-list.html`
- `frontend/src/app/features/work/work-detail/work-detail.html`
- `frontend/src/app/features/work/chapter-reader/chapter-reader.html`
- `frontend/src/app/features/admin/synchronization/synchronization.html`
- `frontend/src/app/shared/components/navbar/navbar.html`
- `frontend/src/app/shared/components/featured-carousel/featured-carousel.html`
- `frontend/src/app/shared/components/manhwa-card/manhwa-card.html`

Nao altere arquivos gerados de `dist`, `node_modules`, `target` ou templates de email do backend, a menos que seja explicitamente necessario e justificado.

## Objetivo principal

Transformar o ManhwaReader em uma experiencia moderna, responsiva, coesa e funcional para leitores de manhwa, mantendo a base Angular existente e melhorando a arquitetura para evolucao futura.

O resultado deve parecer um produto real de leitura, nao apenas uma pagina bonita.

## Direcao de UX

Priorize:

- Mobile-first real.
- Interface limpa durante a leitura.
- Navegacao simples entre home, catalogo, biblioteca, detalhes da obra, capitulos e administracao.
- Consistencia visual entre cards, filtros, listas, formularios, menus, estados vazios, loaders e mensagens.
- Reducao de friccao para continuar lendo.
- Layout estavel, sem saltos visuais quando imagens carregam.
- Performance em paginas com muitas capas ou paginas de capitulo.
- Acessibilidade basica: foco visivel, contraste, aria-labels, navegacao por teclado e textos alternativos.

Evite:

- Refatoracao superficial apenas de cores e espacamento.
- Layouts genericos de dashboard.
- Cards dentro de cards sem necessidade.
- Elementos decorativos que prejudiquem leitura.
- Animacoes excessivas na tela de leitura.
- Dependencias novas sem justificativa clara.

## Escopo de refatoracao por area

### 1. Shell, navegacao e identidade

Refatore `app.html` e `navbar` para criar uma estrutura consistente de aplicacao.

Requisitos:

- Navbar responsiva com acesso claro a Home, Catalogo, Biblioteca e area administrativa quando o usuario for admin.
- Menu do usuario acessivel, com estados de foco e fechamento previsivel.
- Item "Administracao" deve aparecer somente para role `ADMINISTRATOR`.
- A rota `/admin` deve permanecer protegida por `authGuard` e `adminGuard`.
- Textos fixos do menu devem usar i18n.
- Evitar que o navbar atrapalhe a leitura no `chapter-reader`; nessa tela, usar uma experiencia especifica de leitor.

### 2. Login e cadastro

Refatore `login.html` e `register.html`.

Requisitos:

- Formularios claros, responsivos e acessiveis.
- Estados de erro, loading e validacao consistentes.
- Suporte ao login Google existente, quando disponivel.
- Textos, placeholders, validacoes e botoes via i18n.
- Melhorar microcopy para contexto de leitura, sem linguagem promocional exagerada.
- Manter Reactive Forms e a logica atual de autenticacao.

### 3. Home

Refatore `home.html`.

Requisitos:

- Priorizar "Continue lendo" como area de alto valor para usuario logado, mantenha a ideia de mostrar a quantidade de capitulos não lisdos em um badge .
- Destacar atualizacoes recentes sem sobrecarregar a tela.
- Melhorar o carrossel/destaques para mobile.
- Tratar estado "tudo em dia" como feedback positivo e discreto.
- Footer simples e traduzido.
- Scroll-to-top acessivel e sem ocupar area importante no mobile.

### 4. Catalogo

Refatore `catalog-list.html`.

Requisitos:

- Busca e filtros devem ser faceis no mobile.
- Filtros ativos devem ser visiveis e removiveis.
- Estados de loading, vazio e carregamento incremental devem ser claros.
- Infinite scroll deve continuar usando o padrao atual com `IntersectionObserver`.
- Textos fixos e labels de filtros devem ir para i18n.
- Labels de status, tipo, demografia e ordenacao devem ser traduzidos por chave, sem hardcode em arrays.

### 5. Biblioteca

Refatore `library-list.html`.

Requisitos:

- Tabs/status de leitura devem ser claras e traduzidas.
- Estado vazio deve orientar o usuario a procurar obras no catalogo.
- Mantenha a ideia de mostrar a quantidade de capitulos não lisdos em um badge .
- Cards devem indicar progresso, capitulos nao lidos e estado na biblioteca sem poluir a interface.
- Infinite scroll deve permanecer eficiente.


### 6. Detalhe da obra

Refatore `work-detail.html`.

Requisitos:

- Hero da obra deve valorizar capa, titulo, autores, tags, status, ano, demografia e sinopse.
- Sinopse deve ter expansao/colapso acessivel.
- Capas e titulos alternativos devem ter comportamento responsivo.
- Acao de adicionar/remover da biblioteca deve ser clara.
- Lista de capitulos deve ser facil de escanear, com estado lido/nao lido, scanlator e data.
- Acoes "marcar todos", ordenar, abrir capitulo e marcar individualmente devem manter estados de loading e disabled.
- Datas relativas devem ser internacionalizadas usando `Intl.RelativeTimeFormat` ou helper i18n equivalente.
- Textos como "Cap.", "Ver mais", "Marcar como lido", "Nenhum capitulo disponivel" devem usar i18n.


### 7. Leitor de capitulos

Refatore `chapter-reader.html` com prioridade maxima em UX de leitura.

Requisitos:

- Leitura vertical continua estilo webtoon.
- Header minimo, preferencialmente ocultavel/reduzido durante scroll.
- Controles discretos para voltar, capitulo anterior, proximo capitulo e progresso.
- Area de leitura centralizada, com largura adequada para mobile, tablet e desktop.
- Imagens com `loading="lazy"` quando adequado, dimensoes/containers estaveis para reduzir CLS.
- Preload inteligente da proxima imagem ou proximo capitulo quando possivel.
- Estados de loading, erro e capitulo nao encontrado traduzidos.
- Suporte a paginas Markdown ja existente deve ser mantido.
- Nao adicionar elementos visuais que distraiam da leitura.

### 8. Cards e carrossel

Refatore `manhwa-card` e `featured-carousel`.

Requisitos:

- Cards devem ter dimensoes estaveis e aspecto consistente para capas.
- Badges de novo, nao lidos, idioma e progresso devem ser legiveis.
- Titulos longos nao podem quebrar o layout.
- O carrossel deve funcionar bem em toque/mobile e teclado.
- Evitar dependencia de hover como unica forma de interacao.
- Textos fixos como `NEW`, `Ch.` e titulos de secoes devem usar i18n.

## Internacionalizacao i18n

Adicione i18n ao frontend para armazenar todos os textos fixos do site.

### Requisitos obrigatorios

- As chaves do i18n devem ser em ingles.
- Valores iniciais devem contemplar pelo menos `pt-BR`.
- Se for simples adicionar, tambem criar `en-US` como segundo idioma.
- Todos os textos fixos de templates HTML e mensagens em TypeScript devem sair do hardcode.
- Nomes dinamicos vindos da API, como titulo da obra, autor, tag e scanlator, nao devem ser traduzidos.
- Codigos/enums vindos da API devem ser convertidos por chave i18n quando forem exibidos ao usuario.
- O idioma padrao deve ser `pt-BR`.
- A escolha de idioma deve ficar pronta para ser persistida em `localStorage`.
- Nao usar bibliotecas externas de i18n sem justificar. Se optar por dependencia externa, explicar por que e atualizar o projeto corretamente.

### Estrutura sugerida

Preferir uma solucao leve e compativel com Angular standalone:

- `frontend/src/app/core/i18n/i18n.service.ts`
- `frontend/src/app/core/i18n/translate.pipe.ts`
- `frontend/src/app/core/i18n/i18n.models.ts`
- `frontend/src/assets/i18n/pt-BR.json`
- `frontend/src/assets/i18n/en-US.json`

Exemplo de padrao de chave:

```json
{
  "navigation.home": "Inicio",
  "navigation.catalog": "Catalogo",
  "navigation.library": "Biblioteca",
  "navigation.admin": "Administracao",
  "auth.login.title": "Bem-vindo de volta",
  "auth.login.submit": "Entrar",
  "catalog.filters.title": "Filtros",
  "catalog.empty": "Nenhuma obra encontrada.",
  "reader.previousChapter": "Anterior",
  "reader.nextChapter": "Proximo",
  "admin.settings.title": "Configuracoes do site"
}
```

Uso esperado nos templates:

```html
{{ 'navigation.catalog' | translate }}
```

Uso esperado no TypeScript:

```ts
this.i18n.t('work.readingStatus.reading')
```

### Areas que devem ser traduzidas

- Navbar e menu do usuario.
- Login, cadastro, validacoes e botoes.
- Home, secoes, estados vazios e footer.
- Catalogo: busca, filtros, ordenacao, estados e scroll.
- Biblioteca: tabs, estados e mensagens.
- Detalhe da obra: acoes, capitulos, labels, datas relativas e estados.
- Leitor: navegacao, alt text padrao, loading e erros.
- Admin: titulos, tabs, botoes, modais, mensagens, filtros e logs.
- Labels de enums: status de obra, status de biblioteca, tipos, demografias, content rating e ordenacao.

## Area administrativa e configuracoes do site

Evolua a area administrativa atual para uma area de configuracao do site acessivel somente por administradores.

### Requisitos de acesso

- A area deve continuar em `/admin`.
- Usar `authGuard` e `adminGuard`.
- A navbar deve exibir o link de administracao somente quando `userService.profile()?.roles` incluir `ADMINISTRATOR`.
- Usuarios sem permissao devem ser redirecionados para `/home` ou receber tela de acesso negado, conforme padrao do projeto.

### Estrutura sugerida

Criar uma area admin com layout proprio e navegacao interna:

- `/admin` ou `/admin/settings`: Visao geral/configuracoes do site.
- `/admin/synchronization`: Sincronizacao de obras.
- `/admin/import-logs`: Logs de importacao das scans.
- `/admin/customization`: Personalizacao de areas do site.

Se o backend ainda nao tiver endpoints para alguma funcionalidade, criar a estrutura frontend preparada para integracao futura, com estados vazios, mocks locais controlados ou TODOs claros sem quebrar build.

### Funcionalidades iniciais

#### Configuracoes gerais

- Nome do site.
- Idioma padrao.
- Tema padrao.
- Controle de exibicao de secoes da home.
- Preferencias de leitura padrao quando aplicavel.
- Mensagens de estado vazio configuraveis, se fizer sentido.

#### Sincronizacao

Manter e reorganizar a funcionalidade atual de sincronizacao:

- Obras sem vinculo.
- Busca no MangaDex.
- Comparacao de capas.
- Vinculo com MangaDex.
- Obras vinculadas.
- Acao de sincronizar manualmente.
- Estados de sucesso, erro, loading e vazio.

#### Logs de importacao das scans

Criar tela preparada para logs:

- Lista de importacoes.
- Status: sucesso, erro, parcial, em andamento.
- Origem/scan.
- Obra e capitulo quando disponivel.
- Data/hora.
- Mensagem resumida.
- Area expansivel para detalhes tecnicos.
- Filtros por status, origem, obra e periodo.

#### Personalizacao

Criar tela preparada para personalizacao:

- Secoes habilitadas/desabilitadas na home.
- Ordem das secoes principais.
- Quantidade de itens em carrosseis/listas.
- Modo visual padrao.
- Preferencias do leitor.
- Troca do logo do site
- Troca das cores/tema do site


## Arquitetura e padroes tecnicos

Siga os padroes existentes do projeto:

- Angular standalone components.
- Signals quando ja forem usados.
- Lazy loading por rotas.
- Services para chamadas HTTP.
- Separar models, services, routes, templates e estilos por feature.
- Manter nomes consistentes com a base atual.
- Evitar refatoracoes grandes sem necessidade fora do escopo.
- Nao alterar contratos de API sem confirmar.

Quando criar novos componentes, preferir estrutura:

- `features/admin/settings`
- `features/admin/import-logs`
- `features/admin/customization`
- `core/i18n`
- `shared/components` para componentes realmente reutilizaveis.

## Performance

Requisitos:

- Lazy loading de imagens.
- Aspect ratio estavel para capas e paginas.
- Evitar layout shifts.
- Evitar renderizacao pesada em listas grandes.
- Manter infinite scroll com `IntersectionObserver`.
- Evitar recalculos caros em templates.
- Usar `track` em loops Angular.
- Usar skeletons quando carregamento for perceptivel.
- Otimizar o leitor de capitulos para imagens longas.

## Acessibilidade

Requisitos:

- Botoes com `type="button"` quando nao forem submit.
- `aria-label` em botoes icon-only.
- Foco visivel em links, botoes e inputs.
- Contraste suficiente no tema.
- Modais com semantica adequada e fechamento por teclado quando possivel.
- Estados disabled claros.
- Textos alternativos adequados para capas e paginas.

## Entrega esperada

Entregue a refatoracao com:

- Codigo atualizado nos templates HTML necessarios.
- Estilos CSS correspondentes.
- Estrutura i18n funcional.
- Dicionarios de traducao com chaves em ingles.
- Rotas admin reorganizadas.
- Componentes/telas admin iniciais para configuracoes, sincronizacao, logs de importacao e personalizacao.
- Ajustes necessarios nos arquivos TypeScript para usar i18n, novos routes e novos estados.
- Build do frontend passando.
- Breve resumo das mudancas e pontos pendentes.

## Validacao obrigatoria

Antes de finalizar:

- Rodar `npm run build` dentro de `frontend`.
- Verificar que nao foram alterados arquivos de `dist`, `node_modules` ou `target`.
- Verificar que todos os textos fixos visiveis foram migrados para i18n.
- Verificar que a area admin nao aparece para usuarios nao administradores.
- Verificar que `/admin` e subrotas continuam protegidas.
- Verificar responsividade minima em mobile, tablet e desktop.
- Conferir estados vazios, loading e erro nas principais telas.

## Perguntas antes de implementar, se houver duvida

Antes de mexer em backend ou contratos de API, pergunte.

Pergunte tambem se precisar decidir:

- Qual biblioteca/estrategia de i18n usar caso a solucao leve nao atenda.
- Quais endpoints existem para logs de importacao e configuracoes.
- Quais configuracoes do site devem ser persistidas no backend agora.
- Quais idiomas alem de `pt-BR` e `en-US` devem ser suportados.
- Se a administracao deve ser uma unica tela com abas ou subrotas separadas.

Nao pergunte sobre detalhes que possam ser resolvidos seguindo os padroes atuais do projeto.
