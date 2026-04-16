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

  readonly chapterData = signal<ChapterReaderData | null>(null);
  readonly isLoading = signal(true);
  readonly showScrollTop = signal(false);
  readonly isHeaderHidden = signal(false);
  readonly scrollProgress = signal(0);

  slug = '';
  chapterId = '';
  private lastScrollY = 0;

  @HostListener('window:scroll')
  onScroll(): void {
    const scrollY = window.scrollY;
    const max = Math.max(1, document.documentElement.scrollHeight - window.innerHeight);
    this.showScrollTop.set(scrollY > 300);
    this.isHeaderHidden.set(scrollY > this.lastScrollY && scrollY > 140);
    this.scrollProgress.set(Math.min(100, Math.max(0, Math.round((scrollY / max) * 100))));
    this.lastScrollY = scrollY;
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
    this.router.navigate(['/work', this.slug, 'chapter', chapterId]);
  }

  goBackToWork(): void {
    this.router.navigate(['/work', this.slug]);
  }

  scrollToTop(): void {
    window.scrollTo({ top: 0, behavior: 'smooth' });
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
