export interface WorkCatalogItem {
  title: string;
  coverUrl: string;
  publicationDemographic: string;
  status: string;
  chapterCount: number;
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

export interface CatalogFilter {
  title?: string;
  type?: string;
  publicationDemographic?: string;
  status?: string;
  sort?: string;
}

export const WORK_TYPES = ['MANGA', 'MANHWA', 'MANHUA', 'NOVEL'];
export const WORK_STATUSES = ['ONGOING', 'COMPLETED', 'CANCELLED', 'HIATUS'];
export const WORK_DEMOGRAPHICS = ['SHOUNEN', 'SEINEN', 'JOSEI', 'SHOUJO', 'YAOI', 'YURI', 'HENTAI', 'COMIC', 'UNKNOWN'];
export const SORT_OPTIONS = [
  { value: 'updated_at_desc', label: 'Mais recente' },
  { value: 'updated_at_asc', label: 'Mais antigo' },
];
