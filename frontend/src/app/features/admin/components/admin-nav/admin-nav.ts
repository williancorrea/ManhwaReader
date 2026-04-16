import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { TranslatePipe } from '../../../../core/i18n/translate.pipe';

@Component({
  selector: 'app-admin-nav',
  imports: [RouterLink, RouterLinkActive, TranslatePipe],
  templateUrl: './admin-nav.html',
  styleUrl: './admin-nav.css'
})
export class AdminNavComponent {}
