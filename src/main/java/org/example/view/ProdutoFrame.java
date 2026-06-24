package org.example.view;

import org.example.controller.EstoqueController;
import org.example.model.Produto;
import org.example.util.AppContext;
import org.example.util.ResultadoOperacao;
import org.example.util.ErroUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Tela de gerenciamento e visualização de estoque. Lista os produtos
 * (sempre ordenados por ID, do menor para o maior — pedido pelo Repository),
 * permite buscar por nome ou ID, e abre um diálogo (modal) para editar
 * nome/preço/estoque do produto selecionado.
 */
public class ProdutoFrame extends JFrame {

    private final EstoqueController estoqueController;
    private DefaultTableModel modeloTabela;
    private JTable tabelaProdutos;
    private JTextField campoBusca;
    private List<Produto> produtosTodos;
    private List<Produto> produtosExibidos;

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

        JScrollPane scroll = new JScrollPane(tabelaProdutos);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(ComponentesUi.COR_PAINEL);

        JPanel centro = new JPanel(new BorderLayout(0, 8));
        centro.setBackground(ComponentesUi.COR_FUNDO);
        centro.setBorder(BorderFactory.createEmptyBorder(12, 20, 0, 20));
        JLabel rotuloBusca = ComponentesUi.criarRotuloCampo("Buscar por nome ou ID");
        rotuloBusca.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
        JPanel painelBuscaComRotulo = new JPanel(new BorderLayout());
        painelBuscaComRotulo.setOpaque(false);
        painelBuscaComRotulo.add(rotuloBusca, BorderLayout.NORTH);
        painelBuscaComRotulo.add(montarBarraBusca(), BorderLayout.CENTER);
        centro.add(painelBuscaComRotulo, BorderLayout.NORTH);
        centro.add(scroll, BorderLayout.CENTER);

