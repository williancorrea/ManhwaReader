import { Routes } from '@angular/router';

export const adminRoutes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./synchronization/synchronization').then(m => m.AdminSynchronizationComponent)
  }
];
