CREATE TABLE language (
                          id CHAR(36) NOT NULL PRIMARY KEY,
                          code VARCHAR(10) NOT NULL UNIQUE,
                          name VARCHAR(50) NOT NULL
);

-- Portuguese
insert into language (id, code, name) values (UUID(), 'pt-BR', 'Portuguese (Brazil)');
insert into language (id, code, name) values (UUID(), 'pt-PT', 'Portuguese (Portugal)');

-- English
insert into language (id, code, name) values (UUID(), 'en', 'English');
insert into language (id, code, name) values (UUID(), 'en-US', 'English (United States)');
insert into language (id, code, name) values (UUID(), 'en-GB', 'English (United Kingdom)');

-- Spanish
insert into language (id, code, name) values (UUID(), 'es', 'Spanish');
insert into language (id, code, name) values (UUID(), 'es-ES', 'Spanish (Spain)');
insert into language (id, code, name) values (UUID(), 'es-MX', 'Spanish (Mexico)');

-- French
insert into language (id, code, name) values (UUID(), 'fr', 'French');
insert into language (id, code, name) values (UUID(), 'fr-FR', 'French (France)');
insert into language (id, code, name) values (UUID(), 'fr-CA', 'French (Canada)');

-- German
insert into language (id, code, name) values (UUID(), 'de', 'German');
insert into language (id, code, name) values (UUID(), 'de-DE', 'German (Germany)');

-- Italian
insert into language (id, code, name) values (UUID(), 'it', 'Italian');
insert into language (id, code, name) values (UUID(), 'it-IT', 'Italian (Italy)');

-- Polish
insert into language (id, code, name) values (UUID(), 'pl', 'Polish');

-- Russian
insert into language (id, code, name) values (UUID(), 'ru', 'Russian');

-- Japanese
insert into language (id, code, name) values (UUID(), 'ja', 'Japanese');

-- Korean
insert into language (id, code, name) values (UUID(), 'ko', 'Korean');
insert into language (id, code, name) values (UUID(), 'ko-KR', 'Korean (South Korea)');
insert into language (id, code, name) values (UUID(), 'ko-KP', 'Korean (North Korea)');
insert into language (id, code, name) values (UUID(), 'ko-LATN', 'Korean (Romanized)');
insert into language (id, code, name) values (UUID(), 'ko-RO', 'Korean (Romanized)');

-- Chinese
insert into language (id, code, name) values (UUID(), 'zh', 'Chinese');
insert into language (id, code, name) values (UUID(), 'zh-CN', 'Chinese (Simplified, China)');
insert into language (id, code, name) values (UUID(), 'zh-TW', 'Chinese (Traditional, Taiwan)');
insert into language (id, code, name) values (UUID(), 'zh-HK', 'Chinese (Hong Kong)');

-- Thai
insert into language (id, code, name) values (UUID(), 'th', 'Thai');

-- Arabic
insert into language (id, code, name) values (UUID(), 'ar', 'Arabic');
insert into language (id, code, name) values (UUID(), 'ar-SA', 'Arabic (Saudi Arabia)');
