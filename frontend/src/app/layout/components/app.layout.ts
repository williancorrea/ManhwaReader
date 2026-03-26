import { Component, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AppTopbar } from './app.topbar';
import { LayoutService } from '@/app/layout/service/layout.service';
import { AppConfigurator } from './app.configurator';
import { AppBreadcrumb } from './app.breadcrumb';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { AppProfileMenu } from '@/app/layout/components/app.profilemenu';

@Component({
    selector: 'app-layout',
    standalone: true,
    imports: [CommonModule, AppTopbar, RouterModule, AppConfigurator, AppBreadcrumb, ToastModule, AppProfileMenu],
    template: `
        <div class="layout-wrapper" [ngClass]="containerClass()">
            <div class="layout-content-wrapper">
                <div app-topbar></div>

                <div class="content-breadcrumb">
                    <div app-breadcrumb></div>
                </div>

                <div class="layout-content">
                    <router-outlet></router-outlet>
                </div>

                <div class="layout-mask"></div>
            </div>
            <app-profile-menu />
            <app-configurator />
            <p-toast />
        </div>
    `,
    providers: [MessageService]
})
export class AppLayout {
    layoutService = inject(LayoutService);

    containerClass = computed(() => {
        const layoutConfig = this.layoutService.layoutConfig();
        return {
            'layout-light': !layoutConfig.darkTheme,
            'layout-dark': layoutConfig.darkTheme
        };
    });
}
