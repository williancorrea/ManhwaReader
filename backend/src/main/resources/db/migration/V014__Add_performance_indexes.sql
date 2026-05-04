-- Suporta ORDER BY em findFirstUnreadChapter e findPagedByWorkSlug
-- (work_id, disabled, number_formatted, number_version)
CREATE INDEX idx_chapter_work_order
    ON chapter(work_id, disabled, number_formatted, number_version);

-- Suporta MAX(last_read_at) na subconsulta de ordenação em findContinueReadingByUserId
-- O índice UNIQUE existente é (user_id, chapter_id) — não cobre last_read_at
CREATE INDEX idx_reading_progress_user_last_read
    ON reading_progress(user_id, last_read_at);
