export interface SynchronizationDetail {
  origin: string;
  externalId: string;
}

export interface AdminWorkItem {
  id: string;
  title: string;
  slug: string;
  status: string;
  type: string;
  coverUrl: string;
  synchronizationOrigins: string[];
  synchronizations: SynchronizationDetail[];
  originalLanguageCode: string | null;
  originalLanguageFlag: string | null;
}

export interface MangaDexSearchItem {
  id: string;
  coverUrl: string;
  titles: string[];
}

export interface PageResponse<T> {
  content: T[];
  page: {
    size: number;
    number: number;
    totalElements: number;
    totalPages: number;
  };
}
