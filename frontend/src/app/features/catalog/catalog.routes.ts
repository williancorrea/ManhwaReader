import { Routes } from '@angular/router';

export const catalogRoutes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./catalog-list/catalog-list').then(m => m.CatalogListComponent)
  }
];
