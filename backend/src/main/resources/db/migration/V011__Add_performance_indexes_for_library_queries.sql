-- Índice composto para lookup de título oficial por obra (cobre work_id + is_official sem heap fetch)
CREATE INDEX idx_work_title_work_official ON work_title(work_id, is_official);

-- Índice composto para lookup de capa oficial por obra
CREATE INDEX idx_work_cover_work_official ON work_cover(work_id, is_official);

-- Índice composto para contagem de capítulos ativos por obra (unread_count e chapter_count)
CREATE INDEX idx_chapter_work_disabled ON chapter(work_id, disabled);

-- Índice por chapter_id para o padrão LEFT JOIN em reading_progress (busca por capítulo antes do usuário)
-- O índice UNIQUE existente é (user_id, chapter_id), que não serve buscas iniciadas por chapter_id
CREATE INDEX idx_reading_progress_chapter_user ON reading_progress(chapter_id, user_id);
