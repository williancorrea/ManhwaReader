import { Component, inject, OnInit, signal } from '@angular/core';
import { ManhwaCardComponent, Manhwa } from '../../../shared/components/manhwa-card/manhwa-card';
import { NavbarComponent } from '../../../shared/components/navbar/navbar';
import { CatalogService } from '../services/catalog.service';
import { WorkCatalogItem } from '../models/catalog.models';

@Component({
  selector: 'app-catalog-list',
  imports: [NavbarComponent, ManhwaCardComponent],
  templateUrl: './catalog-list.html',
  styleUrl: './catalog-list.css'
})
export class CatalogListComponent implements OnInit {
  private readonly catalogService = inject(CatalogService);

  readonly items = signal<Manhwa[]>([]);
  readonly currentPage = signal(0);
  readonly totalPages = signal(0);
  readonly isLoading = signal(false);

  ngOnInit(): void {
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
    this.catalogService.listar(page).subscribe({
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
