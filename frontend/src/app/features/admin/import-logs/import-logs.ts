import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../../../shared/components/navbar/navbar';
import { TranslatePipe } from '../../../core/i18n/translate.pipe';
import { AdminNavComponent } from '../components/admin-nav/admin-nav';

interface ImportLogMock {
  status: 'SUCCESS' | 'ERROR' | 'PARTIAL' | 'RUNNING';
  source: string;
  work: string;
  chapter: string;
  date: string;
  message: string;
  details: string;
}

@Component({
  selector: 'app-admin-import-logs',
  imports: [NavbarComponent, AdminNavComponent, FormsModule, TranslatePipe],
  templateUrl: './import-logs.html',
  styleUrl: './import-logs.css'
})
export class AdminImportLogsComponent {
  readonly status = signal('');
  readonly source = signal('');
  readonly work = signal('');
  readonly period = signal('');
  readonly logs = signal<ImportLogMock[]>([]);
}
