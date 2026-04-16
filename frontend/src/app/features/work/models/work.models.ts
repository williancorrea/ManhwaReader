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
  originalLanguageFlag: string | null;
  tags: WorkTagItem[];
  authors: WorkAuthorItem[];
  links: WorkLinkItem[];
  chapterCount: number;
  userLibraryStatus: string | null;
  userRating: number | null;
}

export interface AlternativeTitle {
  title: string;
  language: string | null;
  languageFlag: string | null;
  isOfficial: boolean;
}

export interface WorkTagItem {
  name: string;
  group: string;
}

export interface WorkAuthorItem {
  name: string;
  type: string;
}

export interface WorkLinkItem {
  siteCode: string;
  url: string;
}

export interface ChapterItem {
  id: string;
  number: string;
  numberFormatted: string;
  numberWithVersion: string;
  title: string | null;
  language: string;
  releaseDate: string;
  scanlator: string;
  volume: number | null;
  isRead: boolean;
  readProgress: number;
  publishedAt: string;
}

export const LIBRARY_STATUSES = [
  { value: 'READING', labelKey: 'enum.libraryStatus.READING' },
  { value: 'COMPLETED', labelKey: 'enum.libraryStatus.COMPLETED' },
  { value: 'PLAN_TO_READ', labelKey: 'enum.libraryStatus.PLAN_TO_READ' },
  { value: 'DROPPED', labelKey: 'enum.libraryStatus.DROPPED' },
];

export interface ChapterReaderData {
  id: string;
  number: string;
  numberWithVersion: string;
  title: string | null;
  workTitle: string;
  workSlug: string;
  pages: ChapterPage[];
  previousChapter: ChapterNav | null;
  nextChapter: ChapterNav | null;
}

export interface ChapterPage {
  pageNumber: number;
  type: 'IMAGE' | 'MARKDOWN';
  imageUrl: string | null;
  content: string | null;
}

export interface ChapterNav {
  id: string;
  numberWithVersion: string;
}

export const SITE_LABELS: Record<string, string> = {
  MANGADEX: 'MangaDex',
  MY_ANIME_LIST: 'MyAnimeList',
  ANI_LIST: 'AniList',
  ANIME_PLANET: 'Anime-Planet',
  NOVEL_UPDATES: 'Novel Updates',
  MANGA_UPDATES: 'MangaUpdates',
  KITSU: 'Kitsu',
};
