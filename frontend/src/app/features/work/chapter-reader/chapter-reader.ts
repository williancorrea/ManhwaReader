import {
  AfterViewInit,
  Component,
  ElementRef,
  HostListener,
  inject,
  OnDestroy,
  OnInit,
  signal,
  ViewChild
} from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { marked } from 'marked';
import { WorkService } from '../services/work.service';
import { ChapterReaderData } from '../models/work.models';
import { TranslatePipe } from '../../../core/i18n/translate.pipe';

@Component({
  selector: 'app-chapter-reader',
  imports: [TranslatePipe],
  templateUrl: './chapter-reader.html',
  styleUrl: './chapter-reader.css'
})
export class ChapterReaderComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('lastPageSentinel') private lastPageSentinel!: ElementRef<HTMLElement>;

  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly workService = inject(WorkService);
  private readonly sanitizer = inject(DomSanitizer);
  private readonly destroy$ = new Subject<void>();
  private intersectionObserver?: IntersectionObserver;
  private markedAsRead = false;
  private autoScrollRaf?: number;
  private hintTimeout?: ReturnType<typeof setTimeout>;

  readonly chapterData = signal<ChapterReaderData | null>(null);
  readonly isLoading = signal(true);
  readonly isHeaderHidden = signal(false);
  readonly scrollProgress = signal(0);
  readonly isFloatingMenuHidden = signal(false);
  readonly isAutoScrolling = signal(false);
  readonly autoScrollSpeed = signal(Number(localStorage.getItem('reader.autoScrollSpeed')) || 1);
  readonly showAutoScrollHint = signal(false);

  readonly speedOptions = [1, 2, 3, 4] as const;

  slug = '';
  chapterId = '';
  private lastScrollY = 0;

  @HostListener('window:scroll')
  onScroll(): void {
    const scrollY = window.scrollY;
    const max = Math.max(1, document.documentElement.scrollHeight - window.innerHeight);
    const scrollingDown = scrollY > this.lastScrollY && scrollY > 140;
    this.isHeaderHidden.set(scrollingDown);
    if (!this.isAutoScrolling()) {
      this.isFloatingMenuHidden.set(scrollingDown);
    }
    this.scrollProgress.set(Math.min(100, Math.max(0, Math.round((scrollY / max) * 100))));
    this.lastScrollY = scrollY;
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (!this.isAutoScrolling()) return;
    const target = event.target as HTMLElement;
    if (target.closest('.speed-selector') || target.closest('.floating-menu')) return;
    this.isFloatingMenuHidden.set(!this.isFloatingMenuHidden());
  }

  ngOnInit(): void {
    this.route.paramMap
      .pipe(takeUntil(this.destroy$))
      .subscribe(params => {
        this.slug = params.get('slug') ?? '';
        this.chapterId = params.get('chapterId') ?? '';
        this.markedAsRead = false;
        this.loadChapter();
      });
  }

  ngAfterViewInit(): void {
    this.setupIntersectionObserver();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.intersectionObserver?.disconnect();
    this.stopAutoScroll();
    clearTimeout(this.hintTimeout);
  }

  private loadChapter(): void {
    this.isLoading.set(true);
    this.workService.getChapterReader(this.slug, this.chapterId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.chapterData.set(data);
          this.isLoading.set(false);
          this.scrollToTop();
          this.preloadUpcoming(data);
          setTimeout(() => this.setupIntersectionObserver(), 100);
        },
        error: () => {
          this.chapterData.set(null);
          this.isLoading.set(false);
        }
      });
  }

  private setupIntersectionObserver(): void {
    this.intersectionObserver?.disconnect();

    if (!this.lastPageSentinel) return;

    this.intersectionObserver = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && !this.markedAsRead) {
          this.markAsRead();
        }
      },
      { threshold: 0.1 }
    );
    this.intersectionObserver.observe(this.lastPageSentinel.nativeElement);
  }

  private markAsRead(): void {
    if (this.markedAsRead) return;
    this.markedAsRead = true;
    this.workService.markChapterRead(this.slug, this.chapterId)
      .pipe(takeUntil(this.destroy$))
      .subscribe();
  }

  navigateToChapter(chapterId: string): void {
    this.stopAutoScroll();
    this.router.navigate(['/work', this.slug, 'chapter', chapterId]);
  }

  goBackToWork(): void {
    this.stopAutoScroll();
    this.router.navigate(['/work', this.slug]);
  }

  scrollToTop(): void {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  toggleAutoScroll(): void {
    this.isAutoScrolling() ? this.stopAutoScroll() : this.startAutoScroll();
  }

  private startAutoScroll(): void {
    this.isAutoScrolling.set(true);
    this.isFloatingMenuHidden.set(true);
    this.showAutoScrollHint.set(true);
    clearTimeout(this.hintTimeout);
    this.hintTimeout = setTimeout(() => this.showAutoScrollHint.set(false), 5000);
    const tick = () => {
      const max = document.documentElement.scrollHeight - window.innerHeight;
      if (window.scrollY >= max) {
        this.stopAutoScroll();
        return;
      }
      window.scrollBy(0, this.autoScrollSpeed());
      this.autoScrollRaf = requestAnimationFrame(tick);
    };
    this.autoScrollRaf = requestAnimationFrame(tick);
  }

  private stopAutoScroll(): void {
    if (this.autoScrollRaf) cancelAnimationFrame(this.autoScrollRaf);
    this.autoScrollRaf = undefined;
    clearTimeout(this.hintTimeout);
    this.isAutoScrolling.set(false);
    this.showAutoScrollHint.set(false);
  }

  setScrollSpeed(speed: number): void {
    this.autoScrollSpeed.set(speed);
    localStorage.setItem('reader.autoScrollSpeed', String(speed));
  }

  renderMarkdown(content: string | null): SafeHtml {
    if (!content) return '';
    const html = marked.parse(content) as string;
    return this.sanitizer.bypassSecurityTrustHtml(html);
  }

  private preloadUpcoming(data: ChapterReaderData): void {
    const nextImage = data.pages.find(page => page.type === 'IMAGE' && page.imageUrl)?.imageUrl;
    if (nextImage) {
      const image = new Image();
      image.src = nextImage;
    }
  }
}
