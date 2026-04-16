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
  { value: '', labelKey: 'enum.libraryStatus.ALL' },
  { value: 'READING', labelKey: 'enum.libraryStatus.READING' },
  { value: 'COMPLETED', labelKey: 'enum.libraryStatus.COMPLETED' },
  { value: 'PLAN_TO_READ', labelKey: 'enum.libraryStatus.PLAN_TO_READ' },
  { value: 'DROPPED', labelKey: 'enum.libraryStatus.DROPPED' },
];
