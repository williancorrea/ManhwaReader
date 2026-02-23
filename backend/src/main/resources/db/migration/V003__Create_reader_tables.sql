-- Arquivos e Imagens (Base para outros domínios)
CREATE TABLE file (
    id CHAR(36) NOT NULL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    storage_path VARCHAR(512) NOT NULL,
    mime_type VARCHAR(100),
    size_bytes BIGINT,
    checksum VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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
    publication_demographic VARCHAR(30),
    release_year INT,
    status VARCHAR(20)NOT NULL,
    content_rating VARCHAR(20)NOT NULL,
    cover_image_id CHAR(36),
    publisher_id CHAR(36),
    original_language_id CHAR(36),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cover_image_id) REFERENCES file(id),
    FOREIGN KEY (publisher_id) REFERENCES publisher(id),
    FOREIGN KEY (original_language_id) REFERENCES language(id)
);

CREATE TABLE work_synchronization (
    id CHAR(36) NOT NULL PRIMARY KEY,
    work_id CHAR(36) NOT NULL,
    origin VARCHAR(50) NOT NULL,
    external_id VARCHAR(255) NOT NULL UNIQUE,
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
    link VARCHAR(50) NOT NULL,
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
    title VARCHAR(255) NOT NULL,
    is_official BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (work_id) REFERENCES work(id),
    FOREIGN KEY (language_id) REFERENCES language(id)
);

-- Tags
CREATE TABLE tag(
    id        CHAR(36)     NOT NULL PRIMARY KEY,
    group_tag VARCHAR(50)  NOT NULL,
    name      VARCHAR(100) NOT NULL,
    alias1    VARCHAR(100),
    alias2    VARCHAR(100)
);

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
    fan_box     VARCHAR(255),
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
    website VARCHAR(255)
);

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
    image_file_id CHAR(36) NOT NULL,
    FOREIGN KEY (chapter_id) REFERENCES chapter(id),
    FOREIGN KEY (image_file_id) REFERENCES file(id)
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
