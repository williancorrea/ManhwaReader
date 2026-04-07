import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
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
export class CatalogListComponent implements OnInit, OnDestroy {
  private readonly catalogService = inject(CatalogService);
  private readonly destroy$ = new Subject<void>();
  private readonly titleSubject = new Subject<string>();

  readonly items = signal<Manhwa[]>([]);
  readonly currentPage = signal(0);
  readonly totalPages = signal(0);
  readonly isLoading = signal(false);

  showFilters = false;
  filterTitle = '';
  filterType = '';
  filterDemographic = '';
  filterStatus = '';
  filterSort = '';

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

  readonly workTypes = WORK_TYPES;
  readonly workStatuses = WORK_STATUSES;
  readonly workDemographics = WORK_DEMOGRAPHICS;
  readonly sortOptions = SORT_OPTIONS;

  ngOnInit(): void {
    this.titleSubject.pipe(debounceTime(300), takeUntil(this.destroy$)).subscribe(() => {
      this.aplicarFiltros();
    });
    this.carregarPagina(0);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onTitleChange(value: string): void {
    this.filterTitle = value;
    this.titleSubject.next(value);
  }

  aplicarFiltros(): void {
    this.carregarPagina(0);
  }

  nextPage(): void {
    if (this.currentPage() < this.totalPages() - 1) {
      this.carregarPagina(this.currentPage() + 1);
    }
  }

  previousPage(): void {
    if (this.currentPage() > 0) {
      this.carregarPagina(this.currentPage() - 1);
    }
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
        this.items.set(response.content.map(toManhwa));
        this.currentPage.set(response.number);
        this.totalPages.set(response.totalPages);
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
      }
    });
  }
}

function toManhwa(item: WorkCatalogItem, index: number): Manhwa {
  return {
    id: index,
    title: item.title ?? '',
    coverUrl: item.coverUrl ?? '',
    latestChapter: item.chapterCount,
    genres: [item.publicationDemographic, item.status].filter(Boolean) as string[],
  };
}
