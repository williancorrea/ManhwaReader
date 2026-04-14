import { Component, EventEmitter, Input, OnDestroy, OnInit, Output, computed, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Manhwa } from '../manhwa-card/manhwa-card';

export interface DemographicOption {
  value: string;
  label: string;
}

export interface CarouselItem {
  manhwa: Manhwa;
  position: number;
}

@Component({
  selector: 'app-featured-carousel',
  imports: [RouterLink],
  templateUrl: './featured-carousel.html',
  styleUrl: './featured-carousel.css'
})
export class FeaturedCarouselComponent implements OnInit, OnDestroy {
  @Input({ required: true }) set items(v: Manhwa[]) {
    this._items.set(v ?? []);
    this.activeIndex.set(0);
  }
  @Input() demographics: DemographicOption[] = [];
  @Input() selectedDemographic: string = '';
  @Output() demographicChange = new EventEmitter<string>();

  readonly activeIndex = signal(0);
  private readonly _items = signal<Manhwa[]>([]);
  private intervalId: ReturnType<typeof setInterval> | null = null;

  readonly displayItems = computed<CarouselItem[]>(() => {
    const items = this._items();
    const len = items.length;
    if (len === 0) return [];
    return ([-2, -1, 0, 1, 2] as const).map(offset => {
      const idx = ((this.activeIndex() + offset) % len + len) % len;
      return { manhwa: items[idx], position: offset };
    });
  });

  readonly hasItems = computed(() => this._items().length > 0);

  ngOnInit(): void {
    this.startAutoplay();
  }

  ngOnDestroy(): void {
    this.stopAutoplay();
  }

  prev(): void {
    const len = this._items().length;
    const i = this.activeIndex();
    this.activeIndex.set(i === 0 ? len - 1 : i - 1);
    this.restartAutoplay();
  }

  next(): void {
    const len = this._items().length;
    const i = this.activeIndex();
    this.activeIndex.set(i === len - 1 ? 0 : i + 1);
    this.restartAutoplay();
  }

  moveToward(position: number): void {
    if (position > 0) {
      this.next();
    } else if (position < 0) {
      this.prev();
    }
  }

  selectDemographic(value: string): void {
    this.demographicChange.emit(value);
  }

  positionClass(position: number): string {
    if (position === 0) return 'center';
    if (Math.abs(position) === 1) return 'adjacent';
    return 'far';
  }

  onMouseEnter(): void {
    this.stopAutoplay();
  }

  onMouseLeave(): void {
    this.startAutoplay();
  }

  private startAutoplay(): void {
    this.intervalId = setInterval(() => {
      const items = this._items();
      if (items.length === 0) return;
      const i = this.activeIndex();
      this.activeIndex.set(i === items.length - 1 ? 0 : i + 1);
    }, 5000);
  }

  private stopAutoplay(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
      this.intervalId = null;
    }
  }

  private restartAutoplay(): void {
    this.stopAutoplay();
    this.startAutoplay();
  }
}
