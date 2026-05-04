-- Suporta ORDER BY updated_at DESC/ASC sem filtros ativos (ordenação padrão do catálogo)
CREATE INDEX idx_work_updated_at
    ON work(updated_at);

-- Suporta filtro por status + ORDER BY updated_at (padrão: status != null)
CREATE INDEX idx_work_status_updated_at
    ON work(status, updated_at);

-- Suporta filtro por type + ORDER BY updated_at
CREATE INDEX idx_work_type_updated_at
    ON work(type, updated_at);

-- Suporta filtro por publication_demographic + ORDER BY updated_at
CREATE INDEX idx_work_demographic_updated_at
    ON work(publication_demographic, updated_at);

-- NOTA: o filtro por título usa UPPER(title) LIKE '%texto%' com wildcard à esquerda,
-- o que impede o uso de qualquer índice B-tree. Para pesquisa textual eficiente,
-- seria necessário um índice FULLTEXT e mudar WorkSpecification.withTitle() para
-- usar MATCH(title) AGAINST (:title IN BOOLEAN MODE).
