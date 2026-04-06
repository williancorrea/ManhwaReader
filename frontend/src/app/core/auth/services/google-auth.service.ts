import { inject, Injectable, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { environment } from '../../../../environments/environment';

declare global {
  interface Window {
    google?: {
      accounts: {
        id: {
          initialize: (config: GoogleIdConfig) => void;
          prompt: (callback?: (notification: PromptMomentNotification) => void) => void;
        };
      };
    };
  }
}

interface GoogleIdConfig {
  client_id: string;
  callback: (response: GoogleCredentialResponse) => void;
  auto_select?: boolean;
}

interface GoogleCredentialResponse {
  credential: string;
}

interface PromptMomentNotification {
  isNotDisplayed: () => boolean;
  isSkippedMoment: () => boolean;
  isDismissedMoment: () => boolean;
}

@Injectable({ providedIn: 'root' })
export class GoogleAuthService {
  private readonly platformId = inject(PLATFORM_ID);
  private googleCallback: ((idToken: string) => void) | null = null;

  readonly available = signal(false);

  initialize(onCredential: (idToken: string) => void): void {
    if (!isPlatformBrowser(this.platformId)) return;
    this.googleCallback = onCredential;
    this.loadScript();
  }

  prompt(): void {
    if (!isPlatformBrowser(this.platformId)) return;
    if (!window.google?.accounts?.id) return;
    window.google.accounts.id.prompt((notification: PromptMomentNotification) => {
      if (notification.isNotDisplayed() || notification.isSkippedMoment()) {
        // User dismissed or popup was blocked — not an error
      }
    });
  }

  private loadScript(): void {
    if (document.getElementById('google-gsi-script')) {
      this.onScriptLoaded();
      return;
    }

    const script = document.createElement('script');
    script.id = 'google-gsi-script';
    script.src = 'https://accounts.google.com/gsi/client';
    script.async = true;
    script.defer = true;
    script.onload = () => this.onScriptLoaded();
    script.onerror = () => { /* script failed — available stays false */ };
    document.head.appendChild(script);
  }

  private onScriptLoaded(): void {
    if (!window.google?.accounts?.id) return;
    window.google.accounts.id.initialize({
      client_id: environment.googleClientId,
      callback: (response: GoogleCredentialResponse) => {
        if (this.googleCallback) {
          this.googleCallback(response.credential);
        }
      },
      auto_select: false
    });
    this.available.set(true);
  }
}
