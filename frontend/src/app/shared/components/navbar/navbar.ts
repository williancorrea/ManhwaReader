import { Component, computed, inject, signal, HostListener } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/auth/services/auth.service';
import { UserService } from '../../../core/auth/services/user.service';
import { I18nService } from '../../../core/i18n/i18n.service';
import { SupportedLocale } from '../../../core/i18n/i18n.models';
import { TranslatePipe } from '../../../core/i18n/translate.pipe';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, RouterLinkActive, TranslatePipe],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class NavbarComponent {
  readonly authService = inject(AuthService);
  readonly userService = inject(UserService);
  readonly i18n = inject(I18nService);

  readonly isMenuOpen = signal(false);
  readonly isAdmin = computed(() => this.userService.profile()?.roles?.includes('ADMINISTRATOR') ?? false);

  toggleMenu(): void {
    this.isMenuOpen.update(v => !v);
  }

  logout(): void {
    this.isMenuOpen.set(false);
    this.authService.logout();
  }

  setLanguage(value: string): void {
    if (value === 'pt-BR' || value === 'en-US') {
      this.i18n.setLanguage(value as SupportedLocale);
    }
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.navbar__user-menu')) {
      this.isMenuOpen.set(false);
    }
  }

  @HostListener('document:keydown.escape')
  onEscape(): void {
    this.isMenuOpen.set(false);
  }
}
