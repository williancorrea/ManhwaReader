import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../../../shared/components/navbar/navbar';
import { TranslatePipe } from '../../../core/i18n/translate.pipe';
import { AdminNavComponent } from '../components/admin-nav/admin-nav';

@Component({
  selector: 'app-admin-settings',
  imports: [NavbarComponent, AdminNavComponent, FormsModule, TranslatePipe],
  templateUrl: './settings.html',
  styleUrl: './settings.css'
})
export class AdminSettingsComponent {
  readonly siteName = signal('ManhwaReader');
  readonly defaultLanguage = signal('pt-BR');
  readonly defaultTheme = signal('dark');
  readonly readingWidth = signal('920');
  readonly sections = signal([
    { key: 'admin.settings.sectionContinue', enabled: true },
    { key: 'admin.settings.sectionFeatured', enabled: true },
    { key: 'admin.settings.sectionLatest', enabled: true }
  ]);
}
