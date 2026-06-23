package org.example.view;

import org.example.controller.UsuarioController;
import org.example.util.AppContext;
import org.example.util.ResultadoOperacao;

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
        setMinimumSize(new Dimension(420, 380));
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(ComponentesUi.COR_FUNDO);
        setContentPane(painelPrincipal);

        JPanel cabecalho = ComponentesUi.criarCabecalho("Cadastro de Usuário", ComponentesUi.ROSA_MAGENTA);

        JPanel painelFormulario = new JPanel();
        painelFormulario.setLayout(new BoxLayout(painelFormulario, BoxLayout.Y_AXIS));
        painelFormulario.setBackground(ComponentesUi.COR_PAINEL);
        painelFormulario.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        campoLogin = ComponentesUi.criarCampoTexto();
        campoSenha = ComponentesUi.criarCampoSenha();
        comboPerfil = ComponentesUi.criarComboBox("ADMIN", "FUNCIONARIO");

        JButton botaoCadastrar = ComponentesUi.criarBotaoPrimario("Cadastrar Usuário", ComponentesUi.VERDE_NEON, Color.WHITE);
        botaoCadastrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        ComponentesUi.aoClicar(botaoCadastrar, this::cadastrar);

        painelFormulario.add(ComponentesUi.criarRotuloCampo("Login"));
        painelFormulario.add(Box.createVerticalStrut(4));
        painelFormulario.add(campoLogin);
        painelFormulario.add(Box.createVerticalStrut(14));
        painelFormulario.add(ComponentesUi.criarRotuloCampo("Senha"));
        painelFormulario.add(Box.createVerticalStrut(4));
        painelFormulario.add(campoSenha);
        painelFormulario.add(Box.createVerticalStrut(14));
        painelFormulario.add(ComponentesUi.criarRotuloCampo("Perfil de Acesso"));
        painelFormulario.add(Box.createVerticalStrut(4));
        painelFormulario.add(comboPerfil);
        painelFormulario.add(Box.createVerticalStrut(24));
        painelFormulario.add(botaoCadastrar);

        painelPrincipal.add(cabecalho, BorderLayout.NORTH);
        painelPrincipal.add(painelFormulario, BorderLayout.CENTER);
        pack();
    }

    private void cadastrar() {
        String login = campoLogin.getText();
        String senha = new String(campoSenha.getPassword());
        String perfil = (String) comboPerfil.getSelectedItem();

        ResultadoOperacao resultado = usuarioController.cadastrarUsuario(login, senha, perfil);

        if (!resultado.isSucesso()) {
            JOptionPane.showMessageDialog(this, resultado.getMensagem(), "Não foi possível cadastrar", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, resultado.getMensagem());
        campoLogin.setText("");
        campoSenha.setText("");
    }
}
