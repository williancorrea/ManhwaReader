import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { TokenService } from './token.service';
import { AuthResponse, LoginRequest, RegisterRequest, UserProfile } from '../models/auth.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly tokenService = inject(TokenService);
  private readonly apiUrl = `${environment.apiUrl}/auth`;
  private readonly options = { withCredentials: true };

  register(data: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.apiUrl}/register`, data, this.options)
      .pipe(tap(res => this.tokenService.setToken(res.accessToken)));
  }

  login(data: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.apiUrl}/login`, data, this.options)
      .pipe(tap(res => this.tokenService.setToken(res.accessToken)));
  }

  googleLogin(idToken: string): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.apiUrl}/google`, { idToken }, this.options)
      .pipe(tap(res => this.tokenService.setToken(res.accessToken)));
  }

  refresh(): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.apiUrl}/refresh`, null, this.options)
      .pipe(tap(res => this.tokenService.setToken(res.accessToken)));
  }

  getMe(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.apiUrl}/me`, this.options);
  }

  logout(): void {
    this.http
      .post<void>(`${this.apiUrl}/logout`, null, this.options)
      .subscribe({ complete: () => this.clearSessionAndRedirect() });
  }

  isAuthenticated(): boolean {
    return this.tokenService.isAuthenticated();
  }

  private clearSessionAndRedirect(): void {
    this.tokenService.clearToken();
    this.router.navigate(['/auth/login']);
  }
}
