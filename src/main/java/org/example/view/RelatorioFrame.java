package org.example.view;

import org.example.controller.RelatorioController;
import org.example.model.Pedido;
import org.example.util.AppContext;
import org.example.util.RelatorioDoDia;
import org.example.util.ErroUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class RelatorioFrame extends JFrame {

    private final RelatorioController relatorioController;

    private DefaultTableModel modeloTabela;
    private JLabel rotuloTotalGeral;
    private JLabel rotuloTotalPedidos;
    private JLabel rotuloProdutoMaisVendido;
    private JLabel rotuloPeriodo;
    private GraficoBarras grafico;
    private CardLayout cardLayout;
    private JPanel painelCartoes;

    /** true = exibindo relatório do mês; false = exibindo relatório do dia. */
    private boolean exibindoMes = false;

    public RelatorioFrame() {
        this.relatorioController = AppContext.relatorioController();
        montarTela();
        carregarRelatorio();
    }

    private void montarTela() {
        setTitle("Forja Bar — Relatórios");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(700, 580));
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(ComponentesUi.COR_FUNDO);
        setContentPane(painelPrincipal);

        JPanel cabecalho = ComponentesUi.criarCabecalho("Relatórios", ComponentesUi.ROSA_SALMAO);

        JPanel painelAbas = new JPanel(new GridLayout(1, 2, 8, 0));
        painelAbas.setBackground(ComponentesUi.COR_FUNDO);
        painelAbas.setBorder(BorderFactory.createEmptyBorder(12, 20, 0, 20));

        JButton botaoDia = ComponentesUi.criarBotaoPrimario("Relatório do Dia", ComponentesUi.ROSA_SALMAO, Color.WHITE);
        botaoDia.addActionListener(e -> {
            exibindoMes = false;
            carregarRelatorio();
        });
        JButton botaoMes = ComponentesUi.criarBotaoSecundario("Relatório do Mês");
        botaoMes.addActionListener(e -> {
            exibindoMes = true;
            carregarRelatorio();
        });
        painelAbas.add(botaoDia);
        painelAbas.add(botaoMes);

        JPanel painelAlternarVisao = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        painelAlternarVisao.setOpaque(false);
        painelAlternarVisao.setBorder(BorderFactory.createEmptyBorder(8, 20, 0, 20));
        JButton botaoVerTabela = ComponentesUi.criarBotaoSecundario("Ver Tabela");
        botaoVerTabela.addActionListener(e -> cardLayout.show(painelCartoes, "tabela"));
        JButton botaoVerGrafico = ComponentesUi.criarBotaoSecundario("Ver Gráfico");
        botaoVerGrafico.addActionListener(e -> cardLayout.show(painelCartoes, "grafico"));
        painelAlternarVisao.add(botaoVerTabela);
        painelAlternarVisao.add(botaoVerGrafico);

        rotuloPeriodo = new JLabel("Período: Hoje");
        rotuloPeriodo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rotuloPeriodo.setForeground(ComponentesUi.COR_TEXTO_ROTULO);
        rotuloPeriodo.setBorder(BorderFactory.createEmptyBorder(10, 20, 4, 20));

        String[] colunas = {"Pedido", "Mesa", "Status", "Total (R$)", "Itens"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int linha, int coluna) { return false; }
        };
        JTable tabela = ComponentesUi.criarTabelaEstilizada(modeloTabela, ComponentesUi.ROSA_SALMAO);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(ComponentesUi.COR_PAINEL);

        grafico = new GraficoBarras();
        JScrollPane scrollGrafico = new JScrollPane(grafico);
        scrollGrafico.setBorder(BorderFactory.createEmptyBorder());
        scrollGrafico.getViewport().setBackground(ComponentesUi.COR_PAINEL);

        cardLayout = new CardLayout();
        painelCartoes = new JPanel(cardLayout);
        painelCartoes.setOpaque(false);
        painelCartoes.add(scroll, "tabela");
        painelCartoes.add(scrollGrafico, "grafico");

        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setBackground(ComponentesUi.COR_FUNDO);
        painelTopo.add(painelAbas, BorderLayout.NORTH);
        painelTopo.add(painelAlternarVisao, BorderLayout.CENTER);
        painelTopo.add(rotuloPeriodo, BorderLayout.SOUTH);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(ComponentesUi.COR_FUNDO);
        centro.add(painelTopo, BorderLayout.NORTH);
        centro.add(painelCartoes, BorderLayout.CENTER);

        JPanel painelTotais = new JPanel(new GridLayout(3, 1, 0, 4));
        painelTotais.setBackground(ComponentesUi.COR_CABECALHO_BASE);
        painelTotais.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        rotuloTotalPedidos = new JLabel("Total de pedidos: 0");
        rotuloTotalPedidos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rotuloTotalPedidos.setForeground(ComponentesUi.COR_TEXTO_ROTULO);

        rotuloProdutoMaisVendido = new JLabel("Produto mais vendido: —");
        rotuloProdutoMaisVendido.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rotuloProdutoMaisVendido.setForeground(ComponentesUi.COR_TEXTO_ROTULO);

        rotuloTotalGeral = new JLabel("Total geral: R$ 0,00");
        rotuloTotalGeral.setFont(new Font("Segoe UI", Font.BOLD, 16));
        rotuloTotalGeral.setForeground(ComponentesUi.COR_DESTAQUE);

        painelTotais.add(rotuloTotalPedidos);
        painelTotais.add(rotuloProdutoMaisVendido);
        painelTotais.add(rotuloTotalGeral);

        JButton botaoAtualizar = ComponentesUi.criarBotaoSecundario("Atualizar");
        ComponentesUi.aoClicar(botaoAtualizar, this::carregarRelatorio);

        JButton botaoVoltar = ComponentesUi.criarBotaoSecundario("Voltar ao Menu");
        botaoVoltar.addActionListener(e -> dispose());

        JPanel rodape = ComponentesUi.montarBarraAcoes(botaoAtualizar, botaoVoltar);
        rodape.setBorder(BorderFactory.createEmptyBorder(10, 20, 16, 20));

        JPanel sul = new JPanel(new BorderLayout());
        sul.setBackground(ComponentesUi.COR_FUNDO);
        sul.add(painelTotais, BorderLayout.NORTH);
        sul.add(rodape, BorderLayout.SOUTH);

        painelPrincipal.add(cabecalho, BorderLayout.NORTH);
        painelPrincipal.add(centro, BorderLayout.CENTER);
        painelPrincipal.add(sul, BorderLayout.SOUTH);
        pack();
    }

    private void carregarRelatorio() {
        modeloTabela.setRowCount(0);

        RelatorioDoDia relatorio;
        try {
            relatorio = exibindoMes ? relatorioController.gerarRelatorioDoMes() : relatorioController.gerarRelatorioDoDia();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar relatório: " + ErroUtil.causaRaiz(e), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        rotuloPeriodo.setText(exibindoMes ? "Período: Mês atual" : "Período: Hoje");

        for (Pedido pedido : relatorio.getPedidos()) {
            modeloTabela.addRow(new Object[]{
                    "#" + pedido.getId(),
                    "Mesa " + pedido.getNumeroMesa(),
                    ComponentesUi.formatarStatusPedido(pedido.getStatus()),
                    String.format("%.2f", pedido.calcularTotalComDesconto()),
                    pedido.getItens().size()
            });
        }

        rotuloTotalPedidos.setText("Total de pedidos: " + relatorio.getPedidos().size());
        rotuloTotalGeral.setText(String.format("Total geral: R$ %.2f", relatorio.getTotalGeral()));
        rotuloProdutoMaisVendido.setText(
                "Produto mais vendido: " + relatorio.getProdutoMaisVendido().orElse("—"));
        grafico.atualizarDados(relatorio.getQuantidadeVendidaPorProduto());

        if (relatorio.getPedidos().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    exibindoMes
                            ? "Nenhum pedido confirmado neste mês ainda."
                            : "Nenhum pedido confirmado hoje ainda.");
        }
    }

    /** Gráfico de barras horizontais — quantidade vendida por produto, alternando rosa e verde. */
    private static class GraficoBarras extends JPanel {
        private Map<String, Integer> dados = new LinkedHashMap<>();

        GraficoBarras() {
            setBackground(ComponentesUi.COR_PAINEL);
        }

        void atualizarDados(Map<String, Integer> novosDados) {
            this.dados = novosDados == null ? new LinkedHashMap<>() : novosDados;
            int alturaNecessaria = Math.max(200, dados.size() * 42 + 20);
            setPreferredSize(new Dimension(10, alturaNecessaria));
            revalidate();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (dados.isEmpty()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(ComponentesUi.COR_TEXTO_ROTULO);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                g2.drawString("Sem dados de venda no período selecionado.", 24, 30);
                g2.dispose();
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int largura = getWidth();
            int margemEsquerda = 150;
            int margemDireita = 60;
            int larguraBarraMax = Math.max(40, largura - margemEsquerda - margemDireita);
            int maiorValor = dados.values().stream().max(Integer::compareTo).orElse(1);

            int y = 16;
            int alturaBarra = 26;
            int espacamento = 16;
            int indice = 0;
            for (Map.Entry<String, Integer> entrada : dados.entrySet()) {
                Color cor = (indice % 2 == 0) ? ComponentesUi.ROSA_NEON : ComponentesUi.VERDE_NEON;
                int larguraBarra = (int) (((double) entrada.getValue() / maiorValor) * larguraBarraMax);

                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.setColor(ComponentesUi.COR_TEXTO_CLARO);
                String nomeProduto = entrada.getKey();
                if (nomeProduto.length() > 18) {
                    nomeProduto = nomeProduto.substring(0, 17) + "…";
                }
                g2.drawString(nomeProduto, 12, y + alturaBarra - 8);

                GradientPaint gradiente = new GradientPaint(
                        margemEsquerda, y, cor.brighter(), margemEsquerda + larguraBarra, y, cor.darker());
                g2.setPaint(gradiente);
                g2.fillRoundRect(margemEsquerda, y, Math.max(2, larguraBarra), alturaBarra, 8, 8);

                g2.setColor(ComponentesUi.COR_TEXTO_CLARO);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.drawString(String.valueOf(entrada.getValue()), margemEsquerda + larguraBarra + 8, y + alturaBarra - 8);

                y += alturaBarra + espacamento;
                indice++;
            }
            g2.dispose();
        }
    }
}
