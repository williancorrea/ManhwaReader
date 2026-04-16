export type SupportedLocale = 'pt-BR' | 'en-US';

export interface LocaleOption {
  code: SupportedLocale;
  label: string;
}

export type TranslationParams = Record<string, string | number>;

export type TranslationDictionary = Record<string, string>;
