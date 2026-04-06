import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { PageResponse, WorkCatalogItem } from '../models/catalog.models';

@Injectable({ providedIn: 'root' })
export class CatalogService {
  private readonly http = inject(HttpClient);

  listar(page: number = 0, size: number = 20): Observable<PageResponse<WorkCatalogItem>> {
    return this.http.get<PageResponse<WorkCatalogItem>>(
      `${environment.apiUrl}/works?page=${page}&size=${size}`
    );
  }
}
