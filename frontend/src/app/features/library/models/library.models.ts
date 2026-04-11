export interface LibraryItem {
  workId: string;
  slug: string;
  title: string;
  coverUrl: string;
  publicationDemographic: string;
  workStatus: string;
  chapterCount: number;
  libraryStatus: string;
  unreadCount: number;
  originalLanguageCode: string | null;
  originalLanguageFlag: string | null;
}

export const LIBRARY_STATUSES = [
  { value: '', label: 'Todos' },
  { value: 'READING', label: 'Lendo' },
  { value: 'COMPLETED', label: 'Completo' },
  { value: 'PLAN_TO_READ', label: 'Planejo Ler' },
  { value: 'DROPPED', label: 'Abandonado' },
];
