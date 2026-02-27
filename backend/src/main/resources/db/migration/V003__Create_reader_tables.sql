-- Núcleo das Obras
CREATE TABLE publisher (
    id CHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE work (
    id CHAR(36) NOT NULL PRIMARY KEY,
    disabled BOOLEAN DEFAULT FALSE,
    chapter_numbers_reset_on_new_volume BOOLEAN DEFAULT FALSE,
    type VARCHAR(20) NOT NULL,
    publication_demographic VARCHAR(30) NOT NULL,
    release_year INT,
    status VARCHAR(20)NOT NULL,
    content_rating VARCHAR(20),
    slug VARCHAR(200) NOT NULL UNIQUE,
    cover_high VARCHAR(100),
    cover_medium VARCHAR(100),
    cover_low VARCHAR(100),
    cover_custom VARCHAR(100),
    publisher_id CHAR(36),
    original_language_id CHAR(36),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (publisher_id) REFERENCES publisher(id),
    FOREIGN KEY (original_language_id) REFERENCES language(id)
);

CREATE TABLE work_synchronization (
    id CHAR(36) NOT NULL PRIMARY KEY,
    work_id CHAR(36) NOT NULL,
    origin VARCHAR(50) NOT NULL,
    external_id VARCHAR(255) NOT NULL UNIQUE,
    external_slug VARCHAR(255),
    FOREIGN KEY (work_id) REFERENCES work(id)
);

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

CREATE TABLE work_link (
    id CHAR(36) NOT NULL PRIMARY KEY,
    work_id CHAR(36) NOT NULL,
    site_id CHAR(36) NOT NULL,
    code VARCHAR(50) NOT NULL,
    link TEXT NOT NULL,
    FOREIGN KEY (work_id) REFERENCES work(id),
    FOREIGN KEY (site_id) REFERENCES site(id)
);

CREATE TABLE work_synopsis(
    id CHAR(36) NOT NULL PRIMARY KEY,
    work_id CHAR(36) NOT NULL,
    language_id CHAR(36) NOT NULL,
    description TEXT NOT NULL,
    FOREIGN KEY (work_id) REFERENCES work(id),
    FOREIGN KEY (language_id) REFERENCES language(id)
);

CREATE TABLE work_title (
    id CHAR(36) NOT NULL PRIMARY KEY,
    work_id CHAR(36) NOT NULL,
    language_id CHAR(36) NOT NULL,
    title TEXT NOT NULL,
    is_official BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (work_id) REFERENCES work(id),
    FOREIGN KEY (language_id) REFERENCES language(id)
);

-- Tags
CREATE TABLE tag(
    id        CHAR(36)     NOT NULL PRIMARY KEY,
    group_tag VARCHAR(50)  NOT NULL,
    name      VARCHAR(100),
    alias1    VARCHAR(100),
    alias2    VARCHAR(100),
    alias3    VARCHAR(100)
);

INSERT INTO tag (id, group_tag, name, alias1, alias2, alias3) values (UUID(), 'GENRE', 'Action','Ação',null, null);
INSERT INTO tag (id, group_tag, name, alias1, alias2, alias3) values (UUID(), 'GENRE', 'Adventure','Aventura',null, null);
INSERT INTO tag (id, group_tag, name, alias1, alias2, alias3) values (UUID(), 'GENRE', 'Fantasy','Fantasia',null, null);
INSERT INTO tag (id, group_tag, name, alias1, alias2, alias3) values (UUID(), 'GENRE', null,'Drama', null, null);
INSERT INTO tag (id, group_tag, name, alias1, alias2, alias3) values (UUID(), 'GENRE', 'Reincarnation','Reencarnação',null, null);
INSERT INTO tag (id, group_tag, name, alias1, alias2, alias3) values (UUID(), 'GENRE', null,'Horror',null, null);
INSERT INTO tag (id, group_tag, name, alias1, alias2, alias3) values (UUID(), 'GENRE', 'Monsters','Monstros',null, null);
INSERT INTO tag (id, group_tag, name, alias1, alias2, alias3) values (UUID(), 'GENRE', 'Mystery','Mistério',null, null);
INSERT INTO tag (id, group_tag, name, alias1, alias2, alias3) values (UUID(), 'GENRE', null,'Shounen',null, null);
INSERT INTO tag (id, group_tag, name, alias1, alias2, alias3) values (UUID(), 'GENRE', null,'Shoujo',null, null);

CREATE TABLE work_tag(
    id      CHAR(36)     NOT NULL PRIMARY KEY,
    work_id CHAR(36) NOT NULL,
    tag_id  CHAR(36) NOT NULL,
    FOREIGN KEY (work_id) REFERENCES work (id),
    FOREIGN KEY (tag_id) REFERENCES tag (id)
);


CREATE TABLE author
(
    id         CHAR(36)     NOT NULL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    type       VARCHAR(255) NOT NULL,
    biography  TEXT,
    twitter    VARCHAR(255),
    pixiv      VARCHAR(255),
    melon_book VARCHAR(255),
    fan_box    VARCHAR(255),
    booth      VARCHAR(255),
    namicomi   VARCHAR(255),
    nico_video VARCHAR(255),
    skeb       VARCHAR(255),
    fantia     VARCHAR(255),
    tumblr     VARCHAR(255),
    youtube    VARCHAR(255),
    weibo      VARCHAR(255),
    naver      VARCHAR(255),
    website    VARCHAR(255)
);

CREATE TABLE work_author
(
    id        CHAR(36) NOT NULL PRIMARY KEY,
    work_id   CHAR(36) NOT NULL,
    author_id CHAR(36) NOT NULL,
    FOREIGN KEY (work_id) REFERENCES work (id),
    FOREIGN KEY (author_id) REFERENCES author (id)
);








-- Scanlators
CREATE TABLE scanlator (
    id CHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,    
    code VARCHAR(20) NOT NULL UNIQUE,
    website VARCHAR(255),
    synchronization VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO scanlator (id, name, code, website, synchronization) VALUES (UUID(), 'MangaDex', 'MD', 'https://mangadex.org', 'MANGADEX');
INSERT INTO scanlator (id, name, code, website, synchronization) VALUES (UUID(), 'Lycantoons', 'LT', 'https://lycantoons.com', 'LYCANTOONS');
INSERT INTO scanlator (id, name, code, website, synchronization) VALUES (UUID(), 'Mangotoons', 'MT', 'https://mangotoons.com/', 'MANGOTOONS');
INSERT INTO scanlator (id, name, code, website, synchronization) VALUES (UUID(), 'Mediocrescan', 'MS', 'https://mediocrescan.com//', 'MEDIOCRESCAN');

-- Estrutura de Capítulos
CREATE TABLE volume (
    id CHAR(36) NOT NULL PRIMARY KEY,
    work_id CHAR(36) NOT NULL,
    number INT NOT NULL,
    title VARCHAR(255),
    FOREIGN KEY (work_id) REFERENCES work(id)
);

CREATE TABLE chapter (
    id CHAR(36) NOT NULL PRIMARY KEY,
    work_id CHAR(36) NOT NULL,
    volume_id CHAR(36),
    number DECIMAL(10, 2) NOT NULL,
    title VARCHAR(255),
    language_id CHAR(36) NOT NULL,
    release_date DATE,
    scanlator_id CHAR(36),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (work_id) REFERENCES work(id),
    FOREIGN KEY (volume_id) REFERENCES volume(id),
    FOREIGN KEY (scanlator_id) REFERENCES scanlator(id),
    FOREIGN KEY (language_id) REFERENCES language(id)
);

CREATE TABLE page (
    id CHAR(36) NOT NULL PRIMARY KEY,
    chapter_id CHAR(36) NOT NULL,
    page_number INT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    disabled BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (chapter_id) REFERENCES chapter(id)
);

CREATE TABLE chapter_notify (
    id CHAR(36) NOT NULL PRIMARY KEY,
    chapter_id CHAR(36) NOT NULL,
    notify BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (chapter_id) REFERENCES chapter(id)
);



-- Usuários e Funcionalidades
CREATE TABLE user (
    id CHAR(36) NOT NULL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE library (
    id CHAR(36) NOT NULL PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    work_id CHAR(36) NOT NULL,
    status ENUM('READING', 'COMPLETED', 'PLAN_TO_READ', 'DROPPED') NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (work_id) REFERENCES work(id),
    UNIQUE (user_id, work_id)
);

CREATE TABLE reading_progress (
    id CHAR(36) NOT NULL PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    chapter_id CHAR(36) NOT NULL,
    page_number INT,
    last_read_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (chapter_id) REFERENCES chapter(id),
    UNIQUE (user_id, chapter_id)
);

CREATE TABLE rating (
    id CHAR(36) NOT NULL PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    work_id CHAR(36) NOT NULL,
    score TINYINT NOT NULL CHECK (score BETWEEN 1 AND 10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (work_id) REFERENCES work(id),
    UNIQUE (user_id, work_id)
);

CREATE TABLE comment (
    id CHAR(36) NOT NULL PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    work_id CHAR(36),
    chapter_id CHAR(36),
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (work_id) REFERENCES work(id),
    FOREIGN KEY (chapter_id) REFERENCES chapter(id)
);
