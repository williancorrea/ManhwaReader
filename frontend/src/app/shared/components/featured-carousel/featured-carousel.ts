import { Component, Input, OnDestroy, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Manhwa } from '../manhwa-card/manhwa-card';

@Component({
  selector: 'app-featured-carousel',
  imports: [RouterLink],
  templateUrl: './featured-carousel.html',
  styleUrl: './featured-carousel.css'
})
export class FeaturedCarouselComponent implements OnInit, OnDestroy {
  @Input({ required: true }) items!: Manhwa[];

  readonly activeIndex = signal(0);
  private intervalId: ReturnType<typeof setInterval> | null = null;

  ngOnInit(): void {
    this.startAutoplay();
  }

  ngOnDestroy(): void {
    this.stopAutoplay();
  }

  goTo(index: number): void {
    this.activeIndex.set(index);
    this.restartAutoplay();
  }

  prev(): void {
    const i = this.activeIndex();
    this.activeIndex.set(i === 0 ? this.items.length - 1 : i - 1);
    this.restartAutoplay();
  }

  next(): void {
    const i = this.activeIndex();
    this.activeIndex.set(i === this.items.length - 1 ? 0 : i + 1);
    this.restartAutoplay();
  }

  onMouseEnter(): void {
    this.stopAutoplay();
  }

  onMouseLeave(): void {
    this.startAutoplay();
  }

  private startAutoplay(): void {
    this.intervalId = setInterval(() => {
      const i = this.activeIndex();
      this.activeIndex.set(i === this.items.length - 1 ? 0 : i + 1);
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
