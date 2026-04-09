import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

export interface Manhwa {
  id?: number;
  slug?: string;
  title: string;
  coverUrl: string;
  latestChapter: number;
  genres: string[];
  isNew?: boolean;
  progress?: number;
  unreadCount?: number;
  inLibrary?: boolean;
}

export const GENRE_COLORS: Record<string, string> = {
  'Action':         '#EF4444',
  'Fantasy':        '#8B5CF6',
  'Comedy':         '#F59E0B',
  'Mystery':        '#6366F1',
  'Drama':          '#F97316',
  'Martial Arts':   '#DC2626',
  'School':         '#38BDF8',
  'Romance':        '#EC4899',
  'Sci-Fi':         '#06B6D4',
  'Slice of Life':  '#10B981',
  'Horror':         '#78716C',
  'Sports':         '#14B8A6',
  'Isekai':         '#A855F7',
  'Psychological':  '#64748B',
};

@Component({
  selector: 'app-manhwa-card',
  imports: [RouterLink],
  templateUrl: './manhwa-card.html',
  styleUrl: './manhwa-card.css'
})
export class ManhwaCardComponent {
  @Input({ required: true }) manhwa!: Manhwa;
  @Input() variant: 'grid' | 'horizontal' | 'compact' = 'grid';

  get visibleGenres(): string[] {
    return this.manhwa.genres.slice(0, 3);
  }

  get hiddenGenreCount(): number {
    return Math.max(0, this.manhwa.genres.length - 3);
  }

  getGenreStyle(genre: string): string {
    const color = GENRE_COLORS[genre] ?? '#76768A';
    return `background: ${color}26; color: ${color};`;
  }
}
