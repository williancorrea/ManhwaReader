import { Component, computed, HostListener, inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { RouterLink } from '@angular/router';
import { NavbarComponent } from '../../shared/components/navbar/navbar';
import { ManhwaCardComponent, Manhwa } from '../../shared/components/manhwa-card/manhwa-card';
import { FeaturedCarouselComponent } from '../../shared/components/featured-carousel/featured-carousel';
import { CatalogService } from '../catalog/services/catalog.service';
import { WorkCatalogItem } from '../catalog/models/catalog.models';
import { LibraryService } from '../library/services/library.service';
import { LibraryItem } from '../library/models/library.models';

@Component({
  selector: 'app-home',
  imports: [NavbarComponent, ManhwaCardComponent, FeaturedCarouselComponent, RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class HomeComponent implements OnInit {
  private readonly catalogService = inject(CatalogService);
  private readonly libraryService = inject(LibraryService);
  private readonly platformId = inject(PLATFORM_ID);

  readonly featuredWorks = signal<Manhwa[]>([]);
  readonly showScrollTop = signal(false);
  readonly latestUpdates = signal<Manhwa[]>([]);

  private readonly allRecentlyRead = signal<Manhwa[]>([]);
  readonly continueReadingLoaded = signal(false);
  readonly pendingReads = computed(() => this.allRecentlyRead().filter(m => (m.unreadCount ?? 0) > 0));
  readonly allUpToDate = computed(() => this.continueReadingLoaded() && this.allRecentlyRead().length > 0 && this.pendingReads().length === 0);

  @HostListener('window:scroll')
  onScroll(): void {
    if (!isPlatformBrowser(this.platformId)) return;
    this.showScrollTop.set(window.scrollY > 300);
  }

  scrollToTop(): void {
    if (!isPlatformBrowser(this.platformId)) return;
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  ngOnInit(): void {
    this.catalogService.listar(0, 5).subscribe({
      next: (response) => {
        this.featuredWorks.set(response.content.map(toManhwa));
      }
    });

    this.catalogService.listar(0).subscribe({
      next: (response) => {
        this.latestUpdates.set(response.content.map(toManhwa));
      }
    });

    this.libraryService.continueReading(12).subscribe({
      next: (items) => {
        this.allRecentlyRead.set(items.map(toLibraryManhwa));
        this.continueReadingLoaded.set(true);
      }
    });
  }
}

function toManhwa(item: WorkCatalogItem, index: number): Manhwa {
  return {
    id: index,
    slug: item.slug,
    title: item.title ?? '',
    coverUrl: item.coverUrl ?? '',
    latestChapter: item.chapterCount,
    genres: [item.publicationDemographic, item.status].filter(Boolean) as string[],
    inLibrary: item.userLibraryStatus != null,
  };
}

function toLibraryManhwa(item: LibraryItem, index: number): Manhwa {
  return {
    id: index,
    slug: item.slug,
    title: item.title ?? '',
    coverUrl: item.coverUrl ?? '',
    latestChapter: item.chapterCount,
    genres: [],
    unreadCount: item.unreadCount || undefined,
  };
}
