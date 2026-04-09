import { AfterViewInit, Component, ElementRef, HostListener, inject, OnDestroy, OnInit, PLATFORM_ID, signal, ViewChild } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { debounceTime, takeUntil } from 'rxjs/operators';
import { ManhwaCardComponent, Manhwa } from '../../../shared/components/manhwa-card/manhwa-card';
import { NavbarComponent } from '../../../shared/components/navbar/navbar';
import { CatalogService } from '../services/catalog.service';
import { CatalogFilter, WorkCatalogItem, WORK_TYPES, WORK_STATUSES, WORK_DEMOGRAPHICS, SORT_OPTIONS } from '../models/catalog.models';

@Component({
  selector: 'app-catalog-list',
  imports: [NavbarComponent, ManhwaCardComponent, FormsModule],
  templateUrl: './catalog-list.html',
  styleUrl: './catalog-list.css'
})
export class CatalogListComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('sentinel') private sentinelRef!: ElementRef<HTMLElement>;
  @ViewChild('searchInput') private searchInputRef!: ElementRef<HTMLInputElement>;

  private readonly catalogService = inject(CatalogService);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly destroy$ = new Subject<void>();
  private readonly titleSubject = new Subject<string>();
  private intersectionObserver?: IntersectionObserver;

  readonly items = signal<Manhwa[]>([]);
  readonly currentPage = signal(0);
  readonly hasNextPage = signal(false);
  readonly isLoading = signal(false);
  readonly showScrollTop = signal(false);

  showFilters = false;
  filterTitle = '';
  filterType = '';
  filterDemographic = '';
  filterStatus = '';
  filterSort = '';

  readonly workTypes = WORK_TYPES;
  readonly workStatuses = WORK_STATUSES;
  readonly workDemographics = WORK_DEMOGRAPHICS;
  readonly sortOptions = SORT_OPTIONS;

  get hasActiveFilters(): boolean {
    return !!(this.filterType || this.filterDemographic || this.filterStatus);
  }

  private filtersWereOpen = false;

  @HostListener('window:scroll')
  onScroll(): void {
    if (!isPlatformBrowser(this.platformId)) return;
    const scrollY = window.scrollY;
    this.showScrollTop.set(scrollY > 300);
    if (scrollY > 50 && this.showFilters) {
      this.filtersWereOpen = true;
      this.showFilters = false;
    } else if (scrollY <= 50 && this.filtersWereOpen) {
      this.filtersWereOpen = false;
      this.showFilters = true;
    }
  }

  ngOnInit(): void {
    this.titleSubject.pipe(debounceTime(300), takeUntil(this.destroy$)).subscribe(() => {
      this.aplicarFiltros();
    });
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
    this.searchInputRef?.nativeElement.focus();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.intersectionObserver?.disconnect();
  }

  onTitleChange(value: string): void {
    this.filterTitle = value;
    this.titleSubject.next(value);
  }

  aplicarFiltros(): void {
    this.carregarPagina(0);
  }

  toggleFilters(): void {
    if (this.showFilters) {
      this.showFilters = false;
      this.filterType = '';
      this.filterDemographic = '';
      this.filterStatus = '';
      this.carregarPagina(0);
    } else {
      this.showFilters = true;
    }
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
    const filter: CatalogFilter = {
      title: this.filterTitle || undefined,
      type: this.filterType || undefined,
      publicationDemographic: this.filterDemographic || undefined,
      status: this.filterStatus || undefined,
      sort: this.filterSort || undefined,
    };
    this.catalogService.listar(page, 20, filter).subscribe({
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

function toManhwa(item: WorkCatalogItem, id: number): Manhwa {
  return {
    id,
    slug: item.slug,
    title: item.title ?? '',
    coverUrl: item.coverUrl ?? '',
    latestChapter: item.chapterCount,
    genres: [item.publicationDemographic, item.status].filter(Boolean) as string[],
    inLibrary: item.userLibraryStatus != null,
  };
}
