import { Injectable, signal } from '@angular/core';
import { TRANSLATIONS } from './i18n.dictionaries';
import { LocaleOption, SupportedLocale, TranslationParams } from './i18n.models';

const DEFAULT_LOCALE: SupportedLocale = 'pt-BR';
const STORAGE_KEY = 'manhwa-reader-locale';

@Injectable({ providedIn: 'root' })
export class I18nService {
  readonly languageOptions: LocaleOption[] = [
    { code: 'pt-BR', label: 'PT-BR' },
    { code: 'en-US', label: 'EN-US' }
  ];

  readonly language = signal<SupportedLocale>(this.readStoredLocale());

  setLanguage(locale: SupportedLocale): void {
    this.language.set(locale);
    if (typeof localStorage !== 'undefined') {
      localStorage.setItem(STORAGE_KEY, locale);
    }
  }

  t(key: string, params?: TranslationParams): string {
    const locale = this.language();
    const template = TRANSLATIONS[locale][key] ?? TRANSLATIONS['en-US'][key] ?? key;
    if (!params) return template;
    return template.replace(/\{(\w+)\}/g, (_, param: string) => String(params[param] ?? `{${param}}`));
  }

  relativeTime(dateValue: string | Date): string {
    const date = dateValue instanceof Date ? dateValue : new Date(dateValue);
    if (Number.isNaN(date.getTime())) return '';

    const seconds = Math.round((date.getTime() - Date.now()) / 1000);
    const divisions: Array<{ amount: number; unit: Intl.RelativeTimeFormatUnit }> = [
      { amount: 60, unit: 'second' },
      { amount: 60, unit: 'minute' },
      { amount: 24, unit: 'hour' },
      { amount: 30, unit: 'day' },
      { amount: 12, unit: 'month' },
      { amount: Number.POSITIVE_INFINITY, unit: 'year' }
    ];

    let duration = seconds;
    for (const division of divisions) {
      if (Math.abs(duration) < division.amount) {
        return new Intl.RelativeTimeFormat(this.language(), { numeric: 'auto' }).format(
          Math.round(duration),
          division.unit
        );
      }
      duration /= division.amount;
    }

    return '';
  }

  private readStoredLocale(): SupportedLocale {
    if (typeof localStorage === 'undefined') return DEFAULT_LOCALE;
    const stored = localStorage.getItem(STORAGE_KEY);
    return stored === 'en-US' || stored === 'pt-BR' ? stored : DEFAULT_LOCALE;
  }
}
