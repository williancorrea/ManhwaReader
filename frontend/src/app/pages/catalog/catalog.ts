import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { TagModule } from 'primeng/tag';
import { SelectButtonModule } from 'primeng/selectbutton';
import { PaginatorModule, PaginatorState } from 'primeng/paginator';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { Manhwa, ManhwaStatus } from '@/app/types/manhwa';
import { ManhwaService } from '@/app/pages/service/manhwa.service';

type SortOption = 'relevance' | 'recent' | 'rating' | 'chapters';
type ViewMode = 'grid' | 'list';

@Component({
    selector: 'app-catalog',
    standalone: true,
    imports: [CommonModule, RouterModule, FormsModule, ButtonModule, InputTextModule, TagModule, SelectButtonModule, PaginatorModule, IconFieldModule, InputIconModule],
    template: `
        <div class="flex flex-col gap-6">

            <!-- Header -->
            <div>
                <h1 class="text-2xl font-bold text-white mb-1">Explorar Obras</h1>
                <p class="text-surface-400 text-sm">{{ totalResults() }} obras encontradas</p>
            </div>

            <!-- Search + Sort + View -->
            <div class="flex flex-col sm:flex-row gap-3 items-stretch sm:items-center">
                <div class="flex-1">
                    <p-icon-field>
                        <p-inputicon class="pi pi-search" />
                        <input
                            pInputText
                            type="text"
                            placeholder="Buscar por título, autor ou gênero..."
                            [(ngModel)]="searchQuery"
                            (ngModelChange)="onSearch()"
                            class="w-full"
                        />
                    </p-icon-field>
                </div>
                <div class="flex gap-2 items-center">
                    <select
                        [(ngModel)]="sortBy"
                        (ngModelChange)="onSortChange()"
                        class="px-3 py-2 rounded-lg bg-surface-800 border border-surface-700 text-surface-200 text-sm focus:outline-none focus:border-purple-500"
                    >
                        <option value="relevance">Relevância</option>
                        <option value="recent">Mais Recentes</option>
                        <option value="rating">Melhor Avaliados</option>
                        <option value="chapters">Mais Capítulos</option>
                    </select>
                    <div class="flex border border-surface-700 rounded-lg overflow-hidden">
                        <button
                            (click)="viewMode.set('grid')"
                            [class]="viewMode() === 'grid' ? 'bg-purple-600 text-white' : 'bg-surface-800 text-surface-400 hover:text-white'"
                            class="px-3 py-2 transition-colors"
                        >
                            <i class="pi pi-th-large text-sm"></i>
                        </button>
                        <button
                            (click)="viewMode.set('list')"
                            [class]="viewMode() === 'list' ? 'bg-purple-600 text-white' : 'bg-surface-800 text-surface-400 hover:text-white'"
                            class="px-3 py-2 transition-colors"
                        >
                            <i class="pi pi-list text-sm"></i>
                        </button>
                    </div>
                </div>
            </div>

            <!-- Genre Filters -->
            <div class="flex gap-2 flex-wrap items-center">
                <button
                    (click)="selectGenre(null)"
                    [class]="selectedGenre() === null ? 'bg-purple-600 border-purple-600 text-white' : 'bg-transparent border-surface-700 text-surface-400 hover:border-purple-500 hover:text-white'"
                    class="px-4 py-1.5 rounded-full border text-sm font-medium transition-all duration-200"
                >
                    Todos
                </button>
                @for (genre of allGenres(); track genre) {
                    <button
                        (click)="selectGenre(genre)"
                        [class]="selectedGenre() === genre ? 'bg-purple-600 border-purple-600 text-white' : 'bg-transparent border-surface-700 text-surface-400 hover:border-purple-500 hover:text-white'"
                        class="px-4 py-1.5 rounded-full border text-sm font-medium transition-all duration-200"
                    >
                        {{ genre }}
                    </button>
                }
            </div>

            <!-- Status Filter -->
            <div class="flex gap-2 items-center">
                <span class="text-sm text-surface-400 mr-1">Status:</span>
                <button
                    (click)="selectStatus(null)"
                    [class]="selectedStatus() === null ? 'bg-surface-600 text-white' : 'bg-surface-800 text-surface-400 hover:text-white'"
                    class="px-3 py-1 rounded-lg text-xs font-medium transition-colors border border-surface-700"
                >Todos</button>
                <button
                    (click)="selectStatus('ongoing')"
                    [class]="selectedStatus() === 'ongoing' ? 'bg-green-600 border-green-600 text-white' : 'bg-surface-800 text-surface-400 hover:text-white border-surface-700'"
                    class="px-3 py-1 rounded-lg text-xs font-medium transition-colors border"
                >Em Andamento</button>
                <button
                    (click)="selectStatus('completed')"
                    [class]="selectedStatus() === 'completed' ? 'bg-blue-600 border-blue-600 text-white' : 'bg-surface-800 text-surface-400 hover:text-white border-surface-700'"
                    class="px-3 py-1 rounded-lg text-xs font-medium transition-colors border"
                >Completo</button>
                <button
                    (click)="selectStatus('hiatus')"
                    [class]="selectedStatus() === 'hiatus' ? 'bg-orange-600 border-orange-600 text-white' : 'bg-surface-800 text-surface-400 hover:text-white border-surface-700'"
                    class="px-3 py-1 rounded-lg text-xs font-medium transition-colors border"
                >Hiato</button>
            </div>

            <!-- Grid View -->
            @if (viewMode() === 'grid') {
                <div class="grid grid-cols-2 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-4 gap-4">
                    @for (manhwa of pagedManhwas(); track manhwa.id) {
                        <a [routerLink]="['/manhwa', manhwa.id]" class="group cursor-pointer block">
                            <div class="relative rounded-xl overflow-hidden aspect-[2/3] bg-surface-800 mb-2 shadow-lg border border-surface-700 group-hover:border-purple-500 transition-all duration-200">
                                <img
                                    [src]="manhwa.coverUrl"
                                    [alt]="manhwa.title"
                                    class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                                />
                                <!-- Badges -->
                                <div class="absolute top-2 left-2 flex flex-col gap-1">
                                    @if (manhwa.isHot) {
                                        <span class="bg-orange-500 text-white text-xs font-bold px-2 py-0.5 rounded-full leading-none">HOT</span>
                                    }
                                    @if (manhwa.isNew) {
                                        <span class="bg-green-500 text-white text-xs font-bold px-2 py-0.5 rounded-full leading-none">NOVO</span>
                                    }
                                </div>
                                <!-- Hover overlay -->
                                <div class="absolute inset-0 bg-gradient-to-t from-black/90 via-black/20 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-200 flex flex-col justify-end p-3">
                                    <div class="flex items-center gap-1 mb-1">
                                        <i class="pi pi-star-fill text-yellow-400 text-xs"></i>
                                        <span class="text-white text-xs font-semibold">{{ manhwa.rating }}</span>
                                    </div>
                                    <div class="flex gap-1 flex-wrap">
                                        @for (genre of manhwa.genres.slice(0, 2); track genre) {
                                            <span class="text-xs bg-purple-600/80 text-white px-1.5 py-0.5 rounded">{{ genre }}</span>
                                        }
                                    </div>
                                </div>
                                <!-- Status badge bottom right -->
                                <div class="absolute bottom-2 right-2">
                                    <span
                                        [class]="getStatusClass(manhwa.status)"
                                        class="text-xs font-bold px-1.5 py-0.5 rounded"
                                    >{{ getStatusLabel(manhwa.status) }}</span>
                                </div>
                            </div>
                            <h3 class="text-sm font-semibold text-surface-100 group-hover:text-purple-300 transition-colors truncate leading-tight">{{ manhwa.title }}</h3>
                            <p class="text-xs text-surface-500">{{ manhwa.chapters }} capítulos</p>
                        </a>
                    }
                </div>
            }

            <!-- List View -->
            @if (viewMode() === 'list') {
                <div class="flex flex-col gap-3">
                    @for (manhwa of pagedManhwas(); track manhwa.id) {
                        <a [routerLink]="['/manhwa', manhwa.id]" class="group flex items-start gap-4 bg-surface-900 border border-surface-800 hover:border-purple-600 rounded-xl p-4 cursor-pointer transition-all duration-200">
                            <div class="w-16 h-24 rounded-lg overflow-hidden shrink-0 border border-surface-700 shadow-md">
                                <img [src]="manhwa.coverUrl" [alt]="manhwa.title" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-200" />
                            </div>
                            <div class="flex-1 min-w-0">
                                <div class="flex items-start justify-between gap-2 mb-1">
                                    <h3 class="font-bold text-surface-100 group-hover:text-purple-300 transition-colors">{{ manhwa.title }}</h3>
                                    <div class="flex items-center gap-1 shrink-0">
                                        <i class="pi pi-star-fill text-yellow-400 text-xs"></i>
                                        <span class="text-sm font-semibold text-surface-200">{{ manhwa.rating }}</span>
                                    </div>
                                </div>
                                <p class="text-xs text-surface-400 mb-2">Por {{ manhwa.author }}</p>
                                <p class="text-sm text-surface-500 mb-3 line-clamp-2">{{ manhwa.synopsis }}</p>
                                <div class="flex items-center gap-2 flex-wrap">
                                    @for (genre of manhwa.genres; track genre) {
                                        <span class="text-xs bg-surface-800 text-surface-300 px-2 py-0.5 rounded-full border border-surface-700">{{ genre }}</span>
                                    }
                                    <span [class]="getStatusClass(manhwa.status)" class="text-xs font-bold px-2 py-0.5 rounded-full">
                                        {{ getStatusLabel(manhwa.status) }}
                                    </span>
                                </div>
                            </div>
                            <div class="shrink-0 text-right hidden sm:block">
                                <p class="text-lg font-bold text-purple-400">{{ manhwa.chapters }}</p>
                                <p class="text-xs text-surface-500">capítulos</p>
                            </div>
                        </a>
                    }
                </div>
            }

            <!-- Empty State -->
            @if (pagedManhwas().length === 0) {
                <div class="flex flex-col items-center justify-center py-20 text-center">
                    <i class="pi pi-search text-5xl text-surface-600 mb-4"></i>
                    <h3 class="text-lg font-semibold text-surface-300 mb-2">Nenhuma obra encontrada</h3>
                    <p class="text-surface-500 text-sm mb-4">Tente outros termos de busca ou remova os filtros</p>
                    <button pButton label="Limpar Filtros" icon="pi pi-times" severity="secondary" outlined (click)="clearFilters()"></button>
                </div>
            }

            <!-- Pagination -->
            @if (totalResults() > pageSize) {
                <p-paginator
                    [rows]="pageSize"
                    [totalRecords]="totalResults()"
                    [first]="currentPage() * pageSize"
                    (onPageChange)="onPageChange($event)"
                    [rowsPerPageOptions]="[12, 24, 48]"
                    styleClass="border-0 bg-transparent"
                ></p-paginator>
            }
        </div>
    `
})
export class Catalog implements OnInit {
    private manhwaService = inject(ManhwaService);

