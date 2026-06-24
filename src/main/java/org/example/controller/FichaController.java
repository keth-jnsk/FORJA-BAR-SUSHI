package org.example.controller;

import org.example.model.Ficha;
import org.example.service.FichaService;
import org.example.util.ResultadoOperacao;

import java.util.List;

public class FichaController {

    private final FichaService fichaService;

    public FichaController(FichaService fichaService) {
        this.fichaService = fichaService;
    }

    public List<Ficha> listarFichasAbertas() {
        return fichaService.listarAbertas();
    }

    public List<Ficha> listarTodasAsFichas() {
        return fichaService.listarTodas();
    }

    public ResultadoOperacao fecharFicha(String idFicha) {
        if (idFicha == null) {
            return ResultadoOperacao.erro("Selecione uma ficha para fechar.");
        }
        return fichaService.fecharFicha(idFicha);
    }

    public ResultadoOperacao iniciarPreparo(String idFicha) {
        if (idFicha == null) {
            return ResultadoOperacao.erro("Selecione uma ficha.");
        }
        return fichaService.iniciarPreparo(idFicha);
    }

    public ResultadoOperacao marcarComoPronto(String idFicha) {
        if (idFicha == null) {
            return ResultadoOperacao.erro("Selecione uma ficha.");
        }
        return fichaService.marcarComoPronto(idFicha);
    }
}
