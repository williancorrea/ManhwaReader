import { Component, inject, OnDestroy, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { debounceTime, takeUntil } from 'rxjs/operators';
import { NavbarComponent } from '../../../shared/components/navbar/navbar';
import { AdminSynchronizationService } from '../services/admin-synchronization.service';
import { AdminWorkItem, MangaDexSearchItem } from '../models/admin.models';

@Component({
  selector: 'app-admin-synchronization',
  imports: [NavbarComponent, FormsModule],
  templateUrl: './synchronization.html',
  styleUrl: './synchronization.css'
})
export class AdminSynchronizationComponent implements OnInit, OnDestroy {
  private readonly service = inject(AdminSynchronizationService);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly destroy$ = new Subject<void>();
  private readonly worksSearchSubject = new Subject<string>();

  readonly works = signal<AdminWorkItem[]>([]);
  readonly worksLoading = signal(false);
  readonly worksPage = signal(0);
  readonly worksHasNext = signal(false);
  readonly worksTotalElements = signal(0);
  worksSearch = '';

  readonly linkedWorks = signal<AdminWorkItem[]>([]);
  readonly linkedWorksLoading = signal(false);
  readonly linkedWorksPage = signal(0);
  readonly linkedWorksHasNext = signal(false);
  readonly linkedWorksTotalElements = signal(0);
  linkedWorksSearch = '';
  private readonly linkedWorksSearchSubject = new Subject<string>();

  readonly selectedWork = signal<AdminWorkItem | null>(null);

  readonly mangaDexResults = signal<MangaDexSearchItem[]>([]);
  readonly mangaDexLoading = signal(false);
  readonly mangaDexSearched = signal(false);
  mangaDexSearch = '';

  readonly linking = signal<string | null>(null);
  readonly syncing = signal<string | null>(null);
  readonly linkSuccess = signal<string | null>(null);
  readonly linkError = signal<string | null>(null);
  readonly expandedCover = signal<string | null>(null);

  ngOnInit(): void {
    this.worksSearchSubject.pipe(
      debounceTime(300),
      takeUntil(this.destroy$)
    ).subscribe(() => this.loadWorks(0));

    this.linkedWorksSearchSubject.pipe(
      debounceTime(300),
      takeUntil(this.destroy$)
    ).subscribe(() => this.loadLinkedWorks(0));

    this.loadWorks(0);
    this.loadLinkedWorks(0);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onWorksSearchChange(value: string): void {
    this.worksSearch = value;
    this.worksSearchSubject.next(value);
  }

  loadWorks(page: number): void {
    this.worksLoading.set(true);
    this.service.listWorks(page, 20, this.worksSearch || undefined, false).subscribe({
      next: (response) => {
        this.works.set(response.content);
        this.worksPage.set(response.page.number);
        this.worksHasNext.set(response.page.number < response.page.totalPages - 1);
        this.worksTotalElements.set(response.page.totalElements);
        this.worksLoading.set(false);
      },
      error: () => this.worksLoading.set(false)
    });
  }

  onLinkedWorksSearchChange(value: string): void {
    this.linkedWorksSearch = value;
    this.linkedWorksSearchSubject.next(value);
  }

  loadLinkedWorks(page: number): void {
    this.linkedWorksLoading.set(true);
    this.service.listWorks(page, 20, this.linkedWorksSearch || undefined, true).subscribe({
      next: (response) => {
        this.linkedWorks.set(response.content);
        this.linkedWorksPage.set(response.page.number);
        this.linkedWorksHasNext.set(response.page.number < response.page.totalPages - 1);
        this.linkedWorksTotalElements.set(response.page.totalElements);
        this.linkedWorksLoading.set(false);
      },
      error: () => this.linkedWorksLoading.set(false)
    });
  }

  prevLinkedWorksPage(): void {
    if (this.linkedWorksPage() > 0) {
      this.loadLinkedWorks(this.linkedWorksPage() - 1);
    }
  }

  nextLinkedWorksPage(): void {
    if (this.linkedWorksHasNext()) {
      this.loadLinkedWorks(this.linkedWorksPage() + 1);
    }
  }

  triggerManualSync(work: AdminWorkItem): void {
    if (this.syncing()) return;
    this.syncing.set(work.id);
    this.linkSuccess.set(null);
    this.linkError.set(null);

    this.service.syncWorkWithMangaDex(work.id).subscribe({
      next: () => {
        this.syncing.set(null);
        this.linkSuccess.set(`Sincronização manual de "${work.title}" iniciada com sucesso!`);
      },
      error: () => {
        this.syncing.set(null);
        this.linkError.set('Erro ao iniciar sincronização. Tente novamente.');
      }
    });
  }

  prevWorksPage(): void {
    if (this.worksPage() > 0) {
      this.loadWorks(this.worksPage() - 1);
    }
  }

  nextWorksPage(): void {
    if (this.worksHasNext()) {
      this.loadWorks(this.worksPage() + 1);
    }
  }

  selectWork(work: AdminWorkItem): void {
    this.selectedWork.set(work);
    this.mangaDexSearch = work.title || '';
    this.mangaDexResults.set([]);
    this.mangaDexSearched.set(false);
    this.linkSuccess.set(null);
    this.linkError.set(null);
  }

  openCover(url: string): void {
    this.expandedCover.set(url);
  }

  closeCover(): void {
    this.expandedCover.set(null);
  }

  clearMangaDexSearch(): void {
    this.mangaDexSearch = '';
    this.mangaDexResults.set([]);
    this.mangaDexSearched.set(false);
  }

  searchMangaDex(): void {
    if (!this.mangaDexSearch.trim()) return;
    this.mangaDexLoading.set(true);
    this.mangaDexSearched.set(false);
    this.linkSuccess.set(null);
    this.linkError.set(null);
    this.service.searchMangaDex(this.mangaDexSearch.trim()).subscribe({
      next: (results) => {
        this.mangaDexResults.set(results);
        this.mangaDexSearched.set(true);
        this.mangaDexLoading.set(false);
      },
      error: () => {
        this.mangaDexSearched.set(true);
        this.mangaDexLoading.set(false);
      }
    });
  }

  linkToMangaDex(mangaDexItem: MangaDexSearchItem): void {
    const work = this.selectedWork();
    if (!work || this.linking()) return;

    this.linking.set(mangaDexItem.id);
    this.linkSuccess.set(null);
    this.linkError.set(null);

    this.service.linkWorkToMangaDex(work.id, mangaDexItem.id).subscribe({
      next: () => {
        this.linking.set(null);
        this.linkSuccess.set(`"${work.title}" vinculado com sucesso ao MangaDex!`);
        this.mangaDexResults.set([]);
        this.mangaDexSearch = '';
        this.mangaDexSearched.set(false);
        this.selectedWork.set(null);
        this.loadWorks(this.worksPage());
        this.loadLinkedWorks(this.linkedWorksPage());
      },
      error: (err) => {
        this.linking.set(null);
        if (err.status === 409) {
          this.linkError.set('Esta obra já está vinculada ao MangaDex.');
        } else {
          this.linkError.set('Erro ao vincular obra. Tente novamente.');
        }
      }
    });
  }

  hasMangaDexSync(work: AdminWorkItem): boolean {
    return work.synchronizationOrigins?.includes('MANGADEX') ?? false;
  }
}
