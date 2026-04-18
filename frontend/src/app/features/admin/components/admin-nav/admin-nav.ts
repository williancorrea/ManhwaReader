import { Component, HostListener, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { TranslatePipe } from '../../../../core/i18n/translate.pipe';

interface AdminNavItem {
  readonly route: string;
  readonly labelKey: string;
  readonly icon: string;
}

interface AdminNavSection {
  readonly labelKey: string;
  readonly items: readonly AdminNavItem[];
}

@Component({
  selector: 'app-admin-nav',
  imports: [RouterLink, RouterLinkActive, TranslatePipe],
  templateUrl: './admin-nav.html',
  styleUrl: './admin-nav.css'
})
export class AdminNavComponent {
  readonly drawerOpen = signal(false);

  readonly sections: readonly AdminNavSection[] = [
    {
      labelKey: 'admin.nav.section.settings',
      items: [
        { route: '/admin/settings', labelKey: 'admin.nav.settings', icon: 'settings' },
        { route: '/admin/customization', labelKey: 'admin.nav.customization', icon: 'palette' }
      ]
    },
    {
      labelKey: 'admin.nav.section.synchronization',
      items: [
        { route: '/admin/synchronization', labelKey: 'admin.nav.synchronization', icon: 'link' },
        { route: '/admin/sync-errors', labelKey: 'admin.nav.syncErrors', icon: 'alert' },
        { route: '/admin/import-logs', labelKey: 'admin.nav.importLogs', icon: 'logs' }
      ]
    }
  ];

  constructor(private readonly router: Router) {
    this.router.events.subscribe(() => this.drawerOpen.set(false));
  }

  toggleDrawer(): void {
    this.drawerOpen.update(value => !value);
  }

  closeDrawer(): void {
    this.drawerOpen.set(false);
  }

  @HostListener('window:keydown.escape')
  onEscape(): void {
    if (this.drawerOpen()) this.closeDrawer();
  }
}
