import { Routes } from '@angular/router';

export const libraryRoutes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./library-list/library-list').then(m => m.LibraryListComponent)
  }
];
