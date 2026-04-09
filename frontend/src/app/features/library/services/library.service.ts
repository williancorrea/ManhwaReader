import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { LibraryItem } from '../models/library.models';
import { PageResponse } from '../../catalog/models/catalog.models';

@Injectable({ providedIn: 'root' })
export class LibraryService {
  private readonly http = inject(HttpClient);

  list(page: number = 0, size: number = 20, status?: string): Observable<PageResponse<LibraryItem>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) params = params.set('status', status);
    return this.http.get<PageResponse<LibraryItem>>(`${environment.apiUrl}/library`, { params });
  }

  continueReading(size: number = 6): Observable<LibraryItem[]> {
    const params = new HttpParams().set('size', size);
    return this.http.get<LibraryItem[]>(`${environment.apiUrl}/library/continue-reading`, { params });
  }
}
