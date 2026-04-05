# Design System — Manhwa Reader (Dark Theme)

## Filosofia

Theme-first escuro. O objetivo é leitura prolongada sem fadiga visual. Todos os backgrounds carregam um subtom azul-violeta (nunca cinza puro) para coesão com a primary roxa. Controles do reader são quase invisíveis até interação. Cor é usada com moderação — destaque perde valor quando tudo destaca.

---

## 1. Backgrounds

Hierarquia de profundidade por camada. Quanto mais "acima" o elemento, mais claro o background.

| Token | Hex | Uso |
|---|---|---|
| `bg-00` | `#0D0D0F` | Página base, body |
| `bg-01` | `#141418` | Sidebar, nav lateral |
| `bg-02` | `#1A1A20` | Header, footer |
| `bg-03` | `#222228` | Seções alternadas, separadores visuais |
| `bg-04` | `#2A2A32` | Áreas recuadas, blocos de código |
| `bg-05` | `#32323C` | Tooltips, popovers |

**Regra:** nunca pule mais de 2 níveis de profundidade entre elementos adjacentes. A transição deve ser gradual.

---

## 2. Surfaces

Para elementos elevados: cards, modais, dropdowns, drawers.

| Token | Hex | Uso |
|---|---|---|
| `surface-00` | `#18181E` | Card padrão (card de manhwa na listagem) |
| `surface-01` | `#1E1E26` | Card em destaque, modal body |
| `surface-02` | `#26262E` | Dropdown menu, popover content |
| `surface-hover` | `#2E2E38` | Hover state de qualquer surface |
| `surface-active` | `#363642` | Estado pressed/active |

**Regra:** surfaces sempre devem ter pelo menos 1 nível de diferença do background onde estão posicionados. Card (`surface-00`) sobre `bg-00` funciona. Card (`surface-00`) sobre `bg-01` tem contraste insuficiente — use `surface-01`.

---

## 3. Bordas

| Token | Hex | Uso |
|---|---|---|
| `border-subtle` | `#2A2A34` | Separadores internos, divisores de lista |
| `border-default` | `#3A3A46` | Borda de inputs, cards, containers |
| `border-strong` | `#4A4A58` | Borda com ênfase, campos em foco (sem glow) |

**Regra:** bordas são opcionais em cards quando o contraste surface/background já é suficiente. Prefira separação por espaço e cor antes de adicionar bordas. Inputs sempre têm `border-default`.

---

## 4. Texto

| Token | Hex | Contraste em bg-00 | Uso |
|---|---|---|---|
| `text-primary` | `#F0F0F2` | ~18:1 | Títulos, corpo principal, nomes de manhwa |
| `text-secondary` | `#B0B0BA` | ~10:1 | Subtítulos, meta info (autor, capítulos) |
| `text-muted` | `#76768A` | ~5:1 | Timestamps, contadores, placeholders |
| `text-disabled` | `#4E4E60` | ~2.5:1 | Texto em estado disabled |

**Regra:** `text-muted` é o nível mínimo aceitável para texto legível (WCAG AA para texto grande). Nunca use `text-disabled` para informação que o usuário precisa ler — é exclusivo para estados desabilitados.

---

## 5. Primary — Roxo

Cor principal de interação. Usada em CTAs, links, elementos de navegação ativos, barra de progresso de leitura.

| Token | Hex | Uso |
|---|---|---|
| `primary` | `#7C5CFC` | Botões primários, links, ícones ativos |
| `primary-hover` | `#6A48E6` | Hover de botões e links |
| `primary-active` | `#5836CC` | Estado pressed |
| `primary-ghost` | `rgba(124,92,252,0.12)` | Background de botões ghost, badges sutis |
| `primary-subtle` | `rgba(124,92,252,0.20)` | Background de seleção, highlight de busca |

**Onde usar:**
- Botão "Ler agora", "Continuar lendo"
- Tab/nav ativa (text + underline ou pill background)
- Barra de progresso no reader
- Links inline no texto
- Ícone de favorito quando ativo

**Onde NÃO usar:**
- Textos longos em primary — cansa a vista
- Background de áreas grandes — o roxo em área grande compete com o conteúdo (capas de manhwa)

---

## 6. Accent — Verde-teal

Contraponto cromático ao primary. Indica novidade, progresso, e informação positiva sem ser "success".

