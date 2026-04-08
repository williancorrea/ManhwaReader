import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ChapterItem, WorkDetail } from '../models/work.models';
import { PageResponse } from '../../catalog/models/catalog.models';

@Injectable({ providedIn: 'root' })
export class WorkService {
  private readonly http = inject(HttpClient);

  getWorkDetail(slug: string): Observable<WorkDetail> {
    return this.http.get<WorkDetail>(`${environment.apiUrl}/works/${slug}`);
  }

  getChapters(slug: string, page: number, size: number, sort: string, language?: string): Observable<PageResponse<ChapterItem>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', sort);
    if (language) params = params.set('language', language);
    return this.http.get<PageResponse<ChapterItem>>(`${environment.apiUrl}/works/${slug}/chapters`, { params });
  }

  addToLibrary(slug: string, status: string): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/works/${slug}/library`, { status });
  }

  removeFromLibrary(slug: string): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/works/${slug}/library`);
  }

  markChapterRead(slug: string, chapterId: string): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/works/${slug}/chapters/${chapterId}/read`, {});
  }

  markChapterUnread(slug: string, chapterId: string): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/works/${slug}/chapters/${chapterId}/read`);
  }

  markAllChaptersRead(slug: string): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/works/${slug}/chapters/read-all`, {});
  }

  markAllChaptersUnread(slug: string): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/works/${slug}/chapters/read-all`);
  }
}
