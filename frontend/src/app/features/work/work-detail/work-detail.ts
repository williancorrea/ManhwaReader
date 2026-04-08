import {
  AfterViewInit,
  Component,
  ElementRef,
  HostListener,
  inject,
  OnDestroy,
  OnInit,
  PLATFORM_ID,
  signal,
  ViewChild
} from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NavbarComponent } from '../../../shared/components/navbar/navbar';
import { GENRE_COLORS } from '../../../shared/components/manhwa-card/manhwa-card';
import { WorkService } from '../services/work.service';
import { ChapterItem, LIBRARY_STATUSES, SITE_LABELS, WorkDetail } from '../models/work.models';

@Component({
  selector: 'app-work-detail',
  imports: [NavbarComponent],
  templateUrl: './work-detail.html',
  styleUrl: './work-detail.css'
})
export class WorkDetailComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('chapterSentinel') private chapterSentinel!: ElementRef<HTMLElement>;

  private readonly route = inject(ActivatedRoute);
  private readonly workService = inject(WorkService);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly destroy$ = new Subject<void>();
  private intersectionObserver?: IntersectionObserver;

  readonly work = signal<WorkDetail | null>(null);
  readonly chapters = signal<ChapterItem[]>([]);
  readonly isLoadingWork = signal(true);
  readonly isLoadingChapters = signal(false);
  readonly hasNextPage = signal(false);
  readonly currentPage = signal(0);
  readonly synopsisExpanded = signal(false);
  readonly showLibraryMenu = signal(false);
  readonly togglingChapterIds = signal<Set<string>>(new Set());
  readonly showScrollTop = signal(false);
  readonly isTogglingAll = signal(false);

  @HostListener('window:scroll')
  onScroll(): void {
    if (!isPlatformBrowser(this.platformId)) return;
    this.showScrollTop.set(window.scrollY > 300);
  }

  scrollToTop(): void {
    if (!isPlatformBrowser(this.platformId)) return;
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  chapterSort = 'desc';
  slug = '';

  readonly libraryStatuses = LIBRARY_STATUSES;
  readonly siteLabels = SITE_LABELS;

  ngOnInit(): void {
    this.slug = this.route.snapshot.paramMap.get('slug') ?? '';
    this.loadWork();
  }

  ngAfterViewInit(): void {
    if (!isPlatformBrowser(this.platformId)) return;
    this.intersectionObserver = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && !this.isLoadingChapters() && this.hasNextPage()) {
          this.loadChapters(this.currentPage() + 1);
        }
      },
      { rootMargin: '300px' }
    );
    if (this.chapterSentinel) {
      this.intersectionObserver.observe(this.chapterSentinel.nativeElement);
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.intersectionObserver?.disconnect();
  }

  private loadWork(): void {
    this.isLoadingWork.set(true);
    this.workService.getWorkDetail(this.slug)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (work) => {
          this.work.set(work);
          this.isLoadingWork.set(false);
          this.loadChapters(0);
        },
        error: () => this.isLoadingWork.set(false)
      });
  }

  loadChapters(page: number): void {
    this.isLoadingChapters.set(true);
    this.workService.getChapters(this.slug, page, 50, this.chapterSort)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          if (page === 0) {
            this.chapters.set(response.content);
          } else {
            this.chapters.update(current => [...current, ...response.content]);
          }
          this.currentPage.set(response.page.number);
          this.hasNextPage.set(response.page.number < response.page.totalPages - 1);
          this.isLoadingChapters.set(false);
          this.recheckSentinel();
        },
        error: () => this.isLoadingChapters.set(false)
      });
  }

  toggleSort(): void {
    this.chapterSort = this.chapterSort === 'desc' ? 'asc' : 'desc';
    this.loadChapters(0);
  }

  get allChaptersRead(): boolean {
    const list = this.chapters();
    return list.length > 0 && list.every(c => c.isRead);
  }

  toggleAllChaptersRead(): void {
    if (this.isTogglingAll()) return;
    this.isTogglingAll.set(true);

    const action = this.allChaptersRead
      ? this.workService.markAllChaptersUnread(this.slug)
      : this.workService.markAllChaptersRead(this.slug);

    action.pipe(takeUntil(this.destroy$)).subscribe({
      next: () => this.loadChapters(0),
      error: () => this.isTogglingAll.set(false),
      complete: () => this.isTogglingAll.set(false)
    });
  }

  toggleSynopsis(): void {
    this.synopsisExpanded.update(v => !v);
  }

  toggleLibraryMenu(): void {
    this.showLibraryMenu.update(v => !v);
  }

  setLibraryStatus(status: string): void {
    this.showLibraryMenu.set(false);
    this.workService.addToLibrary(this.slug, status)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.work.update(w => w ? { ...w, userLibraryStatus: status } : w);
      });
  }

  removeFromLibrary(): void {
    this.showLibraryMenu.set(false);
    this.workService.removeFromLibrary(this.slug)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.work.update(w => w ? { ...w, userLibraryStatus: null } : w);
      });
  }

  isTogglingChapter(id: string): boolean {
    return this.togglingChapterIds().has(id);
  }

  private addTogglingChapter(id: string): void {
    this.togglingChapterIds.update(set => new Set(set).add(id));
  }

  private removeTogglingChapter(id: string): void {
    this.togglingChapterIds.update(set => { const s = new Set(set); s.delete(id); return s; });
  }

  toggleChapterRead(chapter: ChapterItem): void {
    if (this.isTogglingChapter(chapter.id)) return;
    this.addTogglingChapter(chapter.id);
    const action = chapter.isRead
      ? this.workService.markChapterUnread(this.slug, chapter.id)
      : this.workService.markChapterRead(this.slug, chapter.id);

    action.pipe(takeUntil(this.destroy$)).subscribe({
      next: () => this.loadChapters(0),
      error: () => {},
      complete: () => this.removeTogglingChapter(chapter.id)
    });
  }

  getTagStyle(tagName: string): string {
    const color = GENRE_COLORS[tagName] ?? '#76768A';
    return `background: ${color}26; color: ${color};`;
  }

  getLibraryLabel(status: string | null): string {
    if (!status) return 'Adicionar à Biblioteca';
    return this.libraryStatuses.find(s => s.value === status)?.label ?? status;
  }

  getSiteLabel(code: string): string {
    return this.siteLabels[code] ?? code;
  }

  formatDate(dateStr: string): string {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    const now = new Date();
    const diff = Math.floor((now.getTime() - date.getTime()) / 1000);
    if (diff < 60) return 'agora';
    if (diff < 3600) return `há ${Math.floor(diff / 60)} min`;
    if (diff < 86400) return `há ${Math.floor(diff / 3600)}h`;
    if (diff < 2592000) return `há ${Math.floor(diff / 86400)} dias`;
    if (diff < 31536000) return `há ${Math.floor(diff / 2592000)} meses`;
    return `há ${Math.floor(diff / 31536000)} anos`;
  }

  private recheckSentinel(): void {
    if (!this.intersectionObserver || !this.chapterSentinel) return;
    setTimeout(() => {
      this.intersectionObserver!.unobserve(this.chapterSentinel.nativeElement);
      this.intersectionObserver!.observe(this.chapterSentinel.nativeElement);
    });
  }
}
