-- Tags
CREATE TABLE tag(
    id        CHAR(36)     NOT NULL PRIMARY KEY,
    group_tag VARCHAR(50)  NOT NULL,
    name      VARCHAR(100),
    alias1    VARCHAR(100),
    alias2    VARCHAR(100),
    alias3    VARCHAR(100)
);

# GENRE
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

# FORMAT
# THEME
# CONTENT
