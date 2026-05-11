-- Suporta filtro composto em findLibraryItemsByUserId (WHERE user_id = ? AND status = ?)
-- A UNIQUE (user_id, work_id) existente não cobre filtro por status sem leitura de heap
CREATE INDEX idx_library_user_status ON library(user_id, status);
