package org.example.view;

import org.example.controller.FichaController;
import org.example.model.Ficha;
import org.example.util.AppContext;
import org.example.util.ResultadoOperacao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Tela de fichas abertas. Permite visualizar e fechar a ficha de uma mesa.
 * Antes esta tela acumulava itens manualmente (duplicando a lógica do
 * PedidoFrame); agora ela apenas lista as fichas abertas pelo PedidoFrame
 * e oferece a ação de fechamento, delegada ao FichaController.
 */
public class FichaFrame extends JFrame {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final FichaController fichaController;

    private DefaultTableModel modeloTabela;
    private JTable tabelaFichas;
    private List<Ficha> fichasExibidas;

    public FichaFrame() {
        this.fichaController = AppContext.fichaController();
        montarTela();
        carregarFichasAbertas();
    }

    private void montarTela() {
        setTitle("Forja Bar — Fichas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(700, 460));
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(ComponentesUi.COR_FUNDO);
        setContentPane(painelPrincipal);

        JPanel cabecalho = ComponentesUi.criarCabecalho("Fichas Abertas", ComponentesUi.ROSA_PROFUNDO);

        String[] colunas = {"Mesa", "Pedido", "Abertura", "Total Atual (R$)"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int linha, int coluna) { return false; }
        };
        tabelaFichas = ComponentesUi.criarTabelaEstilizada(modeloTabela, ComponentesUi.ROSA_PROFUNDO);
        JScrollPane scroll = new JScrollPane(tabelaFichas);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(ComponentesUi.COR_PAINEL);

        JButton botaoAtualizar = ComponentesUi.criarBotaoSecundario("Atualizar");
        ComponentesUi.aoClicar(botaoAtualizar, this::carregarFichasAbertas);

        JButton botaoFechar = ComponentesUi.criarBotaoPrimario("Fechar Ficha", ComponentesUi.VERDE_NEON, Color.WHITE);
        ComponentesUi.aoClicar(botaoFechar, this::fecharFichaSelecionada);

        JPanel rodape = ComponentesUi.montarBarraAcoes(botaoAtualizar, botaoFechar);
        rodape.setBorder(BorderFactory.createEmptyBorder(10, 20, 16, 20));

        painelPrincipal.add(cabecalho, BorderLayout.NORTH);
        painelPrincipal.add(scroll, BorderLayout.CENTER);
        painelPrincipal.add(rodape, BorderLayout.SOUTH);
        pack();
    }

    private void carregarFichasAbertas() {
        modeloTabela.setRowCount(0);
        try {
            fichasExibidas = fichaController.listarFichasAbertas();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar fichas: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            fichasExibidas = List.of();
            return;
        }

        for (Ficha ficha : fichasExibidas) {
            modeloTabela.addRow(new Object[]{
                    "Mesa " + ficha.getPedido().getNumeroMesa(),
                    "#" + ficha.getPedido().getId(),
                    ficha.getDataAbertura().format(FORMATO_DATA),
                    String.format("%.2f", ficha.getPedido().calcularTotalComDesconto())
            });
        }

        if (fichasExibidas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há fichas abertas no momento.");
        }
    }

    private void fecharFichaSelecionada() {
        int linhaSelecionada = tabelaFichas.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma ficha para fechar.");
            return;
        }

        Ficha ficha = fichasExibidas.get(linhaSelecionada);
        UUID idFicha = ficha.getId();

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Fechar a ficha da Mesa " + ficha.getPedido().getNumeroMesa() + "?",
                "Confirmar fechamento", JOptionPane.YES_NO_OPTION);
        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            ResultadoOperacao resultado = fichaController.fecharFicha(idFicha);
            JOptionPane.showMessageDialog(this, resultado.getMensagem());
            if (resultado.isSucesso()) {
                carregarFichasAbertas();
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Erro ao fechar ficha: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
