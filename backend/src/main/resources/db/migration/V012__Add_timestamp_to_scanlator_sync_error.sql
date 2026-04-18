ALTER TABLE scanlator_synchronization_error
    ADD COLUMN created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6);

CREATE INDEX idx_sync_error_scanlator_created
    ON scanlator_synchronization_error (scanlator_id, created_at DESC);

CREATE INDEX idx_sync_error_created
    ON scanlator_synchronization_error (created_at DESC);
