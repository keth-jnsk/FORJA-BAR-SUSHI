package org.example.view;

import org.example.controller.EstoqueController;
import org.example.util.AppContext;
import org.example.util.ResultadoOperacao;
import org.example.util.ErroUtil;

import javax.swing.*;
import java.awt.*;

public class EstoqueFrame extends JFrame {

    private final EstoqueController estoqueController;

    private JTextField campoNome;
    private JTextField campoQuantidade;
    private JTextField campoPreco;
    private JComboBox<String> comboTipo;

    public EstoqueFrame() {
        this.estoqueController = AppContext.estoqueController();
        montarTela();
    }

    private void montarTela() {
        setTitle("Forja Bar — Cadastrar Novo Produto");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(440, 460));
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(ComponentesUi.COR_FUNDO);
        setContentPane(painelPrincipal);

        JPanel cabecalho = ComponentesUi.criarCabecalho("Cadastrar Novo Produto", ComponentesUi.VERDE_NEON);

        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBackground(ComponentesUi.COR_PAINEL);
        painelFormulario.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        campoNome = ComponentesUi.criarCampoTexto();
        campoQuantidade = ComponentesUi.criarCampoNumericoInteiro();
        campoPreco = ComponentesUi.criarCampoNumericoDecimal();
        comboTipo = ComponentesUi.criarComboBox("BEBIDA", "COMIDAS");

        JButton botaoVoltar = ComponentesUi.criarBotaoSecundario("Voltar ao Menu");
        botaoVoltar.addActionListener(e -> dispose());

        JButton botaoSalvar = ComponentesUi.criarBotaoPrimario("Salvar Produto", ComponentesUi.VERDE_NEON, Color.WHITE);
        ComponentesUi.aoClicar(botaoSalvar, this::salvarProduto);

        JPanel painelBotoes = ComponentesUi.montarBarraAcoes(botaoVoltar, botaoSalvar);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.WEST;

        int linha = 0;
        c.gridy = linha++; c.insets = new Insets(0, 0, 4, 0); painelFormulario.add(ComponentesUi.criarRotuloCampo("Nome do Produto"), c);
        c.gridy = linha++; c.insets = new Insets(0, 0, 16, 0); painelFormulario.add(campoNome, c);

        c.gridy = linha++; c.insets = new Insets(0, 0, 4, 0); painelFormulario.add(ComponentesUi.criarRotuloCampo("Quantidade em Estoque"), c);
        c.gridy = linha++; c.insets = new Insets(0, 0, 16, 0); painelFormulario.add(campoQuantidade, c);

        c.gridy = linha++; c.insets = new Insets(0, 0, 4, 0); painelFormulario.add(ComponentesUi.criarRotuloCampo("Preço (R$)"), c);
        c.gridy = linha++; c.insets = new Insets(0, 0, 16, 0); painelFormulario.add(campoPreco, c);

        c.gridy = linha++; c.insets = new Insets(0, 0, 4, 0); painelFormulario.add(ComponentesUi.criarRotuloCampo("Tipo do Produto"), c);
        c.gridy = linha++; c.insets = new Insets(0, 0, 26, 0); painelFormulario.add(comboTipo, c);

        c.gridy = linha++; c.insets = new Insets(0, 0, 0, 0); c.weighty = 1.0; c.anchor = GridBagConstraints.SOUTH;
        painelFormulario.add(painelBotoes, c);

        painelPrincipal.add(cabecalho, BorderLayout.NORTH);
        painelPrincipal.add(painelFormulario, BorderLayout.CENTER);
        pack();
    }

    private void salvarProduto() {
        String nome = campoNome.getText();
        String quantidadeTexto = campoQuantidade.getText();
        String precoTexto = campoPreco.getText();
        String tipo = (String) comboTipo.getSelectedItem();

        try {
            ResultadoOperacao resultado = estoqueController.cadastrarProduto(nome, precoTexto, quantidadeTexto, tipo);

            if (!resultado.isSucesso()) {
                JOptionPane.showMessageDialog(this, resultado.getMensagem(), "Não foi possível salvar", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this, resultado.getMensagem());
            campoNome.setText("");
            campoQuantidade.setText("");
            campoPreco.setText("");
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar produto: " + ErroUtil.causaRaiz(e), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
