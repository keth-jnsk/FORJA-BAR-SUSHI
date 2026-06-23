-- Adequa a tabela fichas ao novo modelo da entidade Ficha:
-- agora ela tem status (ABERTA/FECHADA), data de abertura, data de
-- fechamento e o valor total já calculado no momento do fechamento
-- (regra de fechamento de ficha, antes inexistente).

ALTER TABLE fichas ADD COLUMN IF NOT EXISTS status VARCHAR(10) DEFAULT 'ABERTA';
UPDATE fichas SET status = CASE WHEN usada THEN 'FECHADA' ELSE 'ABERTA' END;
ALTER TABLE fichas ALTER COLUMN status SET NOT NULL;

ALTER TABLE fichas ADD COLUMN IF NOT EXISTS data_abertura TIMESTAMP DEFAULT NOW();
UPDATE fichas SET data_abertura = NOW() WHERE data_abertura IS NULL;
ALTER TABLE fichas ALTER COLUMN data_abertura SET NOT NULL;

ALTER TABLE fichas ADD COLUMN IF NOT EXISTS data_fechamento TIMESTAMP;
ALTER TABLE fichas ADD COLUMN IF NOT EXISTS valor_total_fechamento NUMERIC(10,2);

ALTER TABLE fichas DROP COLUMN IF EXISTS usada;
