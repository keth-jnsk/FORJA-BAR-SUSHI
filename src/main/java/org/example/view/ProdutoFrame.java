package org.example.view;

import org.example.controller.EstoqueController;
import org.example.model.Produto;
import org.example.util.AppContext;
import org.example.util.ResultadoOperacao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Tela de gerenciamento e visualização de estoque. Lista os produtos
 * (sempre ordenados por ID, do menor para o maior — pedido pelo Repository)
 * e permite editar nome, preço e quantidade do produto selecionado.
 */
public class ProdutoFrame extends JFrame {

    private final EstoqueController estoqueController;
    private DefaultTableModel modeloTabela;
    private JTable tabelaProdutos;
    private List<Produto> produtosExibidos;

    private JTextField campoNomeEdicao;
    private JTextField campoPrecoEdicao;
    private JTextField campoEstoqueEdicao;

    public ProdutoFrame() {
        this.estoqueController = AppContext.estoqueController();
        montarTela();
        carregarProdutos(false);
    }

    private void montarTela() {
        setTitle("Forja Bar — Gerenciar e Visualizar Estoque");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(760, 560));
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(ComponentesUi.COR_FUNDO);
        setContentPane(painelPrincipal);

        JPanel cabecalho = ComponentesUi.criarCabecalho("Gerenciar e Visualizar Estoque", ComponentesUi.ROSA_CLARO);

