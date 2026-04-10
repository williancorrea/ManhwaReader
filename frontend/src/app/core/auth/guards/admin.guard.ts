import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { CanActivateFn, Router } from '@angular/router';
import { filter, map, take } from 'rxjs';
import { toObservable } from '@angular/core/rxjs-interop';
import { UserService } from '../services/user.service';
import { UserProfile } from '../models/auth.models';

export const adminGuard: CanActivateFn = () => {
  if (!isPlatformBrowser(inject(PLATFORM_ID))) {
    return true;
  }

  const userService = inject(UserService);
  const router = inject(Router);

  const profile = userService.profile();
  if (profile) {
    return profile.roles?.includes('ADMINISTRATOR')
      ? true
      : router.createUrlTree(['/home']);
  }

  return toObservable(userService.profile).pipe(
    filter((p): p is UserProfile => p !== null),
    take(1),
    map(p => p.roles?.includes('ADMINISTRATOR')
      ? true
      : router.createUrlTree(['/home'])
    )
  );
};
