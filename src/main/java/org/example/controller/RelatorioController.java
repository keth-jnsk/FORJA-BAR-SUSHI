package org.example.controller;

import org.example.service.RelatorioService;
import org.example.util.RelatorioDoDia;

public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    public RelatorioDoDia gerarRelatorioDoDia() {
        return relatorioService.gerarRelatorioDoDia();
    }

    public RelatorioDoDia gerarRelatorioDoMes() {
        return relatorioService.gerarRelatorioDoMes();
    }
}
