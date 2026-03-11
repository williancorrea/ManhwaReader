CREATE TABLE site (
                      id CHAR(36) NOT NULL PRIMARY KEY,
                      code VARCHAR(50) NOT NULL UNIQUE,
                      url VARCHAR(255) NOT NULL
);

INSERT INTO site (id, code, url) VALUES (UUID(), 'MANGADEX', 'https://mangadex.org/title');
INSERT INTO site (id, code, url) VALUES (UUID(), 'MEDIOCRESCAN', 'https://mediocrescan.com/obra');
INSERT INTO site (id, code, url) VALUES (UUID(), 'WEB_TOONS', 'https://www.webtoons.com');
INSERT INTO site (id, code, url) VALUES (UUID(), 'ANI_LIST', 'https://anilist.co/manga');
INSERT INTO site (id, code, url) VALUES (UUID(), 'ANIME_PLANET', 'https://www.anime-planet.com/manga');
INSERT INTO site (id, code, url) VALUES (UUID(), 'NOVEL_UPDATES', 'https://www.novelupdates.com/series');
INSERT INTO site (id, code, url) VALUES (UUID(), 'MY_ANIME_LIST', 'https://myanimelist.net/manga');
INSERT INTO site (id, code, url) VALUES (UUID(), 'MANGA_UPDATES', 'https://www.mangaupdates.com/series');
INSERT INTO site (id, code, url) VALUES (UUID(), 'KITSU', 'https://kitsu.app/manga');
