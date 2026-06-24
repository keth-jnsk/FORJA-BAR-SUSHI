package org.example.view;

import org.example.controller.PedidoController;
import org.example.model.Produto;
import org.example.util.AppContext;
import org.example.util.ResultadoOperacao;
import org.example.util.ErroUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PedidoFrame extends JFrame {

    private final PedidoController pedidoController;
    private final int numeroMesa;

    private DefaultTableModel modeloCardapio;
    private DefaultTableModel modeloItensPedido;
    private JTable tabelaCardapio;
    private JLabel rotuloTotal;
    private JSpinner spinnerQuantidade;

    public PedidoFrame(int numeroMesa) {
        this.numeroMesa = numeroMesa;
        this.pedidoController = AppContext.novoPedidoController();
        this.pedidoController.iniciarNovoPedido(numeroMesa);
        montarTela();
        carregarCardapio();
    }

    /**
     * Pergunta o número da mesa antes de abrir a tela de pedido. Retorna
     * null se o usuário cancelar ou não informar um número válido — quem
     * chamar este método deve checar isso antes de abrir o PedidoFrame.
     */
    public static Integer perguntarNumeroMesa(Component pai) {
        while (true) {
            String texto = JOptionPane.showInputDialog(pai, "Número da mesa:", "Novo Pedido", JOptionPane.QUESTION_MESSAGE);
            if (texto == null) {
                return null; // cancelou
            }
            texto = texto.trim();
            try {
                int mesa = Integer.parseInt(texto);
                if (mesa <= 0) {
                    JOptionPane.showMessageDialog(pai, "O número da mesa deve ser maior que zero.", "Mesa inválida", JOptionPane.WARNING_MESSAGE);
                    continue;
                }
                return mesa;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(pai, "Digite um número de mesa válido.", "Mesa inválida", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void montarTela() {
        setTitle("Forja Bar — Novo Pedido (Mesa " + numeroMesa + ")");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(860, 540));
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(ComponentesUi.COR_FUNDO);
        setContentPane(painelPrincipal);

        JPanel cabecalho = ComponentesUi.criarCabecalho("Novo Pedido — Mesa " + numeroMesa, ComponentesUi.ROSA_NEON);

        JPanel corpo = new JPanel(new GridLayout(1, 2, 12, 0));
        corpo.setBackground(ComponentesUi.COR_PAINEL);
        corpo.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        corpo.add(montarColunaCardapio());
        corpo.add(montarColunaItensPedido());

        JButton botaoVoltar = ComponentesUi.criarBotaoSecundario("Voltar ao Menu");
        botaoVoltar.addActionListener(e -> dispose());

        JButton botaoConfirmar = ComponentesUi.criarBotaoPrimario("Confirmar Pedido", ComponentesUi.VERDE_NEON, Color.WHITE);
        ComponentesUi.aoClicar(botaoConfirmar, this::confirmarPedido);

        JPanel rodape = ComponentesUi.montarBarraAcoes(botaoVoltar, botaoConfirmar);
        rodape.setBorder(BorderFactory.createEmptyBorder(10, 20, 16, 20));

        painelPrincipal.add(cabecalho, BorderLayout.NORTH);
        painelPrincipal.add(corpo, BorderLayout.CENTER);
        painelPrincipal.add(rodape, BorderLayout.SOUTH);
        pack();
    }

    private JPanel montarColunaCardapio() {
        JPanel coluna = new JPanel(new BorderLayout(0, 8));
        coluna.setBackground(ComponentesUi.COR_PAINEL);

        JLabel rotulo = ComponentesUi.criarRotuloCampo("Cardápio Disponível");
        String[] colunas = {"ID", "Nome", "Preço", "Estoque", "Tipo"};
        modeloCardapio = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int linha, int coluna) { return false; }
        };
        tabelaCardapio = ComponentesUi.criarTabelaEstilizada(modeloCardapio, ComponentesUi.ROSA_NEON);
        JScrollPane scroll = new JScrollPane(tabelaCardapio);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(45, 25, 38)));
        scroll.getViewport().setBackground(ComponentesUi.COR_PAINEL);

        JPanel painelQuantidade = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        painelQuantidade.setBackground(ComponentesUi.COR_PAINEL);
        JLabel rotuloQuantidade = ComponentesUi.criarRotuloCampo("Quantidade:");
        spinnerQuantidade = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        spinnerQuantidade.setPreferredSize(new Dimension(70, 30));

        JButton botaoAdicionar = ComponentesUi.criarBotaoPrimario("Adicionar", ComponentesUi.ROSA_NEON, Color.WHITE);
        ComponentesUi.aoClicar(botaoAdicionar, this::adicionarItemAoPedido);

        painelQuantidade.add(rotuloQuantidade);
        painelQuantidade.add(spinnerQuantidade);
        painelQuantidade.add(botaoAdicionar);

        coluna.add(rotulo, BorderLayout.NORTH);
        coluna.add(scroll, BorderLayout.CENTER);
        coluna.add(painelQuantidade, BorderLayout.SOUTH);
        return coluna;
    }

    private JPanel montarColunaItensPedido() {
        JPanel coluna = new JPanel(new BorderLayout(0, 8));
        coluna.setBackground(ComponentesUi.COR_PAINEL);

        JLabel rotulo = ComponentesUi.criarRotuloCampo("Itens do Pedido");
        String[] colunas = {"Produto", "Quantidade", "Subtotal"};
        modeloItensPedido = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int linha, int coluna) { return false; }
        };
        JTable tabelaItens = ComponentesUi.criarTabelaEstilizada(modeloItensPedido, ComponentesUi.ROSA_NEON);
        JScrollPane scroll = new JScrollPane(tabelaItens);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(45, 25, 38)));
        scroll.getViewport().setBackground(ComponentesUi.COR_PAINEL);

        rotuloTotal = new JLabel("Total: R$ 0,00");
        rotuloTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        rotuloTotal.setForeground(ComponentesUi.COR_DESTAQUE);

        coluna.add(rotulo, BorderLayout.NORTH);
        coluna.add(scroll, BorderLayout.CENTER);
        coluna.add(rotuloTotal, BorderLayout.SOUTH);
        return coluna;
    }

    private void carregarCardapio() {
        modeloCardapio.setRowCount(0);
        List<Produto> cardapio;
        try {
            cardapio = pedidoController.listarCardapioDisponivel();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar cardápio: " + ErroUtil.causaRaiz(e), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        for (Produto produto : cardapio) {
            modeloCardapio.addRow(new Object[]{
                    produto.getId(), produto.getNome(),
                    String.format("R$ %.2f", produto.getPreco()),
                    produto.getQuantidade(), produto.getTipo()
            });
        }
    }

    private void adicionarItemAoPedido() {
        int linhaSelecionada = tabelaCardapio.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto no cardápio.");
            return;
        }
        int idProduto = (int) modeloCardapio.getValueAt(linhaSelecionada, 0);
        String nomeProduto = (String) modeloCardapio.getValueAt(linhaSelecionada, 1);
        int quantidade = (int) spinnerQuantidade.getValue();

        try {
            ResultadoOperacao resultado = pedidoController.adicionarItem(idProduto, quantidade);

            if (!resultado.isSucesso()) {
                JOptionPane.showMessageDialog(this, resultado.getMensagem(), "Não foi possível adicionar", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Produto produto = pedidoController.getPedidoEmAndamento().getItens().get(
                    pedidoController.getPedidoEmAndamento().getItens().size() - 1).getProduto();
            double subtotal = produto.getPreco() * quantidade;
            modeloItensPedido.addRow(new Object[]{nomeProduto, quantidade, String.format("R$ %.2f", subtotal)});

            atualizarRotuloTotal();
            carregarCardapio();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar item: " + ErroUtil.causaRaiz(e), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarRotuloTotal() {
        double total = pedidoController.calcularTotalAtual();
        if (pedidoController.pedidoAtualTemDesconto()) {
            rotuloTotal.setText(String.format("Total: R$ %.2f (10%% OFF aplicado)", pedidoController.calcularTotalComDescontoAtual()));
        } else {
            rotuloTotal.setText(String.format("Total: R$ %.2f", total));
        }
    }

    private void confirmarPedido() {
        if (pedidoController.getPedidoEmAndamento().getItens().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Adicione ao menos um item antes de confirmar.", "Pedido vazio", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Confirmar o pedido da Mesa " + numeroMesa + "? Esta ação não pode ser desfeita.",
                "Confirmar Pedido", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            ResultadoOperacao resultado = pedidoController.confirmarPedidoEAbrirFicha();

            if (!resultado.isSucesso()) {
                JOptionPane.showMessageDialog(this, resultado.getMensagem(), "Não foi possível confirmar", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this, resultado.getMensagem());
            dispose();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Erro ao confirmar pedido: " + ErroUtil.causaRaiz(e), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
