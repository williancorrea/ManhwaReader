-- Controle de acesso: permissoes, grupos e relacionamento com usuarios
CREATE TABLE permission (
    id CHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE access_group (
    id CHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE access_group_permission (
    access_group_id CHAR(36) NOT NULL,
    permission_id CHAR(36) NOT NULL,
    PRIMARY KEY (access_group_id, permission_id),
    FOREIGN KEY (access_group_id) REFERENCES access_group(id),
    FOREIGN KEY (permission_id) REFERENCES permission(id)
);

CREATE TABLE user_access_group (
    user_id CHAR(36) NOT NULL,
    access_group_id CHAR(36) NOT NULL,
    PRIMARY KEY (user_id, access_group_id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (access_group_id) REFERENCES access_group(id)
);

-- Seed de permissoes iniciais
INSERT INTO permission (id, name)
VALUES
    (UUID(), 'ADMINISTRATOR'),
    (UUID(), 'MODERATOR'),
    (UUID(), 'READER'),
    (UUID(), 'UPLOADER');

-- Seed de grupos iniciais
INSERT INTO access_group (id, name)
VALUES
    (UUID(), 'ADMINISTRATOR'),
    (UUID(), 'MODERATOR'),
    (UUID(), 'READER'),
    (UUID(), 'UPLOADER');

-- Vincula grupos com permissoes correspondentes
INSERT INTO access_group_permission (access_group_id, permission_id)
    SELECT ag.id, p.id
    FROM access_group ag
    JOIN permission p ON p.name = ag.name;
