import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { RatingModule } from 'primeng/rating';
import { FormsModule } from '@angular/forms';
import { Manhwa } from '@/app/types/manhwa';
import { ManhwaService } from '@/app/pages/service/manhwa.service';

@Component({
    selector: 'app-home',
    standalone: true,
    imports: [CommonModule, RouterModule, ButtonModule, TagModule, RatingModule, FormsModule],
    template: `
        <!-- Hero Banner -->
        <div class="relative overflow-hidden rounded-2xl mb-8 bg-gradient-to-r from-slate-900 via-purple-950 to-slate-900 border border-surface-800">
            <div class="absolute inset-0 opacity-20 bg-[radial-gradient(ellipse_at_top_right,_var(--tw-gradient-stops))] from-purple-500 via-transparent to-transparent"></div>
            <div class="relative px-8 py-12 flex items-center gap-8">
                <div class="flex-1 min-w-0">
                    <p class="text-sm font-semibold text-purple-400 uppercase tracking-widest mb-2">Bem-vindo ao</p>
                    <h1 class="text-4xl font-bold text-white mb-4 leading-tight">
                        Manhwa Reader
                    </h1>
                    <p class="text-surface-400 text-lg mb-6 max-w-lg">
                        Leia os melhores manhwas, mangas e webtoons em um só lugar. Milhares de obras atualizadas diariamente.
                    </p>
                    <div class="flex gap-3 flex-wrap">
                        <button pButton routerLink="/catalog" label="Explorar Obras" icon="pi pi-compass" class="bg-purple-600 border-purple-600 hover:bg-purple-700"></button>
                        <button pButton routerLink="/catalog" label="Mais Populares" icon="pi pi-star" severity="secondary" outlined></button>
                    </div>
                </div>
                <div class="hidden lg:flex gap-3 shrink-0">
                    @for (manhwa of heroCovers(); track manhwa.id; let i = $index) {
                        <div
                            class="rounded-xl overflow-hidden border border-white/10 shadow-xl"
                            [class]="i === 1 ? 'w-32 h-48' : 'w-28 h-40 mt-4'"
                        >
                            <img [src]="manhwa.coverUrl" [alt]="manhwa.title" class="w-full h-full object-cover" />
                        </div>
                    }
                </div>
            </div>
        </div>

        <!-- Stats Bar -->
        <div class="grid grid-cols-3 gap-4 mb-8">
            <div class="bg-surface-900 border border-surface-800 rounded-xl p-4 flex items-center gap-3">
                <div class="w-10 h-10 rounded-lg bg-purple-500/20 flex items-center justify-center">
                    <i class="pi pi-book text-purple-400 text-lg"></i>
                </div>
                <div>
                    <p class="text-2xl font-bold text-white">12+</p>
                    <p class="text-xs text-surface-400">Obras Disponíveis</p>
                </div>
            </div>
            <div class="bg-surface-900 border border-surface-800 rounded-xl p-4 flex items-center gap-3">
                <div class="w-10 h-10 rounded-lg bg-blue-500/20 flex items-center justify-center">
                    <i class="pi pi-refresh text-blue-400 text-lg"></i>
                </div>
                <div>
                    <p class="text-2xl font-bold text-white">Diário</p>
                    <p class="text-xs text-surface-400">Atualizações</p>
                </div>
            </div>
            <div class="bg-surface-900 border border-surface-800 rounded-xl p-4 flex items-center gap-3">
                <div class="w-10 h-10 rounded-lg bg-green-500/20 flex items-center justify-center">
                    <i class="pi pi-tag text-green-400 text-lg"></i>
                </div>
                <div>
                    <p class="text-2xl font-bold text-white">10+</p>
                    <p class="text-xs text-surface-400">Gêneros</p>
                </div>
            </div>
        </div>

        <!-- Em Alta -->
        <section class="mb-8">
            <div class="flex items-center justify-between mb-4">
                <div class="flex items-center gap-2">
                    <i class="pi pi-fire text-orange-400 text-xl"></i>
                    <h2 class="text-xl font-bold text-white">Em Alta</h2>
                </div>
                <button pButton routerLink="/catalog" label="Ver Todos" icon="pi pi-arrow-right" iconPos="right" text size="small" class="text-purple-400"></button>
            </div>
            <div class="grid grid-cols-2 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
                @for (manhwa of featuredManhwas(); track manhwa.id) {
                    <a [routerLink]="['/manhwa', manhwa.id]" class="group cursor-pointer block">
                        <div class="relative rounded-xl overflow-hidden aspect-[2/3] bg-surface-800 mb-2 shadow-lg border border-surface-700 group-hover:border-purple-500 transition-all duration-200">
                            <img [src]="manhwa.coverUrl" [alt]="manhwa.title" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" />
                            <div class="absolute inset-0 bg-gradient-to-t from-black/80 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-200"></div>
                            @if (manhwa.isHot) {
                                <span class="absolute top-2 left-2 bg-orange-500 text-white text-xs font-bold px-2 py-0.5 rounded-full">HOT</span>
                            }
                            @if (manhwa.isNew) {
                                <span class="absolute top-2 left-2 bg-green-500 text-white text-xs font-bold px-2 py-0.5 rounded-full">NOVO</span>
                            }
                            <div class="absolute bottom-0 left-0 right-0 p-3 translate-y-full group-hover:translate-y-0 transition-transform duration-200">
                                <p-tag [value]="manhwa.status === 'ongoing' ? 'Em Andamento' : manhwa.status === 'completed' ? 'Completo' : 'Hiato'"
                                    [severity]="manhwa.status === 'ongoing' ? 'success' : manhwa.status === 'completed' ? 'info' : 'warn'"
                                    class="text-xs"></p-tag>
                            </div>
                        </div>
                        <h3 class="text-sm font-semibold text-surface-100 group-hover:text-purple-300 transition-colors truncate">{{ manhwa.title }}</h3>
                        <p class="text-xs text-surface-400">{{ manhwa.chapters }} caps</p>
                    </a>
                }
            </div>
        </section>

        <!-- Atualizados Recentemente -->
        <section>
            <div class="flex items-center justify-between mb-4">
                <div class="flex items-center gap-2">
                    <i class="pi pi-clock text-blue-400 text-xl"></i>
                    <h2 class="text-xl font-bold text-white">Atualizados Recentemente</h2>
                </div>
                <button pButton routerLink="/catalog" label="Ver Todos" icon="pi pi-arrow-right" iconPos="right" text size="small" class="text-purple-400"></button>
            </div>
            <div class="flex flex-col gap-3">
                @for (manhwa of recentManhwas(); track manhwa.id) {
                    <a [routerLink]="['/manhwa', manhwa.id]" class="group flex items-center gap-4 bg-surface-900 border border-surface-800 hover:border-purple-700 rounded-xl p-3 cursor-pointer transition-all duration-200">
                        <div class="w-14 h-20 rounded-lg overflow-hidden shrink-0 border border-surface-700">
                            <img [src]="manhwa.coverUrl" [alt]="manhwa.title" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-200" />
                        </div>
                        <div class="flex-1 min-w-0">
                            <h3 class="font-semibold text-surface-100 group-hover:text-purple-300 transition-colors truncate">{{ manhwa.title }}</h3>
                            <p class="text-xs text-surface-400 mb-1">{{ manhwa.author }}</p>
                            <div class="flex gap-1 flex-wrap">
                                @for (genre of manhwa.genres.slice(0, 2); track genre) {
                                    <span class="text-xs bg-surface-800 text-surface-300 px-2 py-0.5 rounded-full border border-surface-700">{{ genre }}</span>
                                }
                            </div>
                        </div>
                        <div class="shrink-0 text-right">
                            <p class="text-sm font-semibold text-purple-400">Cap. {{ manhwa.chapters }}</p>
                            <p class="text-xs text-surface-500">{{ manhwa.updatedAt | date:'dd/MM' }}</p>
                        </div>
                    </a>
                }
            </div>
        </section>
    `
})
export class Home implements OnInit {
    private manhwaService = inject(ManhwaService);

    featuredManhwas = signal<Manhwa[]>([]);
    recentManhwas = signal<Manhwa[]>([]);
    heroCovers = signal<Manhwa[]>([]);

    ngOnInit() {
        this.featuredManhwas.set(this.manhwaService.getFeatured());
        this.recentManhwas.set(this.manhwaService.getRecent());
        this.heroCovers.set(this.manhwaService.getFeatured().slice(0, 3));
    }
}
