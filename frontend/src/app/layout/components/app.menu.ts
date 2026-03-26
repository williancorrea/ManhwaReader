import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AppMenuitem } from './app.menuitem';

@Component({
    selector: 'app-menu',
    standalone: true,
    imports: [CommonModule, AppMenuitem, RouterModule],
    template: `<ul class="layout-menu">
        @for (item of model; track item.label) {
            @if (!item.separator) {
                <li app-menuitem [item]="item" [root]="true"></li>
            } @else {
                <li class="menu-separator"></li>
            }
        }
    </ul> `,
    host: {
        class: 'layout-menu-container'
    }
})
export class AppMenu {
    model: any[] = [
        {
            label: 'Principal',
            icon: 'pi pi-home',
            items: [
                {
                    label: 'Home',
                    icon: 'pi pi-fw pi-home',
                    routerLink: ['/']
                },
                {
                    label: 'Catálogo',
                    icon: 'pi pi-fw pi-book',
                    routerLink: ['/catalog']
                }
            ]
        },
        { separator: true },
        {
            label: 'Conta',
            icon: 'pi pi-user',
            items: [
                {
                    label: 'Login',
                    icon: 'pi pi-fw pi-sign-in',
                    routerLink: ['/auth/login']
                },
                {
                    label: 'Registrar',
                    icon: 'pi pi-fw pi-user-plus',
                    routerLink: ['/auth/register']
                }
            ]
        }
    ];
}
