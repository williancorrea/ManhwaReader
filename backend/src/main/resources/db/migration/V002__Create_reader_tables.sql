-- Arquivos e Imagens (Base para outros domínios)
CREATE TABLE file (
    id CHAR(36) NOT NULL PRIMARY KEY,
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

CREATE TABLE language (
    id CHAR(36) NOT NULL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE work (
    id CHAR(36) NOT NULL PRIMARY KEY,
    original_title VARCHAR(255) NOT NULL,
    synopsis TEXT,
    type ENUM('MANGA', 'MANHWA', 'MANHUA', 'NOVEL') NOT NULL,
    status ENUM('ONGOING', 'COMPLETED', 'HIATUS', 'CANCELLED') NOT NULL,
    release_year INT,
    cover_image_id CHAR(36),
    publisher_id CHAR(36),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cover_image_id) REFERENCES file(id),
    FOREIGN KEY (publisher_id) REFERENCES publisher(id)
);

CREATE TABLE work_title (
    id CHAR(36) NOT NULL PRIMARY KEY,
    work_id CHAR(36) NOT NULL,
    language_id CHAR(36) NOT NULL,
    title VARCHAR(255) NOT NULL,
    is_official BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (work_id) REFERENCES work(id),
    FOREIGN KEY (language_id) REFERENCES language(id),
    UNIQUE (work_id, language_id)
);

CREATE TABLE alternative_title (
    id CHAR(36) NOT NULL PRIMARY KEY,
    work_id CHAR(36) NOT NULL,
    language_id CHAR(36),
    title VARCHAR(255) NOT NULL,
    FOREIGN KEY (work_id) REFERENCES work(id),
    FOREIGN KEY (language_id) REFERENCES language(id)
);

CREATE TABLE genre (
    id CHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE work_genre (
    work_id CHAR(36) NOT NULL,
    genre_id CHAR(36) NOT NULL,
    PRIMARY KEY (work_id, genre_id),
    FOREIGN KEY (work_id) REFERENCES work(id),
    FOREIGN KEY (genre_id) REFERENCES genre(id)
);

CREATE TABLE author (
    id CHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type ENUM('WRITER', 'ARTIST', 'BOTH') NOT NULL
);

CREATE TABLE work_author (
    work_id CHAR(36) NOT NULL,
    author_id CHAR(36) NOT NULL,
    role VARCHAR(50),
    PRIMARY KEY (work_id, author_id),
    FOREIGN KEY (work_id) REFERENCES work(id),
    FOREIGN KEY (author_id) REFERENCES author(id)
);

-- Tags
CREATE TABLE tag (
    id CHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    is_nsfw BOOLEAN DEFAULT FALSE
);

CREATE TABLE work_tag (
    work_id CHAR(36) NOT NULL,
    tag_id CHAR(36) NOT NULL,
    PRIMARY KEY (work_id, tag_id),
    FOREIGN KEY (work_id) REFERENCES work(id),
    FOREIGN KEY (tag_id) REFERENCES tag(id)
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
    role VARCHAR(20) NOT NULL,
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