    allManhwas = signal<Manhwa[]>([]);
    filteredManhwas = signal<Manhwa[]>([]);
    allGenres = signal<string[]>([]);

    searchQuery = '';
    sortBy: SortOption = 'relevance';
    selectedGenre = signal<string | null>(null);
    selectedStatus = signal<ManhwaStatus | null>(null);
    viewMode = signal<ViewMode>('grid');
    currentPage = signal(0);
    pageSize = 12;

    totalResults = computed(() => this.filteredManhwas().length);

    pagedManhwas = computed(() => {
        const start = this.currentPage() * this.pageSize;
        return this.filteredManhwas().slice(start, start + this.pageSize);
    });

    ngOnInit() {
        this.allManhwas.set(this.manhwaService.getAll());
        this.allGenres.set(this.manhwaService.getAllGenres());
        this.applyFilters();
    }

    onSearch() {
        this.currentPage.set(0);
        this.applyFilters();
    }

    onSortChange() {
        this.applyFilters();
    }

    selectGenre(genre: string | null) {
        this.selectedGenre.set(genre);
        this.currentPage.set(0);
        this.applyFilters();
    }

    selectStatus(status: ManhwaStatus | null) {
        this.selectedStatus.set(status);
        this.currentPage.set(0);
        this.applyFilters();
    }

