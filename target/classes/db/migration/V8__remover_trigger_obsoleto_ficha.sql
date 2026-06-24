-- O trigger "trigger_validar_ficha" (e a função "validar_ficha()"), criados
-- na V2, foram escritos para a coluna booleana "usada" da tabela fichas.
-- A V5 removeu essa coluna (substituída pela coluna "status", que já é
-- validada no código Java em Ficha.fechar()), mas esqueceu de remover este
-- trigger. Resultado: toda atualização na tabela fichas (por exemplo, ao
-- fechar uma ficha) disparava o trigger, que tentava ler OLD.usada /
-- NEW.usada — colunas que não existem mais — e o UPDATE falhava com:
-- "record \"old\" has no field \"usada\"".
--
-- A regra que esse trigger aplicava (não permitir fechar uma ficha já
-- fechada) já é garantida em Ficha.fechar() na aplicação, então é seguro
-- remover apenas o trigger/função obsoletos, sem substituí-los.

DROP TRIGGER IF EXISTS trigger_validar_ficha ON fichas;
DROP FUNCTION IF EXISTS validar_ficha();
