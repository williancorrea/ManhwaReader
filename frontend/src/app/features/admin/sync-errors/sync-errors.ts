import { Component, HostListener, OnDestroy, OnInit, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { debounceTime, takeUntil } from 'rxjs/operators';
import { NavbarComponent } from '../../../shared/components/navbar/navbar';
import { TranslatePipe } from '../../../core/i18n/translate.pipe';
import { I18nService } from '../../../core/i18n/i18n.service';
import { AdminNavComponent } from '../components/admin-nav/admin-nav';
import { AdminSyncErrorsService } from '../services/admin-sync-errors.service';
import {
  SyncError,
  SyncErrorFilters,
  SyncErrorSummary,
  SynchronizationOriginType
} from '../models/admin.models';

type SortDirection = 'asc' | 'desc';
type SortField = 'createdAt' | 'externalWorkName' | 'scanlator.name';

const SYNC_ORIGINS: readonly SynchronizationOriginType[] = [
  'MANGADEX',
  'LYCANTOONS',
  'MANGOTOONS',
  'MEDIOCRESCAN'
];

const PURGE_PRESETS: readonly number[] = [7, 30, 90];

@Component({
  selector: 'app-admin-sync-errors',
  imports: [NavbarComponent, AdminNavComponent, FormsModule, TranslatePipe, DatePipe],
  templateUrl: './sync-errors.html',
  styleUrl: './sync-errors.css'
})
export class AdminSyncErrorsComponent implements OnInit, OnDestroy {
  private readonly service = inject(AdminSyncErrorsService);
  private readonly i18n = inject(I18nService);
  private readonly destroy$ = new Subject<void>();
  private readonly filterChange$ = new Subject<void>();

  readonly origins = SYNC_ORIGINS;
  readonly purgePresets = PURGE_PRESETS;

  readonly summary = signal<SyncErrorSummary | null>(null);

  readonly loading = signal(false);
  readonly errors = signal<SyncError[]>([]);
  readonly page = signal(0);
  readonly pageSize = signal(10);
  readonly totalElements = signal(0);
  readonly totalPages = signal(0);

  readonly sortField = signal<SortField>('createdAt');
  readonly sortDirection = signal<SortDirection>('desc');

  readonly filterSync = signal<SynchronizationOriginType | ''>('');
  readonly filterName = signal('');
  readonly filterExternalId = signal('');
  readonly filterMessage = signal('');
  readonly filterFrom = signal('');
  readonly filterTo = signal('');
  readonly filterOrphans = signal(false);

  readonly selected = signal<ReadonlySet<string>>(new Set());
  readonly allSelectedOnPage = computed(() => {
    const list = this.errors();
    if (list.length === 0) return false;
    const sel = this.selected();
    return list.every(e => sel.has(e.id));
  });
  readonly selectedCount = computed(() => this.selected().size);

  readonly detail = signal<SyncError | null>(null);
  readonly detailLoading = signal(false);
  readonly confirmingDelete = signal<string | null>(null);

  readonly purgeMenuOpen = signal(false);
  readonly feedback = signal<{ kind: 'success' | 'error'; text: string } | null>(null);

  ngOnInit(): void {
    this.filterChange$.pipe(
      debounceTime(300),
      takeUntil(this.destroy$)
    ).subscribe(() => this.loadPage(0));

    this.loadSummary();
    this.loadPage(0);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private buildFilters(): SyncErrorFilters {
    const filters: SyncErrorFilters = {};
    const sync = this.filterSync();
    if (sync) filters.synchronization = sync;
    if (this.filterName().trim()) filters.externalWorkName = this.filterName().trim();
    if (this.filterExternalId().trim()) filters.externalWorkId = this.filterExternalId().trim();
    if (this.filterMessage().trim()) filters.errorMessage = this.filterMessage().trim();
    if (this.filterFrom()) filters.from = this.toIsoStart(this.filterFrom());
    if (this.filterTo()) filters.to = this.toIsoEnd(this.filterTo());
    if (this.filterOrphans()) filters.orphansOnly = true;
    return filters;
  }

  private toIsoStart(date: string): string {
    return new Date(`${date}T00:00:00`).toISOString();
  }

  private toIsoEnd(date: string): string {
    return new Date(`${date}T23:59:59.999`).toISOString();
  }

  loadSummary(): void {
    this.service.summary().subscribe({
      next: (summary) => this.summary.set(summary),
      error: () => this.summary.set(null)
    });
  }

  loadPage(page: number): void {
    this.loading.set(true);
    const sort = `${this.sortField()},${this.sortDirection()}`;
    this.service.list(this.buildFilters(), page, this.pageSize(), sort).subscribe({
      next: (response) => {
        this.errors.set(response.content);
        this.page.set(response.page.number);
        this.totalElements.set(response.page.totalElements);
        this.totalPages.set(response.page.totalPages);
        this.loading.set(false);
        this.clearSelection();
      },
      error: () => {
        this.loading.set(false);
        this.setFeedback('error', this.i18n.t('admin.syncErrors.feedback.loadError'));
      }
    });
  }

  onFilterChange(): void {
    this.filterChange$.next();
  }

  clearFilters(): void {
    this.filterSync.set('');
    this.filterName.set('');
    this.filterExternalId.set('');
    this.filterMessage.set('');
    this.filterFrom.set('');
    this.filterTo.set('');
    this.filterOrphans.set(false);
    this.loadPage(0);
  }

  toggleSort(field: SortField): void {
    if (this.sortField() === field) {
      this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortField.set(field);
      this.sortDirection.set('desc');
    }
    this.loadPage(0);
  }

  prevPage(): void {
    if (this.page() > 0) this.loadPage(this.page() - 1);
  }

  nextPage(): void {
    if (this.page() + 1 < this.totalPages()) this.loadPage(this.page() + 1);
  }

  toggleOne(id: string): void {
    const current = new Set(this.selected());
    if (current.has(id)) current.delete(id); else current.add(id);
    this.selected.set(current);
  }

  isSelected(id: string): boolean {
    return this.selected().has(id);
  }

  toggleAllOnPage(): void {
    const list = this.errors();
    const current = new Set(this.selected());
    if (this.allSelectedOnPage()) {
      list.forEach(e => current.delete(e.id));
    } else {
      list.forEach(e => current.add(e.id));
    }
    this.selected.set(current);
  }

  clearSelection(): void {
    if (this.selected().size > 0) this.selected.set(new Set());
  }

  deleteSelected(): void {
    const ids = Array.from(this.selected());
    if (ids.length === 0) return;
    this.service.deleteMany(ids).subscribe({
      next: () => {
        this.setFeedback('success', this.i18n.t('admin.syncErrors.feedback.bulkDeleted', { count: ids.length }));
        this.loadSummary();
        this.loadPage(this.page());
      },
      error: () => this.setFeedback('error', this.i18n.t('admin.syncErrors.feedback.deleteError'))
    });
  }

  requestDelete(id: string): void {
    this.confirmingDelete.set(id);
  }

  cancelDelete(): void {
    this.confirmingDelete.set(null);
  }

  confirmDelete(id: string): void {
    this.service.delete(id).subscribe({
      next: () => {
        this.confirmingDelete.set(null);
        this.setFeedback('success', this.i18n.t('admin.syncErrors.feedback.deleted'));
        this.detail.set(null);
        this.loadSummary();
        this.loadPage(this.page());
      },
      error: () => {
        this.confirmingDelete.set(null);
        this.setFeedback('error', this.i18n.t('admin.syncErrors.feedback.deleteError'));
      }
    });
  }

  openDetail(row: SyncError): void {
    this.detail.set(row);
    this.detailLoading.set(true);
    this.service.getById(row.id).subscribe({
      next: (full) => {
        this.detail.set(full);
        this.detailLoading.set(false);
      },
      error: () => this.detailLoading.set(false)
    });
  }

  closeDetail(): void {
    this.detail.set(null);
    this.confirmingDelete.set(null);
  }

  togglePurgeMenu(): void {
    this.purgeMenuOpen.update(v => !v);
  }

  purge(days: number): void {
    this.purgeMenuOpen.set(false);
    if (!confirm(this.i18n.t('admin.syncErrors.purge.confirm', { days }))) return;
    this.service.purgeOlderThan(days).subscribe({
      next: () => {
        this.setFeedback('success', this.i18n.t('admin.syncErrors.feedback.purged', { days }));
        this.loadSummary();
        this.loadPage(0);
      },
      error: () => this.setFeedback('error', this.i18n.t('admin.syncErrors.feedback.purgeError'))
    });
  }

  copyStackTrace(): void {
    const text = this.detail()?.stackTrace ?? '';
    if (!text) return;
    navigator.clipboard?.writeText(text).then(
      () => this.setFeedback('success', this.i18n.t('admin.syncErrors.feedback.copied')),
      () => this.setFeedback('error', this.i18n.t('admin.syncErrors.feedback.copyError'))
    );
  }

  @HostListener('window:keydown.escape')
  onEscape(): void {
    if (this.detail()) this.closeDetail();
    else if (this.purgeMenuOpen()) this.purgeMenuOpen.set(false);
  }

  private setFeedback(kind: 'success' | 'error', text: string): void {
    this.feedback.set({ kind, text });
    setTimeout(() => this.feedback.set(null), 4000);
  }

  originLabel(origin: SynchronizationOriginType | null | undefined): string {
    if (!origin) return '—';
    return origin.charAt(0) + origin.slice(1).toLowerCase();
  }
}
