import { Routes } from '@angular/router';

export const adminRoutes: Routes = [
  {
    path: '',
    redirectTo: 'settings',
    pathMatch: 'full'
  },
  {
    path: 'settings',
    loadComponent: () =>
      import('./settings/settings').then(m => m.AdminSettingsComponent)
  },
  {
    path: 'synchronization',
    loadComponent: () =>
      import('./synchronization/synchronization').then(m => m.AdminSynchronizationComponent)
  },
  {
    path: 'import-logs',
    loadComponent: () =>
      import('./import-logs/import-logs').then(m => m.AdminImportLogsComponent)
  },
  {
    path: 'customization',
    loadComponent: () =>
      import('./customization/customization').then(m => m.AdminCustomizationComponent)
  }
];
