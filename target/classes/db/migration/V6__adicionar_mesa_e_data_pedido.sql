-- Adiciona o número da mesa ao pedido (perguntado ao abrir "Novo Pedido")
-- e a data/hora de criação, necessária para os relatórios do dia e do mês
-- (antes o relatório "do dia" na verdade somava TODOS os pedidos já feitos,
-- de qualquer data, por não existir nenhum campo de data em pedidos).

ALTER TABLE pedidos ADD COLUMN IF NOT EXISTS numero_mesa INT;
UPDATE pedidos SET numero_mesa = 0 WHERE numero_mesa IS NULL;
ALTER TABLE pedidos ALTER COLUMN numero_mesa SET NOT NULL;

ALTER TABLE pedidos ADD COLUMN IF NOT EXISTS data_criacao TIMESTAMP DEFAULT NOW();
UPDATE pedidos SET data_criacao = NOW() WHERE data_criacao IS NULL;
ALTER TABLE pedidos ALTER COLUMN data_criacao SET NOT NULL;
