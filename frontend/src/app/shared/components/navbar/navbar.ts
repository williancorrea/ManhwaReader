import { Component, inject, signal, HostListener } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/services/auth.service';
import { UserService } from '../../../core/auth/services/user.service';

@Component({
  selector: 'app-navbar',
  imports: [FormsModule, RouterLink],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class NavbarComponent {
  readonly authService = inject(AuthService);
  readonly userService = inject(UserService);

  isSearchOpen = false;
  searchQuery = '';
  readonly isMenuOpen = signal(false);

  toggleSearch(): void {
    this.isSearchOpen = !this.isSearchOpen;
  }

  toggleMenu(): void {
    this.isMenuOpen.update(v => !v);
  }

  logout(): void {
    this.isMenuOpen.set(false);
    this.authService.logout();
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.navbar__user-menu')) {
      this.isMenuOpen.set(false);
    }
  }
}