| Token | Hex | Uso |
|---|---|---|
| `accent` | `#00D4AA` | Badge "novo capítulo", indicadores de progresso |
| `accent-hover` | `#00B892` | Hover |
| `accent-ghost` | `rgba(0,212,170,0.12)` | Background de badges "novo" |
| `accent-subtle` | `rgba(0,212,170,0.20)` | Highlight de novidades na listagem |

**Onde usar:**
- Badge "Novo" ao lado de capítulos recentes
- Indicador de capítulos não lidos (dot ou counter)
- Barra de progresso de leitura (alternativa ao primary quando há dois indicadores simultâneos)
- Status "Em publicação"

**Onde NÃO usar:**
- Substituindo success em formulários — para isso existe a cor de success
- Em textos corridos — contraste insuficiente para leitura prolongada em bg escuro

---

## 7. Alerts e Status

Uso exclusivo para feedback do sistema. Nunca como decoração.

| Token | Hex | BG (12% opacity) | Uso |
|---|---|---|---|
| `success` | `#22C55E` | `rgba(34,197,94,0.12)` | Ações concluídas, download completo, login OK |
| `error` | `#EF4444` | `rgba(239,68,68,0.12)` | Erros de formulário, falha de carregamento |
| `warning` | `#F59E0B` | `rgba(245,158,11,0.12)` | Avisos, conteúdo sensível, rate limit |
| `info` | `#3B82F6` | — | Dicas, informações contextuais |

**Padrão de uso em alertas:**
- Background: variante 12% opacity
- Borda esquerda (4px): cor sólida
- Texto do alert: `text-primary`
- Ícone: cor sólida

---

## 8. Reader (modo leitura)

Contexto separado do resto da aplicação. Prioridade absoluta: zero distração.

| Token | Hex | Uso |
|---|---|---|
| `reader-bg` | `#000000` | Fundo do reader (preto puro para OLED e contraste máximo) |
| `reader-overlay` | `#0A0A0C` | Background do header/footer do reader quando visível |
| `reader-controls-idle` | `rgba(255,255,255,0.06)` | Botões e controles em repouso |
| `reader-controls-hover` | `rgba(255,255,255,0.12)` | Hover dos controles |
| `reader-progress-bar` | `#7C5CFC` | Barra de progresso de página/capítulo |
| `reader-progress-track` | `rgba(124,92,252,0.30)` | Trilha da barra de progresso |

**Comportamento:**
- Header e footer do reader começam ocultos. Aparecem com tap/click e somem após 3s de inatividade.
- Controles usam opacidades ultra baixas para não competir com o conteúdo.
- Transições de visibilidade: `opacity` com `transition: 300ms ease`.
- Barra de progresso sempre visível na parte inferior (2-3px de altura). Única exceção ao princípio de "esconder tudo".

---

## 9. Tags de Gênero

Cada gênero tem uma cor fixa para reconhecimento rápido.

| Gênero | Hex | Gênero | Hex |
|---|---|---|---|
| Action | `#EF4444` | Romance | `#EC4899` |
| Fantasy | `#8B5CF6` | Sci-Fi | `#06B6D4` |
| Comedy | `#F59E0B` | Slice of Life | `#10B981` |
| Mystery | `#6366F1` | Horror | `#78716C` |
| Drama | `#F97316` | Sports | `#14B8A6` |
| Martial Arts | `#DC2626` | Isekai | `#A855F7` |
| School | `#38BDF8` | Psychological | `#64748B` |

**Padrão de exibição (dois modos):**

1. **Pill ghost (padrão):** background com 15% opacity da cor + texto na cor sólida. Usar na listagem, cards, páginas de detalhe.
2. **Pill sólida:** background sólido + texto branco. Usar apenas em destaque (filtro ativo, badge no header do card). Máximo 2-3 por card.

**Regra:** na listagem geral, nunca exiba mais de 3 tags por card. Se o manhwa tem 8 gêneros, mostre os 3 primeiros + "+5" em `text-muted`.

---

## 10. Botões

### Hierarquia

| Variante | Background | Texto | Borda | Quando usar |
|---|---|---|---|---|
| Primary | `#7C5CFC` | `#FFFFFF` | nenhuma | CTA principal. Um por seção visível |
| Secondary | `#26262E` | `#B0B0BA` | `#3A3A46` | Ações secundárias (favoritar, compartilhar) |
| Outline | `transparent` | `#7C5CFC` | `#7C5CFC` | Ação alternativa ao primary |
| Ghost | `transparent` | `#B0B0BA` | nenhuma | Ações terciárias, ícones de ação |
| Disabled | `#2A2A34` | `#4E4E60` | nenhuma | Qualquer botão em estado disabled |
| Danger | `#EF4444` | `#FFFFFF` | nenhuma | Ações destrutivas (deletar conta, remover favoritos em massa) |

