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

  readonly selectedWork = signal<AdminWorkItem | null>(null);

  readonly mangaDexResults = signal<MangaDexSearchItem[]>([]);
  readonly mangaDexLoading = signal(false);
  mangaDexSearch = '';

  readonly linking = signal(false);
  readonly linkSuccess = signal<string | null>(null);
  readonly linkError = signal<string | null>(null);

  ngOnInit(): void {
    this.worksSearchSubject.pipe(
      debounceTime(300),
      takeUntil(this.destroy$)
    ).subscribe(() => this.loadWorks(0));

    this.loadWorks(0);
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
    this.service.listWorks(page, 20, this.worksSearch || undefined).subscribe({
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
    this.linkSuccess.set(null);
    this.linkError.set(null);
  }

  searchMangaDex(): void {
    if (!this.mangaDexSearch.trim()) return;
    this.mangaDexLoading.set(true);
    this.linkSuccess.set(null);
    this.linkError.set(null);
    this.service.searchMangaDex(this.mangaDexSearch.trim()).subscribe({
      next: (results) => {
        this.mangaDexResults.set(results);
        this.mangaDexLoading.set(false);
      },
      error: () => this.mangaDexLoading.set(false)
    });
  }

  linkToMangaDex(mangaDexItem: MangaDexSearchItem): void {
    const work = this.selectedWork();
    if (!work || this.linking()) return;

    this.linking.set(true);
    this.linkSuccess.set(null);
    this.linkError.set(null);

    this.service.linkWorkToMangaDex(work.id, mangaDexItem.id).subscribe({
      next: () => {
        this.linking.set(false);
        this.linkSuccess.set(`"${work.title}" vinculado com sucesso ao MangaDex!`);
        this.mangaDexResults.set([]);
        this.selectedWork.set(null);
        this.loadWorks(this.worksPage());
      },
      error: (err) => {
        this.linking.set(false);
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
