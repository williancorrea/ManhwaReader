import { Component, inject, OnInit, afterNextRender, signal, computed } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../../core/auth/services/auth.service';
import { GoogleAuthService } from '../../../core/auth/services/google-auth.service';
import { ApiError } from '../../../core/auth/models/auth.models';
import { I18nService } from '../../../core/i18n/i18n.service';
import { TranslatePipe } from '../../../core/i18n/translate.pipe';

const ERROR_KEYS: Record<string, string> = {
  'auth.error.invalid-credentials': 'auth.error.invalidCredentials',
  'auth.error.google-account': 'auth.error.googleAccount'
};

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink, TranslatePipe],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly googleAuthService = inject(GoogleAuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly i18n = inject(I18nService);

  readonly isLoading = signal(false);
  readonly errorMessage = signal('');
  readonly googleAvailable = computed(() => this.googleAuthService.available());

  readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(5)]]
  });

  constructor() {
    afterNextRender(() => {
      this.googleAuthService.initialize((idToken: string) => {
        this.handleGoogleCredential(idToken);
      });
    });
  }

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set('');

    const { email, password } = this.form.getRawValue();

    this.authService.login({ email, password }).subscribe({
      next: () => this.navigateAfterAuth(),
      error: (err: unknown) => {
        this.isLoading.set(false);
        this.errorMessage.set(this.extractErrorMessage(err));
      }
    });
  }

  onGoogleLogin(): void {
    this.googleAuthService.prompt();
  }

  private handleGoogleCredential(idToken: string): void {
    this.isLoading.set(true);
    this.errorMessage.set('');

    this.authService.googleLogin(idToken).subscribe({
      next: () => this.navigateAfterAuth(),
      error: (err: unknown) => {
        this.isLoading.set(false);
        this.errorMessage.set(this.extractErrorMessage(err));
      }
    });
  }

  private navigateAfterAuth(): void {
    const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl') ?? '/home';
    this.router.navigateByUrl(returnUrl);
  }

  private extractErrorMessage(err: unknown): string {
    if (err instanceof HttpErrorResponse) {
      const body = err.error as ApiError;
      const key = body?.items?.[0]?.key;
      if (key && ERROR_KEYS[key]) return this.i18n.t(ERROR_KEYS[key]);
    }
    return this.i18n.t('auth.error.generic');
  }
}
