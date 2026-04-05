import { inject, Injectable, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { BehaviorSubject } from 'rxjs';

interface JwtClaims {
  sub: string;
  email: string;
  name: string;
  exp: number;
  iat: number;
}

@Injectable({ providedIn: 'root' })
export class TokenService {
  private readonly platformId = inject(PLATFORM_ID);
  private readonly tokenSubject = new BehaviorSubject<string | null>(null);

  readonly token$ = this.tokenSubject.asObservable();

  getToken(): string | null {
    return this.tokenSubject.value;
  }

  setToken(token: string): void {
    this.tokenSubject.next(token);
  }

  clearToken(): void {
    this.tokenSubject.next(null);
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) return false;
    return !this.isTokenExpired();
  }

  isTokenExpired(): boolean {
    if (!isPlatformBrowser(this.platformId)) return true;
    const claims = this.decodeClaims();
    if (!claims) return true;
    return Date.now() / 1000 >= claims.exp;
  }

  getClaims(): JwtClaims | null {
    if (!isPlatformBrowser(this.platformId)) return null;
    return this.decodeClaims();
  }

  private decodeClaims(): JwtClaims | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const payload = token.split('.')[1];
      const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
      return JSON.parse(decoded) as JwtClaims;
    } catch {
      return null;
    }
  }
}
