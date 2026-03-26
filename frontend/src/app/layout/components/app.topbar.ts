import {Component, computed, ElementRef, inject} from '@angular/core';
import {MenuItem} from 'primeng/api';
import {RouterModule} from '@angular/router';
import {CommonModule} from '@angular/common';
import {StyleClassModule} from 'primeng/styleclass';
import {LayoutService} from '@/app/layout/service/layout.service';
import {ButtonModule} from 'primeng/button';

@Component({
    selector: '[app-topbar]',
    standalone: true,
    imports: [RouterModule, CommonModule, StyleClassModule, ButtonModule],
    template: `
        <div class="topbar-start">
            <a [routerLink]="['/']" class="app-logo cursor-pointer flex items-center gap-2">
                <img class="h-8" [src]="'/demo/images/logo-' + logo() + '.png'"/>
                <img class="h-8 hidden lg:block" [src]="'/demo/images/appname-' + logo() + '.png'"/>
            </a>
        </div>

        <nav class="layout-topbar-menu-section hidden lg:flex items-center gap-6">
            <a routerLink="/" routerLinkActive="text-primary-500" [routerLinkActiveOptions]="{ exact: true }"
               class="flex items-center gap-2 cursor-pointer hover:text-primary-500 duration-200 font-medium">
                <i class="pi pi-home"></i>
                <span>Home</span>
            </a>
            <a routerLink="/catalog" routerLinkActive="text-primary-500"
               class="flex items-center gap-2 cursor-pointer hover:text-primary-500 duration-200 font-medium">
                <i class="pi pi-book"></i>
                <span>Catálogo</span>
            </a>
            <a routerLink="/auth/login" routerLinkActive="text-primary-500"
               class="flex items-center gap-2 cursor-pointer hover:text-primary-500 duration-200 font-medium">
                <i class="pi pi-sign-in"></i>
                <span>Login</span>
            </a>
            <a routerLink="/auth/register" routerLinkActive="text-primary-500"
               class="flex items-center gap-2 cursor-pointer hover:text-primary-500 duration-200 font-medium">
                <i class="pi pi-user-plus"></i>
                <span>Registrar</span>
            </a>
        </nav>

        <div class="topbar-end">
            <ul class="topbar-menu">
                <li class="profile-item topbar-item">
                    <a pStyleClass="@next" enterFromClass="!hidden" enterActiveClass="animate-scalein"
                       leaveToClass="!hidden" leaveActiveClass="animate-fadeout" [hideOnOutsideClick]="true"
                       class="cursor-pointer">
                        <img class="rounded-full" src="/demo/images/avatar-m-1.jpg"/>
                    </a>

                    <ul class="topbar-menu active-topbar-menu p-6! w-60 z-50 !hidden rounded shadow-xl">
                        <li role="menuitem" class="m-0! mb-4!">
                            <a
                                href="#"
                                class="flex items-center hover:text-primary-500 duration-200"
                                pStyleClass="@grandparent"
                                enterFromClass="!hidden"
                                enterActiveClass="animate-scalein"
                                leaveToClass="!hidden"
                                leaveActiveClass="animate-fadeout"
                                [hideOnOutsideClick]="true"
                            >
                                <i class="pi pi-fw pi-lock mr-2"></i>
                                <span>Privacy</span>
                            </a>
                        </li>

                        <li role="menuitem" class="m-0! mb-4!">
                            <a
                                href="#"
                                class="flex items-center hover:text-primary-500 duration-200"
                                pStyleClass="@grandparent"
                                enterFromClass="!hidden"
                                enterActiveClass="animate-scalein"
                                leaveToClass="!hidden"
                                leaveActiveClass="animate-fadeout"
                                [hideOnOutsideClick]="true"
                            >
                                <i class="pi pi-fw pi-cog mr-2"></i>
                                <span>Settings</span>
                            </a>
                        </li>
                        <li role="menuitem" class="m-0!">
                            <a
                                href="#"
                                class="flex items-center hover:text-primary-500 duration-200"
                                pStyleClass="@grandparent"
                                enterFromClass="!hidden"
                                enterActiveClass="animate-scalein"
                                leaveToClass="!hidden"
                                leaveActiveClass="animate-fadeout"
                                [hideOnOutsideClick]="true"
                            >
                                <i class="pi pi-fw pi-sign-out mr-2"></i>
                                <span>Logout</span>
                            </a>
                        </li>
                    </ul>
                </li>

            </ul>
        </div>
    `,
    host: {
        class: 'layout-topbar'
    }
})
export class AppTopbar {
    menu: MenuItem[] = [];

    el = inject(ElementRef);

    constructor(public layoutService: LayoutService) {
    }

    logo = computed(() => this.layoutService.logo());
}
