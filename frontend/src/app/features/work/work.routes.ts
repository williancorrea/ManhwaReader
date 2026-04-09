import { Routes } from '@angular/router';

export const workRoutes: Routes = [
  {
    path: ':slug',
    loadComponent: () =>
      import('./work-detail/work-detail').then(m => m.WorkDetailComponent)
  },
  {
    path: ':slug/chapter/:chapterId',
    loadComponent: () =>
      import('./chapter-reader/chapter-reader').then(m => m.ChapterReaderComponent)
  }
];
