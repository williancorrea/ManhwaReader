import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { NavbarComponent } from '../../shared/components/navbar/navbar';
import { ManhwaCardComponent, Manhwa } from '../../shared/components/manhwa-card/manhwa-card';
import { FeaturedCarouselComponent } from '../../shared/components/featured-carousel/featured-carousel';
import { CatalogService } from '../catalog/services/catalog.service';
import { WorkCatalogItem } from '../catalog/models/catalog.models';

@Component({
  selector: 'app-home',
  imports: [NavbarComponent, ManhwaCardComponent, FeaturedCarouselComponent, RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class HomeComponent implements OnInit {
  private readonly catalogService = inject(CatalogService);

  readonly featuredWorks = signal<Manhwa[]>([]);

  readonly continueReading: Manhwa[] = [
    { id: 1,  title: 'Solo Leveling',             coverUrl: 'https://picsum.photos/seed/manhwa1/200/300',  latestChapter: 179, genres: ['Action', 'Fantasy'],            progress: 65 },
    { id: 2,  title: 'Tower of God',               coverUrl: 'https://picsum.photos/seed/manhwa2/200/300',  latestChapter: 598, genres: ['Fantasy', 'Mystery'],           progress: 42 },
    { id: 3,  title: 'The Beginning After End',    coverUrl: 'https://picsum.photos/seed/manhwa3/200/300',  latestChapter: 190, genres: ['Fantasy', 'Isekai', 'Action'],   progress: 78 },
    { id: 4,  title: 'Omniscient Reader',          coverUrl: 'https://picsum.photos/seed/manhwa4/200/300',  latestChapter: 147, genres: ['Action', 'Drama'],              progress: 30 },
    { id: 5,  title: 'Windbreaker',                coverUrl: 'https://picsum.photos/seed/manhwa5/200/300',  latestChapter: 464, genres: ['Sports', 'Drama'],              progress: 55 },
    { id: 6,  title: 'Nano Machine',               coverUrl: 'https://picsum.photos/seed/manhwa6/200/300',  latestChapter: 183, genres: ['Martial Arts', 'Sci-Fi'],        progress: 20 },
  ];

  readonly latestUpdates = signal<Manhwa[]>([]);

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
