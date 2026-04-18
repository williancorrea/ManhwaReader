export interface SynchronizationDetail {
  origin: string;
  externalId: string;
}

export interface AdminWorkItem {
  id: string;
  title: string;
  titles: string[];
  slug: string;
  status: string;
  type: string;
  coverUrl: string;
  synchronizationOrigins: string[];
  synchronizations: SynchronizationDetail[];
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

export type SynchronizationOriginType =
  | 'MANGADEX'
  | 'LYCANTOONS'
  | 'MANGOTOONS'
  | 'MEDIOCRESCAN';

export interface SyncError {
  id: string;
  scanlatorId: string | null;
  scanlatorName: string | null;
  scanlatorCode: string | null;
  scanlatorWebsite: string | null;
  synchronization: SynchronizationOriginType | null;
  workId: string | null;
  externalWorkId: string;
  externalWorkName: string;
  errorMessage: string | null;
  stackTrace: string | null;
  createdAt: string;
}

export interface SyncErrorSummary {
  total: number;
  last24Hours: number;
  last7Days: number;
  byOrigin: Partial<Record<SynchronizationOriginType, number>>;
}

export interface SyncErrorFilters {
  synchronization?: SynchronizationOriginType;
  externalWorkName?: string;
  externalWorkId?: string;
  errorMessage?: string;
  from?: string;
  to?: string;
  orphansOnly?: boolean;
}
