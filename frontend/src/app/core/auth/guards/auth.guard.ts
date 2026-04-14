import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { TokenService } from '../services/token.service';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (_route, state) => {
  const tokenService = inject(TokenService);
  const authService = inject(AuthService);
  const router = inject(Router);

  if (tokenService.isAuthenticated()) {
    return true;
  }

  return authService.refresh().pipe(
    map(() => true),
    catchError(() =>
      of(
        router.createUrlTree(['/auth/login'], {
          queryParams: { returnUrl: state.url }
        })
      )
    )
  );
};
