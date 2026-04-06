import { inject, Injectable, signal } from '@angular/core';
import { filter, switchMap, catchError, EMPTY } from 'rxjs';
import { UserProfile } from '../models/auth.models';
import { AuthService } from './auth.service';
import { TokenService } from './token.service';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly authService = inject(AuthService);
  private readonly tokenService = inject(TokenService);

  readonly profile = signal<UserProfile | null>(null);

  constructor() {
    // React to token changes:
    //   token set → load profile from API
    //   token cleared → clear cached profile
    this.tokenService.token$.pipe(
      filter(token => token === null),
    ).subscribe(() => this.profile.set(null));

    this.tokenService.token$.pipe(
      filter((token): token is string => token !== null),
      switchMap(() =>
        this.authService.getMe().pipe(
          catchError(() => EMPTY)
        )
      )
    ).subscribe(profile => this.profile.set(profile));
  }

  initials(): string {
    const p = this.profile();
    if (!p?.name) return 'U';
    return p.name
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map(w => w[0].toUpperCase())
      .join('');
  }
}
