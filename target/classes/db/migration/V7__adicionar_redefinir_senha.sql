-- Suporte a troca obrigatória de senha: toda conta nova, e toda conta que
-- tenha a senha redefinida pelo administrador, precisa trocar a senha
-- provisória no próximo login.
-- Contas já existentes (ex.: o admin de seed) ficam com FALSE por padrão,
-- para não travar logins que já estavam funcionando antes desta migration.

ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS deve_redefinir_senha BOOLEAN NOT NULL DEFAULT FALSE;
