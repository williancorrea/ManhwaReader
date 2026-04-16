import { Component, EventEmitter, Input, OnDestroy, OnInit, Output, computed, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Manhwa } from '../manhwa-card/manhwa-card';
import { TranslatePipe } from '../../../core/i18n/translate.pipe';

export interface DemographicOption {
  value: string;
  labelKey: string;
}

export interface CarouselItem {
  manhwa: Manhwa;
  position: number;
  sourceIndex: number;
  trackKey: string;
}

@Component({
  selector: 'app-featured-carousel',
  imports: [RouterLink, TranslatePipe],
  templateUrl: './featured-carousel.html',
  styleUrl: './featured-carousel.css'
})
export class FeaturedCarouselComponent implements OnInit, OnDestroy {
  @Input({ required: true }) set items(v: Manhwa[]) {
    this._items.set(v ?? []);
    this.activeIndex.set(0);
    this.syncAutoplay();
  }
  @Input() demographics: DemographicOption[] = [];
  @Input() selectedDemographic: string = '';
  @Output() demographicChange = new EventEmitter<string>();

  readonly activeIndex = signal(0);
  private readonly _items = signal<Manhwa[]>([]);
  private intervalId: ReturnType<typeof setInterval> | null = null;
  private readonly autoplayDelayMs = 4000;

  readonly displayItems = computed<CarouselItem[]>(() => {
    const items = this._items();
    const len = items.length;
    if (len === 0) return [];
    const trackByItem = len >= 5;

    return ([-2, -1, 0, 1, 2] as const).map(offset => {
      const idx = ((this.activeIndex() + offset) % len + len) % len;
      return {
        manhwa: items[idx],
        position: offset,
        sourceIndex: idx,
        trackKey: trackByItem ? `item-${idx}` : `slot-${offset}`
      };
    });
  });

  readonly hasItems = computed(() => this._items().length > 0);

  ngOnInit(): void {
    this.syncAutoplay();
  }

  ngOnDestroy(): void {
    this.stopAutoplay();
  }

  prev(): void {
    const len = this._items().length;
    if (len <= 1) return;
    const i = this.activeIndex();
    this.activeIndex.set(i === 0 ? len - 1 : i - 1);
    this.restartAutoplay();
  }

  next(): void {
    const len = this._items().length;
    if (len <= 1) return;
    const i = this.activeIndex();
    this.activeIndex.set(i === len - 1 ? 0 : i + 1);
    this.restartAutoplay();
  }

  focusItem(sourceIndex: number): void {
    const len = this._items().length;
    if (len <= 1) return;
    if (sourceIndex < 0 || sourceIndex >= len) return;
    if (sourceIndex === this.activeIndex()) return;

    this.activeIndex.set(sourceIndex);
    this.restartAutoplay();
  }

  selectDemographic(value: string): void {
    this.demographicChange.emit(value);
  }

  positionClass(position: number): string {
    if (position === 0) return 'center';
    if (Math.abs(position) === 1) return 'adjacent';
    return 'far';
  }

  private startAutoplay(): void {
    if (this.intervalId || !this.shouldAutoplay()) return;

    this.intervalId = setInterval(() => {
      const items = this._items();
      if (items.length <= 1) return;
      const i = this.activeIndex();
      this.activeIndex.set(i === items.length - 1 ? 0 : i + 1);
    }, this.autoplayDelayMs);
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

  private syncAutoplay(): void {
    if (this.shouldAutoplay()) {
      this.startAutoplay();
    } else {
      this.stopAutoplay();
    }
  }

  private shouldAutoplay(): boolean {
    return this._items().length > 1;
  }
}
