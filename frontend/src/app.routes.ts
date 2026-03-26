import { Routes } from '@angular/router';
import { AppLayout } from '@/app/layout/components/app.layout';

export const appRoutes: Routes = [
    {
        path: '',
        component: AppLayout,
        children: [
            {
                path: '',
                data: { breadcrumb: 'Home' },
                loadComponent: () => import('@/app/pages/home/home').then((c) => c.Home)
            },
            {
                path: 'catalog',
                data: { breadcrumb: 'Catálogo' },
                loadComponent: () => import('@/app/pages/catalog/catalog').then((c) => c.Catalog)
            },
            {
                path: 'manhwa/:id',
                data: { breadcrumb: 'Detalhes' },
                loadComponent: () => import('@/app/pages/notfound/notfound').then((c) => c.Notfound)
            }
        ]
    },
    { path: 'auth', loadChildren: () => import('@/app/pages/auth/auth.routes') },
    {
        path: 'notfound',
        loadComponent: () => import('@/app/pages/notfound/notfound').then((c) => c.Notfound)
    },
    { path: '**', redirectTo: '/notfound' }
];
