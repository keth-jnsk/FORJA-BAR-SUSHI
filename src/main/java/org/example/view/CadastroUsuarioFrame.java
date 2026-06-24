package org.example.view;

import org.example.controller.UsuarioController;
import org.example.util.AppContext;
import org.example.util.ResultadoOperacao;
import org.example.util.ErroUtil;

import javax.swing.*;
import java.awt.*;

public class CadastroUsuarioFrame extends JFrame {

    private final UsuarioController usuarioController;

    private JTextField campoLogin;
    private JPasswordField campoSenha;
    private JComboBox<String> comboPerfil;

    public CadastroUsuarioFrame() {
        this.usuarioController = AppContext.usuarioController();
        montarTela();
    }

    private void montarTela() {
        setTitle("Forja Bar — Cadastro de Usuário");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(440, 420));
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(ComponentesUi.COR_FUNDO);
        setContentPane(painelPrincipal);

        JPanel cabecalho = ComponentesUi.criarCabecalho("Cadastro de Usuário", ComponentesUi.ROSA_MAGENTA);

        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBackground(ComponentesUi.COR_PAINEL);
        painelFormulario.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        campoLogin = ComponentesUi.criarCampoTexto();
        campoSenha = ComponentesUi.criarCampoSenha();
        comboPerfil = ComponentesUi.criarComboBox("ADMIN", "FUNCIONARIO");

        JButton botaoCadastrar = ComponentesUi.criarBotaoPrimario("Cadastrar Usuário", ComponentesUi.VERDE_NEON, Color.WHITE);
        ComponentesUi.aoClicar(botaoCadastrar, this::cadastrar);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.WEST;

        int linha = 0;
        c.gridy = linha++; c.insets = new Insets(0, 0, 4, 0); painelFormulario.add(ComponentesUi.criarRotuloCampo("Login"), c);
        c.gridy = linha++; c.insets = new Insets(0, 0, 16, 0); painelFormulario.add(campoLogin, c);

        c.gridy = linha++; c.insets = new Insets(0, 0, 4, 0); painelFormulario.add(ComponentesUi.criarRotuloCampo("Senha"), c);
        c.gridy = linha++; c.insets = new Insets(0, 0, 16, 0); painelFormulario.add(campoSenha, c);

        c.gridy = linha++; c.insets = new Insets(0, 0, 4, 0); painelFormulario.add(ComponentesUi.criarRotuloCampo("Perfil de Acesso"), c);
        c.gridy = linha++; c.insets = new Insets(0, 0, 26, 0); painelFormulario.add(comboPerfil, c);

        c.gridy = linha++; c.insets = new Insets(0, 0, 0, 0); c.weighty = 1.0; c.anchor = GridBagConstraints.SOUTH;
        painelFormulario.add(botaoCadastrar, c);

        painelPrincipal.add(cabecalho, BorderLayout.NORTH);
        painelPrincipal.add(painelFormulario, BorderLayout.CENTER);
        pack();
    }

    private void cadastrar() {
        String login = campoLogin.getText();
        String senha = new String(campoSenha.getPassword());
        String perfil = (String) comboPerfil.getSelectedItem();

        try {
            ResultadoOperacao resultado = usuarioController.cadastrarUsuario(login, senha, perfil);

            if (!resultado.isSucesso()) {
                JOptionPane.showMessageDialog(this, resultado.getMensagem(), "Não foi possível cadastrar", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this, resultado.getMensagem());
            campoLogin.setText("");
            campoSenha.setText("");
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar usuário: " + ErroUtil.causaRaiz(e), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
