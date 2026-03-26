import { Injectable, signal } from '@angular/core';
import { Manhwa, ManhwaStatus } from '@/app/types/manhwa';

const MOCK_MANHWAS: Manhwa[] = [
    {
        id: '1',
        title: 'Solo Leveling',
        coverUrl: 'https://placehold.co/200x280/1a1a2e/e94560?text=Solo+Leveling',
        genres: ['Ação', 'Fantasia', 'Aventura'],
        status: 'completed',
        chapters: 179,
        rating: 9.8,
        synopsis: 'Em um mundo onde caçadores com poderes sobrenaturais devem combater monstros mortais, Sung Jin-Woo é considerado o caçador mais fraco. Após um dungeon devastador, ele descobre um sistema misterioso que só ele pode ver.',
        author: 'Chugong',
        isHot: true,
        views: 150000,
        updatedAt: new Date('2024-12-01')
    },
    {
        id: '2',
        title: 'Omniscient Reader',
        coverUrl: 'https://placehold.co/200x280/16213e/0f3460?text=Omniscient+Reader',
        genres: ['Ação', 'Fantasia', 'Drama'],
        status: 'completed',
        chapters: 551,
        rating: 9.5,
        synopsis: 'Kim Dokja é o único leitor de uma web novel obscura. Quando o mundo começa a seguir os eventos da história que ele leu, ele é o único que sabe como tudo vai acabar.',
        author: 'Sing Shong',
        isHot: true,
        views: 120000,
        updatedAt: new Date('2024-11-15')
    },
    {
        id: '3',
        title: 'The Beginning After the End',
        coverUrl: 'https://placehold.co/200x280/0f3460/533483?text=TBATE',
        genres: ['Ação', 'Fantasia', 'Reencarnação'],
        status: 'ongoing',
        chapters: 187,
        rating: 9.2,
        synopsis: 'O Rei Grey, abençoado com força, habilidades e riqueza, reina supremo em um mundo dominado pela política e pelo poder. No entanto, por trás dessa gloriosa fachada, a solidão governa.',
        author: 'TurtleMe',
        isNew: true,
        views: 98000,
        updatedAt: new Date('2025-01-10')
    },
    {
        id: '4',
        title: 'Tower of God',
        coverUrl: 'https://placehold.co/200x280/2d132c/ee4540?text=Tower+of+God',
        genres: ['Ação', 'Aventura', 'Mistério'],
        status: 'ongoing',
        chapters: 620,
        rating: 9.0,
        synopsis: 'Bam, um menino que cresceu sozinho nas trevas abaixo de uma Torre misteriosa, sobe nela para encontrar sua única amiga Rachel.',
        author: 'SIU',
        views: 200000,
        updatedAt: new Date('2025-01-05')
    },
    {
        id: '5',
        title: 'Nano Machine',
        coverUrl: 'https://placehold.co/200x280/1b1b2f/f5a623?text=Nano+Machine',
        genres: ['Ação', 'Artes Marciais', 'Sci-Fi'],
        status: 'ongoing',
        chapters: 171,
        rating: 8.8,
        synopsis: 'Um descendente bastardo de um clã de artes marciais recebe uma nano máquina do futuro, transformando-o em um guerreiro invencível.',
        author: 'Geumgang Bulgoe',
        isNew: true,
        views: 85000,
        updatedAt: new Date('2025-01-08')
    },
    {
        id: '6',
        title: 'Eleceed',
        coverUrl: 'https://placehold.co/200x280/162447/e84545?text=Eleceed',
        genres: ['Ação', 'Comédia', 'Super Poderes'],
        status: 'ongoing',
        chapters: 278,
        rating: 8.9,
        synopsis: 'Jiwoo, um jovem de bom coração com poderes de velocidade inacreditável, se envolve com o misterioso Kayden, um agente de elite escondido no corpo de um gato gordo.',
        author: 'Son Jae Ho',
        views: 75000,
        updatedAt: new Date('2024-12-20')
    },
    {
        id: '7',
        title: 'Reincarnator',
        coverUrl: 'https://placehold.co/200x280/0d0d0d/c0392b?text=Reincarnator',
        genres: ['Ação', 'Fantasia', 'Reencarnação'],
        status: 'completed',
        chapters: 310,
        rating: 8.5,
        synopsis: 'Hansung Oh é mandado de volta ao passado para salvar a humanidade de uma calamidade que destruiu toda a civilização.',
        author: 'Noobcook',
        views: 65000,
        updatedAt: new Date('2024-09-01')
    },
    {
        id: '8',
        title: 'Legend of the Northern Blade',
        coverUrl: 'https://placehold.co/200x280/1a1a2e/2980b9?text=Northern+Blade',
        genres: ['Ação', 'Artes Marciais', 'Aventura'],
        status: 'ongoing',
        chapters: 170,
        rating: 8.7,
        synopsis: 'Após o colapso do Vento do Norte, o filho do fundador Jin Kwan-Ho começa sua jornada para restaurar a ordem da organização.',
        author: 'Hae Min',
        views: 70000,
        updatedAt: new Date('2024-12-28')
    },
    {
        id: '9',
        title: 'Volcanic Age',
        coverUrl: 'https://placehold.co/200x280/2c1654/a855f7?text=Volcanic+Age',
        genres: ['Ação', 'Artes Marciais', 'Romance'],
        status: 'completed',
        chapters: 268,
        rating: 8.4,
        synopsis: 'Joo Seo-Cheon sobreviveu à calamidade que assolou o mundo das artes marciais. Em seu leito de morte, ele retorna ao passado para mudar o destino.',
        author: 'Hyun Gun',
        views: 60000,
        updatedAt: new Date('2024-08-15')
    },
    {
        id: '10',
        title: 'Second Life Ranker',
        coverUrl: 'https://placehold.co/200x280/0a3d62/1e90ff?text=Second+Life',
        genres: ['Ação', 'Fantasia', 'Vingança'],
        status: 'ongoing',
        chapters: 153,
        rating: 8.6,
        synopsis: 'Yeon-woo tinha um irmão gêmeo que desapareceu. Cinco anos depois, ele descobre um diário escondido no relógio do irmão revelando os mistérios de uma Torre.',
        author: 'Sadoyeon',
        isNew: true,
        views: 80000,
        updatedAt: new Date('2025-01-12')
    },
    {
        id: '11',
        title: 'Return of the Mount Hua Sect',
        coverUrl: 'https://placehold.co/200x280/1a3a00/4caf50?text=Mount+Hua',
        genres: ['Ação', 'Artes Marciais', 'Comédia'],
        status: 'ongoing',
        chapters: 144,
        rating: 9.1,
        synopsis: 'O discípulo mais forte da seita Mount Hua, Chung Myung, morre após derrotar o Demônio Supremo. Ele renasceu 100 anos depois como o discípulo mais fraco da seita.',
        author: 'Biga',
        isHot: true,
        views: 110000,
        updatedAt: new Date('2025-01-14')
    },
    {
        id: '12',
        title: 'Murim Login',
        coverUrl: 'https://placehold.co/200x280/2d1b00/ff8c00?text=Murim+Login',
        genres: ['Ação', 'Artes Marciais', 'Isekai'],
        status: 'ongoing',
        chapters: 190,
        rating: 8.8,
        synopsis: 'Jin Tae-Kyung, um caçador de rank D, torna-se imerso num jogo de realidade virtual de artes marciais. Ao sair do jogo, suas habilidades são transferidas para o mundo real.',
        author: 'Ahn Hyeon-Jun',
        views: 88000,
        updatedAt: new Date('2025-01-03')
    }
];

@Injectable({
    providedIn: 'root'
})
export class ManhwaService {
    private manhwas = signal<Manhwa[]>(MOCK_MANHWAS);

    getAll() {
        return this.manhwas();
    }

    getById(id: string) {
        return this.manhwas().find((m) => m.id === id);
    }

    getFeatured() {
        return this.manhwas()
            .filter((m) => m.isHot)
            .slice(0, 6);
    }

    getRecent() {
        return [...this.manhwas()]
            .sort((a, b) => (b.updatedAt?.getTime() ?? 0) - (a.updatedAt?.getTime() ?? 0))
            .slice(0, 6);
    }

    getByGenre(genre: string) {
        if (!genre) return this.manhwas();
        return this.manhwas().filter((m) => m.genres.includes(genre));
    }

    search(query: string) {
        const q = query.toLowerCase();
        return this.manhwas().filter((m) => m.title.toLowerCase().includes(q) || m.author.toLowerCase().includes(q) || m.genres.some((g) => g.toLowerCase().includes(q)));
    }

    getAllGenres(): string[] {
        const genres = new Set<string>();
        this.manhwas().forEach((m) => m.genres.forEach((g) => genres.add(g)));
        return Array.from(genres).sort();
    }
}
