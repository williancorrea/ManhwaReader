export interface WorkCatalogItem {
  title: string;
  coverUrl: string;
  publicationDemographic: string;
  status: string;
  chapterCount: number;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}