**Regra:** máximo 1 botão primary visível por viewport. Se houver dois CTAs competindo, um vira outline ou secondary.

### Estados

- **Hover:** escurece 10-15% (primary-hover, surface-hover)
- **Active/Pressed:** escurece mais 10% (primary-active, surface-active)
- **Focus:** borda `#7C5CFC` com `box-shadow: 0 0 0 3px rgba(124,92,252,0.30)`
- **Loading:** texto substituído por spinner, botão mantém largura, pointer-events desabilitado

---

## 11. Inputs e Formulários

| Estado | Background | Borda | Texto |
|---|---|---|---|
| Default | `#18181E` | `#3A3A46` | `#F0F0F2` |
| Placeholder | `#18181E` | `#3A3A46` | `#76768A` |
| Focus | `#18181E` | `#7C5CFC` | `#F0F0F2` |
| Error | `#18181E` | `#EF4444` | `#F0F0F2` |
| Disabled | `#141418` | `#2A2A34` | `#4E4E60` |

**Regras:**
- Label acima do input, nunca dentro (placeholder não substitui label).
- Mensagem de erro abaixo do input em `error` (#EF4444), font-size menor.
- Focus ring: `box-shadow: 0 0 0 3px rgba(124,92,252,0.25)`.
- Border-radius: `8px` para todos os inputs.

---

## 12. Scrollbar

```css
::-webkit-scrollbar {
  width: 6px;
}
::-webkit-scrollbar-track {
  background: #2A2A34;
}
::-webkit-scrollbar-thumb {
  background: #4A4A58;
  border-radius: 3px;
}
::-webkit-scrollbar-thumb:hover {
  background: #5A5A6A;
}
```

Para Firefox: `scrollbar-color: #4A4A58 #2A2A34; scrollbar-width: thin;`

---

## 13. Overlay e Modal

| Token | Valor | Uso |
|---|---|---|
| `overlay-bg` | `rgba(0,0,0,0.60)` | Backdrop de modais e drawers |
| `modal-bg` | `#1E1E26` | Background do modal body |
| `modal-border` | `#3A3A46` | Borda do modal (opcional) |

**Regras:**
- Modal sempre centralizado vertical e horizontalmente.
- Fechar com click no overlay, tecla Escape, e botão X.
- Animação de entrada: `opacity 0→1` + `translateY(8px)→0` em 200ms.
- Border-radius do modal: `12px`.

---

## 14. Gradient Accent

```css
background: linear-gradient(135deg, #7C5CFC, #00D4AA);
```

**Uso estritamente limitado a:**
- Hero section / banner de destaque na home
- Loading bar / skeleton shimmer
- Branding elements (logo, ícone do app)
- Hover state de elementos premium/destaque

**Nunca usar em:** textos, backgrounds de seção inteira, bordas de cards comuns.

---

## 15. Sombras

Em dark theme, sombras tradicionais são ineficazes. Usar elevação por luminosidade.

```css
/* Evitar */
box-shadow: 0 4px 12px rgba(0,0,0,0.3);

/* Preferir */
/* Elevação via background mais claro + borda sutil */
background: var(--surface-01);
border: 1px solid var(--border-subtle);
```

Exceção: dropdowns e popovers podem usar sombra leve para separação do conteúdo abaixo:
```css
box-shadow: 0 4px 16px rgba(0,0,0,0.40);
```

---

## 16. Tipografia (recomendação)

| Elemento | Size | Weight | Color | Line-height |
|---|---|---|---|---|
| H1 (título de página) | 28px | 600 | text-primary | 1.3 |
| H2 (seção) | 22px | 600 | text-primary | 1.3 |
| H3 (sub-seção) | 18px | 500 | text-primary | 1.4 |
| Body | 15px | 400 | text-primary | 1.6 |
| Body small | 13px | 400 | text-secondary | 1.5 |
| Caption | 11px | 400 | text-muted | 1.4 |
| Badge/tag | 11px | 500 | varia | 1.0 |
| Button | 14px | 500 | varia | 1.0 |

Font stack sugerida: `'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif`

---

## 17. Espaçamento (base 4px)

| Token | Valor | Uso |
|---|---|---|
| `space-1` | 4px | Gap mínimo entre ícone e texto |
| `space-2` | 8px | Padding interno de badges/tags |
| `space-3` | 12px | Gap entre items de lista |
| `space-4` | 16px | Padding de cards, gap de grid |
| `space-5` | 20px | Margem entre seções menores |
| `space-6` | 24px | Padding de containers |
| `space-8` | 32px | Margem entre seções |
| `space-10` | 40px | Margem entre blocos principais |
| `space-12` | 48px | Padding de página (lateral) |

---

## 18. Border Radius

| Token | Valor | Uso |
|---|---|---|
| `radius-sm` | 4px | Tags, badges, chips |
| `radius-md` | 8px | Botões, inputs, cards pequenos |
| `radius-lg` | 12px | Cards de manhwa, modais |
| `radius-xl` | 16px | Containers grandes, hero sections |
| `radius-full` | 9999px | Avatares, pills, dots |

---

## 19. Transições

Padrão global: `transition: all 150ms ease`

| Contexto | Duração | Easing |
|---|---|---|
| Hover (cor, background) | 150ms | ease |
| Focus ring | 100ms | ease-out |
| Modal enter | 200ms | ease-out |
| Modal exit | 150ms | ease-in |
| Drawer slide | 250ms | ease-out |
| Reader controls show/hide | 300ms | ease |
| Skeleton shimmer | 1.5s | linear (loop) |
| Page transition | 200ms | ease-in-out |

---

## Referência rápida — CSS Custom Properties

```css
:root {
  /* Backgrounds */
  --bg-00: #0D0D0F;
  --bg-01: #141418;
  --bg-02: #1A1A20;
  --bg-03: #222228;
  --bg-04: #2A2A32;
  --bg-05: #32323C;

  /* Surfaces */
  --surface-00: #18181E;
  --surface-01: #1E1E26;
  --surface-02: #26262E;
  --surface-hover: #2E2E38;
  --surface-active: #363642;

  /* Borders */
  --border-subtle: #2A2A34;
  --border-default: #3A3A46;
  --border-strong: #4A4A58;

  /* Text */
  --text-primary: #F0F0F2;
  --text-secondary: #B0B0BA;
  --text-muted: #76768A;
  --text-disabled: #4E4E60;

  /* Primary */
  --primary: #7C5CFC;
  --primary-hover: #6A48E6;
  --primary-active: #5836CC;
  --primary-ghost: rgba(124, 92, 252, 0.12);
  --primary-subtle: rgba(124, 92, 252, 0.20);

  /* Accent */
  --accent: #00D4AA;
  --accent-hover: #00B892;
  --accent-ghost: rgba(0, 212, 170, 0.12);
  --accent-subtle: rgba(0, 212, 170, 0.20);

  /* Status */
  --success: #22C55E;
  --success-bg: rgba(34, 197, 94, 0.12);
  --error: #EF4444;
  --error-bg: rgba(239, 68, 68, 0.12);
  --warning: #F59E0B;
  --warning-bg: rgba(245, 158, 11, 0.12);
  --info: #3B82F6;

  /* Reader */
  --reader-bg: #000000;
  --reader-overlay: #0A0A0C;
  --reader-controls-idle: rgba(255, 255, 255, 0.06);
  --reader-controls-hover: rgba(255, 255, 255, 0.12);
  --reader-progress-bar: #7C5CFC;
  --reader-progress-track: rgba(124, 92, 252, 0.30);

  /* Overlay */
  --overlay-bg: rgba(0, 0, 0, 0.60);
  --modal-bg: #1E1E26;

  /* Scrollbar */
  --scrollbar-track: #2A2A34;
  --scrollbar-thumb: #4A4A58;

  /* Focus */
  --focus-ring: 0 0 0 3px rgba(124, 92, 252, 0.30);

  /* Gradient */
  --gradient-accent: linear-gradient(135deg, #7C5CFC, #00D4AA);

  /* Spacing */
  --space-1: 4px;
  --space-2: 8px;
  --space-3: 12px;
  --space-4: 16px;
  --space-5: 20px;
  --space-6: 24px;
  --space-8: 32px;
  --space-10: 40px;
  --space-12: 48px;

  /* Radius */
  --radius-sm: 4px;
  --radius-md: 8px;
  --radius-lg: 12px;
  --radius-xl: 16px;
  --radius-full: 9999px;

  /* Typography */
  --font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}
```
