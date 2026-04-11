import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpErrorResponse, HttpHandlerFn, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { BehaviorSubject, catchError, filter, switchMap, take, throwError } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { TokenService } from '../services/token.service';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { AuthResponse } from '../models/auth.models';

let isRefreshing = false;
const refreshTokenSubject = new BehaviorSubject<string | null>(null);

const AUTH_ENDPOINTS = ['/auth/refresh', '/auth/login', '/auth/register', '/auth/google'];

function isAuthEndpoint(url: string): boolean {
  return AUTH_ENDPOINTS.some(endpoint => url.includes(endpoint));
}

function isApiUrl(url: string): boolean {
  return url.startsWith(environment.apiUrl);
}

function addAuthHeader(req: HttpRequest<unknown>, token: string): HttpRequest<unknown> {
  return req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
}

export const authInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
) => {
  const platformId = inject(PLATFORM_ID);
  const tokenService = inject(TokenService);
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!isApiUrl(req.url)) {
    return next(req);
  }

  // On the server (SSR) there are no browser cookies, so skip auth logic entirely
  if (!isPlatformBrowser(platformId)) {
    return next(req);
  }

  const token = tokenService.getToken();
  const authReq = token ? addAuthHeader(req, token) : req;

  return next(authReq).pipe(
    catchError((error: unknown) => {
      if (!(error instanceof HttpErrorResponse) || error.status !== 401) {
        return throwError(() => error);
      }

      if (isAuthEndpoint(req.url)) {
        return throwError(() => error);
      }

      if (isRefreshing) {
        return refreshTokenSubject.pipe(
          filter((t): t is string => t !== null),
          take(1),
          switchMap(newToken => next(addAuthHeader(req, newToken)))
        );
      }

      isRefreshing = true;
      refreshTokenSubject.next(null);

      return authService.refresh().pipe(
        switchMap((res: AuthResponse) => {
          isRefreshing = false;
          refreshTokenSubject.next(res.accessToken);
          return next(addAuthHeader(req, res.accessToken));
        }),
        catchError((refreshError: unknown) => {
          isRefreshing = false;
          refreshTokenSubject.next(null);
          tokenService.clearToken();
          router.navigate(['/auth/login']);
          return throwError(() => refreshError);
        })
      );
    })
  );
};
