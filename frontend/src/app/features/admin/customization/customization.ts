import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../../../shared/components/navbar/navbar';
import { TranslatePipe } from '../../../core/i18n/translate.pipe';
import { AdminNavComponent } from '../components/admin-nav/admin-nav';

@Component({
  selector: 'app-admin-customization',
  imports: [NavbarComponent, AdminNavComponent, FormsModule, TranslatePipe],
  templateUrl: './customization.html',
  styleUrl: './customization.css'
})
export class AdminCustomizationComponent {
  readonly carouselItems = signal(12);
  readonly visualMode = signal('comfortable');
  readonly logoUrl = signal('');
  readonly primaryColor = signal('#e85d75');
  readonly accentColor = signal('#30d5c8');
}
