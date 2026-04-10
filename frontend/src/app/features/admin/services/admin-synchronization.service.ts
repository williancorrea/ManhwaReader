import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { AdminWorkItem, MangaDexSearchItem, PageResponse } from '../models/admin.models';

@Injectable({ providedIn: 'root' })
export class AdminSynchronizationService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/admin/synchronization`;

  listWorks(page = 0, size = 20, title?: string, linkedToMangaDex?: boolean): Observable<PageResponse<AdminWorkItem>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (title) params = params.set('title', title);
    if (linkedToMangaDex !== undefined) params = params.set('linkedToMangaDex', linkedToMangaDex);
    return this.http.get<PageResponse<AdminWorkItem>>(`${this.baseUrl}/works`, { params });
  }

  searchMangaDex(title: string, page = 0, size = 5): Observable<MangaDexSearchItem[]> {
    const params = new HttpParams()
      .set('title', title)
      .set('page', page)
      .set('size', size);
    return this.http.get<MangaDexSearchItem[]>(`${this.baseUrl}/mangadex/search`, { params });
  }

  linkWorkToMangaDex(workId: string, mangaDexId: string): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/mangadex/link`, { workId, mangaDexId });
  }

  syncWorkWithMangaDex(workId: string): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/mangadex/sync/${workId}`, {});
  }
}
