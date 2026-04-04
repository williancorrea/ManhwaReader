-- Remover coluna username (login será via email)
ALTER TABLE user DROP COLUMN username;

-- Adicionar novos campos de autenticação
ALTER TABLE user ADD COLUMN name VARCHAR(100) NOT NULL DEFAULT '' AFTER id;
ALTER TABLE user ADD COLUMN google_id VARCHAR(255) NULL AFTER password_hash;
ALTER TABLE user ADD COLUMN avatar_url VARCHAR(500) NULL AFTER google_id;
ALTER TABLE user ADD COLUMN email_verified BOOLEAN NOT NULL DEFAULT FALSE AFTER avatar_url;
ALTER TABLE user ADD COLUMN updated_at TIMESTAMP NULL AFTER created_at;

-- Tornar password_hash nullable para usuários Google
ALTER TABLE user MODIFY COLUMN password_hash VARCHAR(255) NULL;

-- Índice único para google_id (permitindo NULL)
CREATE UNIQUE INDEX idx_user_google_id ON user(google_id);

-- Atualizar usuário admin existente
UPDATE user SET name = 'Administrador', email_verified = TRUE WHERE email = 'willian.vag@gmail.com';
