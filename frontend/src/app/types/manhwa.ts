export type ManhwaStatus = 'ongoing' | 'completed' | 'hiatus';

export interface Manhwa {
    id: string;
    title: string;
    coverUrl: string;
    genres: string[];
    status: ManhwaStatus;
    chapters: number;
    rating: number;
    synopsis: string;
    author: string;
    isNew?: boolean;
    isHot?: boolean;
    views?: number;
    updatedAt?: Date;
}