        String[] colunas = {"ID", "Nome", "Preço (R$)", "Estoque", "Tipo"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int linha, int coluna) { return false; }
        };
        tabelaProdutos = ComponentesUi.criarTabelaEstilizada(modeloTabela, ComponentesUi.ROSA_CLARO);
        tabelaProdutos.getSelectionModel().addListSelectionListener(e -> preencherCamposComSelecao());

        JScrollPane scroll = new JScrollPane(tabelaProdutos);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(ComponentesUi.COR_PAINEL);

        painelPrincipal.add(cabecalho, BorderLayout.NORTH);
        painelPrincipal.add(scroll, BorderLayout.CENTER);
        painelPrincipal.add(montarPainelInferior(), BorderLayout.SOUTH);
        pack();
    }

    private JPanel montarPainelInferior() {
        JPanel painelInferior = new JPanel(new BorderLayout(0, 10));
        painelInferior.setBackground(ComponentesUi.COR_FUNDO);
        painelInferior.setBorder(BorderFactory.createEmptyBorder(10, 20, 16, 20));

        JPanel painelEdicao = new JPanel(new GridLayout(1, 3, 10, 0));
        painelEdicao.setBackground(ComponentesUi.COR_FUNDO);

        JPanel colunaNome = new JPanel();
        colunaNome.setLayout(new BoxLayout(colunaNome, BoxLayout.Y_AXIS));
        colunaNome.setBackground(ComponentesUi.COR_FUNDO);
        campoNomeEdicao = ComponentesUi.criarCampoTexto();
        colunaNome.add(ComponentesUi.criarRotuloCampo("Nome"));
        colunaNome.add(Box.createVerticalStrut(4));
        colunaNome.add(campoNomeEdicao);

        JPanel colunaPreco = new JPanel();
        colunaPreco.setLayout(new BoxLayout(colunaPreco, BoxLayout.Y_AXIS));
        colunaPreco.setBackground(ComponentesUi.COR_FUNDO);
        campoPrecoEdicao = ComponentesUi.criarCampoTexto();
        colunaPreco.add(ComponentesUi.criarRotuloCampo("Preço (R$)"));
        colunaPreco.add(Box.createVerticalStrut(4));
        colunaPreco.add(campoPrecoEdicao);

        JPanel colunaEstoque = new JPanel();
        colunaEstoque.setLayout(new BoxLayout(colunaEstoque, BoxLayout.Y_AXIS));
        colunaEstoque.setBackground(ComponentesUi.COR_FUNDO);
        campoEstoqueEdicao = ComponentesUi.criarCampoTexto();
        colunaEstoque.add(ComponentesUi.criarRotuloCampo("Estoque"));
        colunaEstoque.add(Box.createVerticalStrut(4));
        colunaEstoque.add(campoEstoqueEdicao);

        painelEdicao.add(colunaNome);
        painelEdicao.add(colunaPreco);
        painelEdicao.add(colunaEstoque);

        JButton botaoAtualizar = ComponentesUi.criarBotaoSecundario("Atualizar");
        ComponentesUi.aoClicar(botaoAtualizar, () -> carregarProdutos(true));

        JButton botaoSalvar = ComponentesUi.criarBotaoPrimario("Salvar", ComponentesUi.VERDE_NEON, Color.WHITE);
        ComponentesUi.aoClicar(botaoSalvar, this::salvarEdicaoSelecionada);

        JButton botaoRemover = ComponentesUi.criarBotaoPrimario("Remover", ComponentesUi.ROSA_ALERTA, Color.WHITE);
        ComponentesUi.aoClicar(botaoRemover, this::removerProdutoSelecionado);

        JPanel rodape = ComponentesUi.montarBarraAcoes(botaoAtualizar, botaoSalvar, botaoRemover);

        painelInferior.add(painelEdicao, BorderLayout.NORTH);
        painelInferior.add(rodape, BorderLayout.SOUTH);
        return painelInferior;
    }

    private void carregarProdutos(boolean mostrarFeedback) {
        modeloTabela.setRowCount(0);
        limparCamposEdicao();
        try {
            produtosExibidos = estoqueController.listarTodos();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (Produto produto : produtosExibidos) {
            modeloTabela.addRow(new Object[]{
                    produto.getId(),
                    produto.getNome(),
                    String.format("%.2f", produto.getPreco()),
                    produto.getQuantidade(),
                    produto.getTipo()
            });
        }

        if (mostrarFeedback) {
            JOptionPane.showMessageDialog(this,
                    produtosExibidos.isEmpty()
                            ? "Lista atualizada — nenhum produto cadastrado."
                            : "Lista atualizada! " + produtosExibidos.size() + " produto(s) encontrado(s).");
        }
    }

    private void preencherCamposComSelecao() {
        int linha = tabelaProdutos.getSelectedRow();
        if (linha == -1) {
            limparCamposEdicao();
            return;
        }
        Produto produto = produtosExibidos.get(linha);
        campoNomeEdicao.setText(produto.getNome());
        campoPrecoEdicao.setText(String.format("%.2f", produto.getPreco()));
        campoEstoqueEdicao.setText(String.valueOf(produto.getQuantidade()));
    }

    private void limparCamposEdicao() {
        campoNomeEdicao.setText("");
        campoPrecoEdicao.setText("");
        campoEstoqueEdicao.setText("");
    }

    private void salvarEdicaoSelecionada() {
        int linha = tabelaProdutos.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela para editar.");
            return;
        }
        Produto produtoOriginal = produtosExibidos.get(linha);
        int id = produtoOriginal.getId();

        StringBuilder mensagens = new StringBuilder();
        boolean algumaFalha = false;

        String novoNome = campoNomeEdicao.getText();
        if (!novoNome.equals(produtoOriginal.getNome())) {
            ResultadoOperacao resultado = estoqueController.atualizarNome(id, novoNome);
            mensagens.append(resultado.getMensagem()).append("\n");
            algumaFalha = algumaFalha || !resultado.isSucesso();
        }

        try {
            double novoPreco = Double.parseDouble(campoPrecoEdicao.getText().replace(",", "."));
            if (novoPreco != produtoOriginal.getPreco()) {
                ResultadoOperacao resultado = estoqueController.atualizarPreco(id, novoPreco);
                mensagens.append(resultado.getMensagem()).append("\n");
                algumaFalha = algumaFalha || !resultado.isSucesso();
            }
        } catch (NumberFormatException e) {
            mensagens.append("Preço inválido — não foi alterado.\n");
            algumaFalha = true;
        }

        try {
            int novaQuantidade = Integer.parseInt(campoEstoqueEdicao.getText());
            if (novaQuantidade != produtoOriginal.getQuantidade()) {
                ResultadoOperacao resultado = estoqueController.atualizarEstoque(id, novaQuantidade);
                mensagens.append(resultado.getMensagem()).append("\n");
                algumaFalha = algumaFalha || !resultado.isSucesso();
            }
        } catch (NumberFormatException e) {
            mensagens.append("Quantidade em estoque inválida — não foi alterada.\n");
            algumaFalha = true;
        }

        if (mensagens.length() == 0) {
            JOptionPane.showMessageDialog(this, "Nenhuma alteração para salvar.");
            return;
        }

        JOptionPane.showMessageDialog(this, mensagens.toString().trim(),
                algumaFalha ? "Algumas alterações não foram aplicadas" : "Sucesso",
                algumaFalha ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
        carregarProdutos(false);
    }

    private void removerProdutoSelecionado() {
        int linhaSelecionada = tabelaProdutos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela.");
            return;
        }
        int id = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
        String nome = (String) modeloTabela.getValueAt(linhaSelecionada, 1);

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Remover o produto '" + nome + "'?", "Confirmar remoção", JOptionPane.YES_NO_OPTION);
        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        ResultadoOperacao resultado = estoqueController.removerProduto(id);
        JOptionPane.showMessageDialog(this, resultado.getMensagem());
        if (resultado.isSucesso()) {
            carregarProdutos(false);
        }
    }
}
