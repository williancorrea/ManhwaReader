import { Component } from '@angular/core';
import { NavbarComponent } from '../../shared/components/navbar/navbar';
import { ManhwaCardComponent, Manhwa } from '../../shared/components/manhwa-card/manhwa-card';

@Component({
  selector: 'app-home',
  imports: [NavbarComponent, ManhwaCardComponent],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class HomeComponent {
  continueReading: Manhwa[] = [
    { id: 1,  title: 'Solo Leveling',             coverUrl: 'https://picsum.photos/seed/manhwa1/200/300',  latestChapter: 179, genres: ['Action', 'Fantasy'],            progress: 65 },
    { id: 2,  title: 'Tower of God',               coverUrl: 'https://picsum.photos/seed/manhwa2/200/300',  latestChapter: 598, genres: ['Fantasy', 'Mystery'],           progress: 42 },
    { id: 3,  title: 'The Beginning After End',    coverUrl: 'https://picsum.photos/seed/manhwa3/200/300',  latestChapter: 190, genres: ['Fantasy', 'Isekai', 'Action'],   progress: 78 },
    { id: 4,  title: 'Omniscient Reader',          coverUrl: 'https://picsum.photos/seed/manhwa4/200/300',  latestChapter: 147, genres: ['Action', 'Drama'],              progress: 30 },
    { id: 5,  title: 'Windbreaker',                coverUrl: 'https://picsum.photos/seed/manhwa5/200/300',  latestChapter: 464, genres: ['Sports', 'Drama'],              progress: 55 },
    { id: 6,  title: 'Nano Machine',               coverUrl: 'https://picsum.photos/seed/manhwa6/200/300',  latestChapter: 183, genres: ['Martial Arts', 'Sci-Fi'],        progress: 20 },
  ];

  latestUpdates: Manhwa[] = [
    { id: 7,  title: 'Eleceed',                    coverUrl: 'https://picsum.photos/seed/manhwa7/200/300',  latestChapter: 296, genres: ['Action', 'Comedy', 'School'],    isNew: true  },
    { id: 8,  title: 'Lookism',                    coverUrl: 'https://picsum.photos/seed/manhwa8/200/300',  latestChapter: 489, genres: ['Drama', 'Action', 'School'],     isNew: true  },
    { id: 9,  title: 'I Love Yoo',                 coverUrl: 'https://picsum.photos/seed/manhwa9/200/300',  latestChapter: 277, genres: ['Romance', 'Drama'],              isNew: false },
    { id: 10, title: 'True Beauty',                coverUrl: 'https://picsum.photos/seed/manhwa10/200/300', latestChapter: 216, genres: ['Romance', 'Comedy', 'School'],    isNew: true  },
    { id: 11, title: 'The God of High School',     coverUrl: 'https://picsum.photos/seed/manhwa11/200/300', latestChapter: 567, genres: ['Martial Arts', 'Action'],         isNew: false },
    { id: 12, title: 'Sweet Home',                 coverUrl: 'https://picsum.photos/seed/manhwa12/200/300', latestChapter: 140, genres: ['Horror', 'Drama', 'Mystery'],     isNew: false },
    { id: 13, title: 'Hardcore Leveling Warrior',  coverUrl: 'https://picsum.photos/seed/manhwa13/200/300', latestChapter: 330, genres: ['Action', 'Fantasy', 'Comedy'],    isNew: true  },
    { id: 14, title: 'Noblesse',                   coverUrl: 'https://picsum.photos/seed/manhwa14/200/300', latestChapter: 544, genres: ['Action', 'Fantasy'],              isNew: false },
    { id: 15, title: 'Viral Hit',                  coverUrl: 'https://picsum.photos/seed/manhwa15/200/300', latestChapter: 112, genres: ['Action', 'Comedy', 'Sports'],     isNew: true  },
    { id: 16, title: 'Jungle Juice',               coverUrl: 'https://picsum.photos/seed/manhwa16/200/300', latestChapter: 121, genres: ['Action', 'Fantasy', 'School'],    isNew: false },
    { id: 17, title: 'Currency Exchange',          coverUrl: 'https://picsum.photos/seed/manhwa17/200/300', latestChapter: 88,  genres: ['Romance', 'Comedy'],              isNew: true  },
    { id: 18, title: "A Returner's Magic",         coverUrl: 'https://picsum.photos/seed/manhwa18/200/300', latestChapter: 231, genres: ['Fantasy', 'Action', 'Drama'],     isNew: false },
  ];
}
