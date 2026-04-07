import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { CatalogFilter, PageResponse, WorkCatalogItem } from '../models/catalog.models';

@Injectable({ providedIn: 'root' })
export class CatalogService {
  private readonly http = inject(HttpClient);

  listar(page: number = 0, size: number = 20, filter?: CatalogFilter): Observable<PageResponse<WorkCatalogItem>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (filter?.title) params = params.set('title', filter.title);
    if (filter?.type) params = params.set('type', filter.type);
    if (filter?.publicationDemographic) params = params.set('publicationDemographic', filter.publicationDemographic);
    if (filter?.status) params = params.set('status', filter.status);
    if (filter?.sort) params = params.set('sort', filter.sort);
    return this.http.get<PageResponse<WorkCatalogItem>>(`${environment.apiUrl}/works`, { params });
  }
}
