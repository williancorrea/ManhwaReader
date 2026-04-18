import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  PageResponse,
  SyncError,
  SyncErrorFilters,
  SyncErrorSummary
} from '../models/admin.models';

@Injectable({ providedIn: 'root' })
export class AdminSyncErrorsService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/admin/sync-errors`;

  list(
    filters: SyncErrorFilters,
    page = 0,
    size = 20,
    sort = 'createdAt,desc'
  ): Observable<PageResponse<SyncError>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', sort);

    if (filters.synchronization) params = params.set('synchronization', filters.synchronization);
    if (filters.externalWorkName) params = params.set('externalWorkName', filters.externalWorkName);
    if (filters.externalWorkId) params = params.set('externalWorkId', filters.externalWorkId);
    if (filters.errorMessage) params = params.set('errorMessage', filters.errorMessage);
    if (filters.from) params = params.set('from', filters.from);
    if (filters.to) params = params.set('to', filters.to);
    if (filters.orphansOnly) params = params.set('orphansOnly', true);

    return this.http.get<PageResponse<SyncError>>(this.baseUrl, { params });
  }

  summary(): Observable<SyncErrorSummary> {
    return this.http.get<SyncErrorSummary>(`${this.baseUrl}/summary`);
  }

  getById(id: string): Observable<SyncError> {
    return this.http.get<SyncError>(`${this.baseUrl}/${id}`);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  deleteMany(ids: string[]): Observable<void> {
    return this.http.delete<void>(this.baseUrl, { body: { ids } });
  }

  purgeOlderThan(days: number): Observable<void> {
    const params = new HttpParams().set('days', days);
    return this.http.delete<void>(`${this.baseUrl}/older-than`, { params });
  }
}
