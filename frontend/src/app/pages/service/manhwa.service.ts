import {Injectable, signal} from '@angular/core';
import {Manhwa} from '@/app/types/manhwa';

const MOCK_MANHWAS: Manhwa[] = [
    {
        id: '1',
        title: 'Solo Leveling',
        coverUrl: 'https://cdn.mangotoons.com/obras/10759/cover.webp',
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
        coverUrl: 'https://cdn.yomu.com.br/obras/Omniscient-Reader\'s-Viewpoint-novel/capa/aa1a2f378e493448.webp',
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
        coverUrl: 'https://mediocrescan.com/_next/image?url=https%3A%2F%2Fapi.mediocrescan.com%2Fstorage%2Fobras%2F220%2F1ff98ed7ee079a0693bfba7ed814629f9d751c44.webp%3Fw%3D600&w=1200&q=75',
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
        coverUrl: 'https://cdn.yomu.com.br/obras/o-regressor-da-familia-caida/capa/cover-1767023299359-rp60lv.png',
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
        coverUrl: 'https://cdn.monstercomics.com.br/obras/o-mago-negro-eu-sou-o-unico-mago-transcendental-que-retornou-com-habilidades-injustas/e8eaeb3f1a931f44615e1ab9c47cbabb5a616e60.webp',
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
        coverUrl: 'https://cdn.monstercomics.com.br/obras/a-historia-de-um-soldado-de-baixa-patente-que-se-torna-um-monarca/6b7508c7daf797e0f34f0b42561d3a02b6ab8c83.webp',
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
        coverUrl: 'https://cdn.monstercomics.com.br/obras/restaurante-da-vida-apos-a-morte/f214c898ff7796a32250be52d6ecc6950aedc01a.webp',
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
        coverUrl: 'https://cdn.monstercomics.com.br/obras/o-homem-implacavel-do-juizo-final-acumulando-trilhoes-de-suprimentos-no-inicio/ec8d9ee0481a3334b8688b6540d7c09b6e8fb3ea.webp',
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
        coverUrl: 'https://cdn.yomu.com.br/obras/o-filho-cacula-do-conde-e-um-jogador/capa/cover-1773233418863-62ndsf.png',
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
        coverUrl: 'https://cdn.yomu.com.br/obras/o-recluso-supremo/capa/cover-1764174806623-5nwq16.png',
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
        coverUrl: 'https://cdn.monstercomics.com.br/obras/os-filmes-sao-reais/cover.png',
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
        coverUrl: 'https://cdn.monstercomics.com.br/obras/o-heroi-de-nivel-maximo-retornou/29219e08a8c73f74dcd2294cd24fb3070f1f1520.webp',
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