    clearFilters() {
        this.searchQuery = '';
        this.selectedGenre.set(null);
        this.selectedStatus.set(null);
        this.sortBy = 'relevance';
        this.currentPage.set(0);
        this.applyFilters();
    }

    onPageChange(event: PaginatorState) {
        this.currentPage.set(event.page ?? 0);
        this.pageSize = event.rows ?? 12;
    }

    private applyFilters() {
        let result = [...this.allManhwas()];

        if (this.searchQuery.trim()) {
            const q = this.searchQuery.toLowerCase();
            result = result.filter(
                (m) => m.title.toLowerCase().includes(q) || m.author.toLowerCase().includes(q) || m.genres.some((g) => g.toLowerCase().includes(q))
            );
        }

        if (this.selectedGenre()) {
            result = result.filter((m) => m.genres.includes(this.selectedGenre()!));
        }

        if (this.selectedStatus()) {
            result = result.filter((m) => m.status === this.selectedStatus());
        }

        switch (this.sortBy) {
            case 'recent':
                result.sort((a, b) => (b.updatedAt?.getTime() ?? 0) - (a.updatedAt?.getTime() ?? 0));
                break;
            case 'rating':
                result.sort((a, b) => b.rating - a.rating);
                break;
            case 'chapters':
                result.sort((a, b) => b.chapters - a.chapters);
                break;
        }

        this.filteredManhwas.set(result);
    }

    getStatusLabel(status: ManhwaStatus): string {
        const labels: Record<ManhwaStatus, string> = {
            ongoing: 'Em And.',
            completed: 'Completo',
            hiatus: 'Hiato'
        };
        return labels[status];
    }

    getStatusClass(status: ManhwaStatus): string {
        const classes: Record<ManhwaStatus, string> = {
            ongoing: 'bg-green-600/90 text-white',
            completed: 'bg-blue-600/90 text-white',
            hiatus: 'bg-orange-600/90 text-white'
        };
        return classes[status];
    }
}
