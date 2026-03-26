import { Injectable, effect, signal, computed, inject } from '@angular/core';
import { Router } from '@angular/router';

export interface LayoutConfig {
    preset: string;
    primary: string;
    surface: string | undefined | null;
    darkTheme: boolean;
}

interface LayoutState {
    profileSidebarVisible: boolean;
    configSidebarVisible: boolean;
    searchBarActive: boolean;
    rightMenuActive: boolean;
    topbarMenuActive: boolean;
    menuProfileActive: boolean;
}

@Injectable({
    providedIn: 'root'
})
export class LayoutService {
    layoutConfig = signal<LayoutConfig>({
        preset: 'Aura',
        primary: 'emerald',
        surface: null,
        darkTheme: true
    });

    layoutState = signal<LayoutState>({
        configSidebarVisible: false,
        searchBarActive: false,
        profileSidebarVisible: false,
        rightMenuActive: false,
        topbarMenuActive: false,
        menuProfileActive: false
    });

    router = inject(Router);

    isDarkTheme = computed(() => this.layoutConfig().darkTheme);

    logo = computed(() => (this.layoutConfig().darkTheme ? 'light' : 'dark'));

    private initialized = false;

    constructor() {
        effect(() => {
            const config = this.layoutConfig();

            if (!this.initialized || !config) {
                this.initialized = true;
                return;
            }

            this.handleDarkModeTransition(config);
        });
    }

    private handleDarkModeTransition(config: LayoutConfig): void {
        const supportsViewTransition = 'startViewTransition' in document;

        if (supportsViewTransition) {
            this.startViewTransition(config);
        } else {
            this.toggleDarkMode(config);
        }
    }

    private startViewTransition(config: LayoutConfig): void {
        document.startViewTransition(() => {
            this.toggleDarkMode(config);
        });
    }

    toggleDarkMode(config?: LayoutConfig): void {
        const _config = config || this.layoutConfig();
        if (_config.darkTheme) {
            document.documentElement.classList.add('app-dark');
        } else {
            document.documentElement.classList.remove('app-dark');
        }
    }

    onMenuProfileToggle() {
        this.layoutState.update((prev) => ({ ...prev, menuProfileActive: !prev.menuProfileActive }));
    }

    toggleProfileSidebar() {
        this.layoutState.update((prev) => ({
            ...prev,
            profileSidebarVisible: !prev.profileSidebarVisible
        }));
    }

    toggleConfigSidebar() {
        this.layoutState.update((prev) => ({
            ...prev,
            configSidebarVisible: !prev.configSidebarVisible
        }));
    }

    showConfigSidebar() {
        this.layoutState.update((prev) => ({
            ...prev,
            configSidebarVisible: true
        }));
    }

    hideConfigSidebar() {
        this.layoutState.update((prev) => ({ ...prev, configSidebarVisible: false }));
    }

    showProfileSidebar() {
        this.layoutState.update((prev) => ({
            ...prev,
            profileSidebarVisible: true
        }));
    }

    isDesktop() {
        return window.innerWidth > 991;
    }
}
