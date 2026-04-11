import { AfterViewInit, Component, ElementRef, HostListener, inject, OnDestroy, OnInit, PLATFORM_ID, signal, ViewChild } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ManhwaCardComponent, Manhwa } from '../../../shared/components/manhwa-card/manhwa-card';
import { NavbarComponent } from '../../../shared/components/navbar/navbar';
import { LibraryService } from '../services/library.service';
import { LibraryItem, LIBRARY_STATUSES } from '../models/library.models';

@Component({
  selector: 'app-library-list',
  imports: [NavbarComponent, ManhwaCardComponent],
  templateUrl: './library-list.html',
  styleUrl: './library-list.css'
})
export class LibraryListComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('sentinel') private sentinelRef!: ElementRef<HTMLElement>;

  private readonly libraryService = inject(LibraryService);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly destroy$ = new Subject<void>();
  private intersectionObserver?: IntersectionObserver;

  readonly items = signal<Manhwa[]>([]);
  readonly currentPage = signal(0);
  readonly hasNextPage = signal(false);
  readonly isLoading = signal(false);
  readonly showScrollTop = signal(false);

  filterStatus = '';
  readonly statusTabs = LIBRARY_STATUSES;

  @HostListener('window:scroll')
  onScroll(): void {
    if (!isPlatformBrowser(this.platformId)) return;
    this.showScrollTop.set(window.scrollY > 300);
  }

  ngOnInit(): void {
    this.carregarPagina(0);
  }

  ngAfterViewInit(): void {
    if (!isPlatformBrowser(this.platformId)) return;
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

  scrollToTop(): void {
    if (!isPlatformBrowser(this.platformId)) return;
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
    this.libraryService.list(page, 20, status).subscribe({
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
  };
}
