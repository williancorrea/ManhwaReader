import { AfterViewInit, Component, ElementRef, HostListener, inject, OnDestroy, OnInit, signal, ViewChild } from '@angular/core';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ManhwaCardComponent, Manhwa } from '../../../shared/components/manhwa-card/manhwa-card';
import { NavbarComponent } from '../../../shared/components/navbar/navbar';
import { LibraryService } from '../services/library.service';
import { LibraryItem, LIBRARY_STATUSES } from '../models/library.models';
import { TranslatePipe } from '../../../core/i18n/translate.pipe';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-library-list',
  imports: [NavbarComponent, ManhwaCardComponent, TranslatePipe, RouterLink, FormsModule],
  templateUrl: './library-list.html',
  styleUrl: './library-list.css'
})
export class LibraryListComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('sentinel') private sentinelRef!: ElementRef<HTMLElement>;

  private readonly libraryService = inject(LibraryService);
  private readonly destroy$ = new Subject<void>();
  private readonly titleSearch$ = new Subject<string>();
  private intersectionObserver?: IntersectionObserver;

  readonly items = signal<Manhwa[]>([]);
  readonly currentPage = signal(0);
  readonly hasNextPage = signal(false);
  readonly isLoading = signal(false);
  readonly showScrollTop = signal(false);

  filterStatus = '';
  filterTitle = '';
  readonly statusTabs = LIBRARY_STATUSES;

  @HostListener('window:scroll')
  onScroll(): void {
    this.showScrollTop.set(window.scrollY > 300);
  }

  ngOnInit(): void {
    this.titleSearch$.pipe(
      debounceTime(350),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(() => this.carregarPagina(0));
    this.carregarPagina(0);
  }

  ngAfterViewInit(): void {
    this.intersectionObserver = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && !this.isLoading() && this.hasNextPage()) {
          this.carregarPagina(this.currentPage() + 1);
        }
      },
      { rootMargin: '300px' }
    );
    this.intersectionObserver.observe(this.sentinelRef.nativeElement);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.intersectionObserver?.disconnect();
  }

  onStatusChange(status: string): void {
    this.filterStatus = status;
    this.carregarPagina(0);
  }

  onTitleChange(value: string): void {
    this.filterTitle = value;
    this.titleSearch$.next(value);
  }

  scrollToTop(): void {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  private recheckSentinel(): void {
    if (!this.intersectionObserver || !this.sentinelRef) return;
    setTimeout(() => {
      this.intersectionObserver!.unobserve(this.sentinelRef.nativeElement);
      this.intersectionObserver!.observe(this.sentinelRef.nativeElement);
    });
  }

  private carregarPagina(page: number): void {
    this.isLoading.set(true);
    const status = this.filterStatus || undefined;
    const title = this.filterTitle.trim() || undefined;
    this.libraryService.list(page, 20, status, title).subscribe({
      next: (response) => {
        const offset = page * response.page.size;
        const newItems = response.content.map((item, i) => toManhwa(item, offset + i));
        if (page === 0) {
          this.items.set(newItems);
        } else {
          this.items.update(current => [...current, ...newItems]);
        }
        this.currentPage.set(response.page.number);
        this.hasNextPage.set(response.page.number < response.page.totalPages - 1);
        this.isLoading.set(false);
        this.recheckSentinel();
      },
      error: () => {
        this.isLoading.set(false);
      }
    });
  }
}

function toManhwa(item: LibraryItem, id: number): Manhwa {
  return {
    id,
    slug: item.slug,
    title: item.title ?? '',
    coverUrl: item.coverUrl ?? '',
    latestChapter: item.chapterCount,
    genres: [item.publicationDemographic, item.workStatus].filter(Boolean) as string[],
    unreadCount: item.unreadCount,
    originalLanguageFlag: item.originalLanguageFlag,
    originalLanguageName: item.originalLanguageName,
    demographic: item.publicationDemographic,
  };
}
