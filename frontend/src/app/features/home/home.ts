import { Component, computed, HostListener, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { NavbarComponent } from '../../shared/components/navbar/navbar';
import { ManhwaCardComponent, Manhwa } from '../../shared/components/manhwa-card/manhwa-card';
import { FeaturedCarouselComponent, DemographicOption } from '../../shared/components/featured-carousel/featured-carousel';
import { CatalogService } from '../catalog/services/catalog.service';
import { WorkCatalogItem } from '../catalog/models/catalog.models';
import { LibraryService } from '../library/services/library.service';
import { LibraryItem } from '../library/models/library.models';
import { TranslatePipe } from '../../core/i18n/translate.pipe';

@Component({
  selector: 'app-home',
  imports: [NavbarComponent, ManhwaCardComponent, FeaturedCarouselComponent, RouterLink, TranslatePipe],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class HomeComponent implements OnInit {
  private readonly catalogService = inject(CatalogService);
  private readonly libraryService = inject(LibraryService);

  readonly featuredWorks = signal<Manhwa[]>([]);
  readonly showScrollTop = signal(false);
  readonly latestUpdates = signal<Manhwa[]>([]);
  readonly selectedDemographic = signal('COMIC');

  readonly demographics: DemographicOption[] = [
    { value: 'COMIC', labelKey: 'enum.demographic.COMIC' },
    { value: 'SHOUNEN', labelKey: 'enum.demographic.SHOUNEN' },
    { value: 'SEINEN', labelKey: 'enum.demographic.SEINEN' },
    { value: 'JOSEI', labelKey: 'enum.demographic.JOSEI' },
    { value: 'SHOUJO', labelKey: 'enum.demographic.SHOUJO' },
    { value: 'YAOI', labelKey: 'enum.demographic.YAOI' },
    { value: 'YURI', labelKey: 'enum.demographic.YURI' },
    { value: 'HENTAI', labelKey: 'enum.demographic.HENTAI' },
  ];

  private readonly allRecentlyRead = signal<Manhwa[]>([]);
  readonly continueReadingLoaded = signal(false);
  readonly pendingReads = computed(() => this.allRecentlyRead().filter(m => (m.unreadCount ?? 0) > 0));
  readonly allUpToDate = computed(() => this.continueReadingLoaded() && this.allRecentlyRead().length > 0 && this.pendingReads().length === 0);

  @HostListener('window:scroll')
  onScroll(): void {
    this.showScrollTop.set(window.scrollY > 300);
  }

  scrollToTop(): void {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  ngOnInit(): void {
    this.loadFeaturedWorks('COMIC');

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

  onDemographicChange(demographic: string): void {
    this.selectedDemographic.set(demographic);
    this.loadFeaturedWorks(demographic);
  }

  private loadFeaturedWorks(demographic: string): void {
    this.catalogService.listar(0, 8, { publicationDemographic: demographic }).subscribe({
      next: (response) => {
        this.featuredWorks.set(response.content.map(toManhwa));
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
    originalLanguageFlag: item.originalLanguageFlag,
    originalLanguageName: item.originalLanguageName,
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
    originalLanguageFlag: item.originalLanguageFlag,
    originalLanguageName: item.originalLanguageName,
  };
}