        painelPrincipal.add(cabecalho, BorderLayout.NORTH);
        painelPrincipal.add(centro, BorderLayout.CENTER);
        painelPrincipal.add(montarRodape(), BorderLayout.SOUTH);
        pack();
    }

    private JPanel montarBarraBusca() {
        JPanel barra = new JPanel(new BorderLayout(8, 0));
        barra.setOpaque(false);

        JComponent lupa = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ComponentesUi.COR_TEXTO_ROTULO);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(3, 3, 12, 12);
                g2.drawLine(14, 14, 19, 19);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(22, 22);
            }
        };

        campoBusca = ComponentesUi.criarCampoTexto();
        campoBusca.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                aplicarFiltro();
            }
        });

        JButton botaoLimpar = ComponentesUi.criarBotaoSecundario("Limpar");
        botaoLimpar.addActionListener(e -> {
            campoBusca.setText("");
            aplicarFiltro();
        });
        botaoLimpar.setMaximumSize(new Dimension(110, 38));
        botaoLimpar.setPreferredSize(new Dimension(110, 38));

        JPanel ladoDireito = new JPanel(new BorderLayout(8, 0));
        ladoDireito.setOpaque(false);
        ladoDireito.add(campoBusca, BorderLayout.CENTER);
        ladoDireito.add(botaoLimpar, BorderLayout.EAST);

        barra.add(lupa, BorderLayout.WEST);
        barra.add(ladoDireito, BorderLayout.CENTER);
        return barra;
    }

    private JPanel montarRodape() {
        JButton botaoAtualizar = ComponentesUi.criarBotaoSecundario("Atualizar Lista");
        ComponentesUi.aoClicar(botaoAtualizar, () -> carregarProdutos(true));

        JButton botaoEditar = ComponentesUi.criarBotaoPrimario("Atualizar Estoque", ComponentesUi.VERDE_NEON, Color.WHITE);
        ComponentesUi.aoClicar(botaoEditar, this::abrirModalEdicao);

        JButton botaoRemover = ComponentesUi.criarBotaoPrimario("Remover", ComponentesUi.ROSA_ALERTA, Color.WHITE);
        ComponentesUi.aoClicar(botaoRemover, this::removerProdutoSelecionado);

        JPanel rodape = ComponentesUi.montarBarraAcoes(botaoAtualizar, botaoEditar, botaoRemover);
        rodape.setBorder(BorderFactory.createEmptyBorder(10, 20, 16, 20));
        return rodape;
    }

    private void carregarProdutos(boolean mostrarFeedback) {
        try {
            produtosTodos = estoqueController.listarTodos();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + ErroUtil.causaRaiz(e), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        aplicarFiltro();

        if (mostrarFeedback) {
            JOptionPane.showMessageDialog(this,
                    produtosTodos.isEmpty()
                            ? "Lista atualizada — nenhum produto cadastrado."
                            : "Lista atualizada! " + produtosTodos.size() + " produto(s) encontrado(s).");
        }
    }

    private void aplicarFiltro() {
        String termo = campoBusca.getText() == null ? "" : campoBusca.getText().trim();
        modeloTabela.setRowCount(0);

        if (termo.isEmpty()) {
            produtosExibidos = produtosTodos;
        } else {
            String termoMinusculo = termo.toLowerCase();
            Integer idBuscado = null;
            try {
                idBuscado = Integer.parseInt(termo);
            } catch (NumberFormatException ignored) {
                // não é um ID numérico — busca só por nome
            }
            final Integer idFiltro = idBuscado;
            produtosExibidos = produtosTodos.stream()
                    .filter(p -> p.getNome().toLowerCase().contains(termoMinusculo) || (idFiltro != null && p.getId() == idFiltro))
                    .toList();
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
    }

    private void abrirModalEdicao() {
        int linha = tabelaProdutos.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela para atualizar.");
            return;
        }
        Produto produto = produtosExibidos.get(linha);
        new ModalEdicaoProduto(produto).setVisible(true);
    }

    private void removerProdutoSelecionado() {
        int linhaSelecionada = tabelaProdutos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela.");
            return;
        }
        Produto produto = produtosExibidos.get(linhaSelecionada);

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Remover o produto '" + produto.getNome() + "'?", "Confirmar remoção", JOptionPane.YES_NO_OPTION);
        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            ResultadoOperacao resultado = estoqueController.removerProduto(produto.getId());
            JOptionPane.showMessageDialog(this, resultado.getMensagem());
            if (resultado.isSucesso()) {
                carregarProdutos(false);
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Erro ao remover produto: " + ErroUtil.causaRaiz(e), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Diálogo modal de edição de nome/preço/estoque, aberto a partir do botão "Atualizar Estoque". */
    private class ModalEdicaoProduto extends JDialog {

        private final Produto produtoOriginal;
        private final JTextField campoNome;
        private final JTextField campoPreco;
        private final JTextField campoEstoque;

        ModalEdicaoProduto(Produto produto) {
            super(ProdutoFrame.this, "Atualizar Estoque — " + produto.getNome(), true);
            this.produtoOriginal = produto;

            JPanel painel = new JPanel(new GridBagLayout());
            painel.setBackground(ComponentesUi.COR_PAINEL);
            painel.setBorder(BorderFactory.createEmptyBorder(24, 28, 20, 28));
            setContentPane(painel);

            campoNome = ComponentesUi.criarCampoTexto();
            campoNome.setText(produto.getNome());
            campoPreco = ComponentesUi.criarCampoNumericoDecimal();
            campoPreco.setText(String.format("%.2f", produto.getPreco()));
            campoEstoque = ComponentesUi.criarCampoNumericoInteiro();
            campoEstoque.setText(String.valueOf(produto.getQuantidade()));

            JButton botaoCancelar = ComponentesUi.criarBotaoSecundario("Cancelar");
            botaoCancelar.addActionListener(e -> dispose());

            JButton botaoSalvar = ComponentesUi.criarBotaoPrimario("Salvar", ComponentesUi.VERDE_NEON, Color.WHITE);
            ComponentesUi.aoClicar(botaoSalvar, this::salvar);

            JPanel botoes = ComponentesUi.montarBarraAcoes(botaoCancelar, botaoSalvar);

            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            c.anchor = GridBagConstraints.WEST;

            int linha = 0;
            c.gridy = linha++; c.insets = new Insets(0, 0, 4, 0); painel.add(ComponentesUi.criarRotuloCampo("Nome"), c);
            c.gridy = linha++; c.insets = new Insets(0, 0, 14, 0); painel.add(campoNome, c);

            c.gridy = linha++; c.insets = new Insets(0, 0, 4, 0); painel.add(ComponentesUi.criarRotuloCampo("Preço (R$)"), c);
            c.gridy = linha++; c.insets = new Insets(0, 0, 14, 0); painel.add(campoPreco, c);

            c.gridy = linha++; c.insets = new Insets(0, 0, 4, 0); painel.add(ComponentesUi.criarRotuloCampo("Estoque"), c);
            c.gridy = linha++; c.insets = new Insets(0, 0, 22, 0); painel.add(campoEstoque, c);

            c.gridy = linha++; c.insets = new Insets(0, 0, 0, 0); c.weighty = 1.0; c.anchor = GridBagConstraints.SOUTH;
            painel.add(botoes, c);

            setMinimumSize(new Dimension(400, 360));
            setLocationRelativeTo(ProdutoFrame.this);
            pack();
        }

        private void salvar() {
            StringBuilder mensagens = new StringBuilder();
            boolean algumaFalha = false;

            try {
                String novoNome = campoNome.getText();
                if (!novoNome.equals(produtoOriginal.getNome())) {
                    ResultadoOperacao resultado = estoqueController.atualizarNome(produtoOriginal.getId(), novoNome);
                    mensagens.append(resultado.getMensagem()).append("\n");
                    algumaFalha = algumaFalha || !resultado.isSucesso();
                }

                try {
                    double novoPreco = Double.parseDouble(campoPreco.getText().replace(",", "."));
                    if (novoPreco != produtoOriginal.getPreco()) {
                        ResultadoOperacao resultado = estoqueController.atualizarPreco(produtoOriginal.getId(), novoPreco);
                        mensagens.append(resultado.getMensagem()).append("\n");
                        algumaFalha = algumaFalha || !resultado.isSucesso();
                    }
                } catch (NumberFormatException e) {
                    mensagens.append("Preço inválido — não foi alterado.\n");
                    algumaFalha = true;
                }

                try {
                    int novaQuantidade = Integer.parseInt(campoEstoque.getText());
                    if (novaQuantidade != produtoOriginal.getQuantidade()) {
                        ResultadoOperacao resultado = estoqueController.atualizarEstoque(produtoOriginal.getId(), novaQuantidade);
                        mensagens.append(resultado.getMensagem()).append("\n");
                        algumaFalha = algumaFalha || !resultado.isSucesso();
                    }
                } catch (NumberFormatException e) {
                    mensagens.append("Quantidade em estoque inválida — não foi alterada.\n");
                    algumaFalha = true;
                }
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar produto: " + ErroUtil.causaRaiz(e), "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (mensagens.length() == 0) {
                JOptionPane.showMessageDialog(this, "Nenhuma alteração para salvar.");
                return;
            }

            JOptionPane.showMessageDialog(this, mensagens.toString().trim(),
                    algumaFalha ? "Algumas alterações não foram aplicadas" : "Sucesso",
                    algumaFalha ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);

            if (!algumaFalha) {
                dispose();
            }
            carregarProdutos(false); // atualiza a lista no fundo independentemente
        }
    }
}
