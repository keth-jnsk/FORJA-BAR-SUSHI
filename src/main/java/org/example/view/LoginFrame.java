package org.example.view;

import org.example.controller.LoginController;
import org.example.model.Usuario;
import org.example.util.AppContext;

import javax.swing.*;
import java.awt.*;

/**
 * View de login. Não acessa AuthService nem UsuarioRepository diretamente —
 * toda a lógica passa pelo LoginController.
 */
public class LoginFrame extends JFrame {

    private final LoginController loginController;

    private JTextField campoLogin;
    private JPasswordField campoSenha;

    public LoginFrame() {
        this.loginController = AppContext.loginController();
        montarTela();
    }

    private void montarTela() {
        setTitle("Forja Bar — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(440, 480));
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new GridBagLayout());
        painelPrincipal.setBackground(ComponentesUi.COR_FUNDO);
        setContentPane(painelPrincipal);

        JPanel cartao = new JPanel(new GridBagLayout());
        cartao.setBackground(ComponentesUi.COR_PAINEL);
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ComponentesUi.ROSA_NEON, 1, true),
                BorderFactory.createEmptyBorder(32, 36, 28, 36)));
        cartao.setPreferredSize(new Dimension(360, 420));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.insets = new Insets(0, 0, 0, 0);

        JPanel logo = ComponentesUi.criarLogo(60);

        JLabel rotuloSubtitulo = new JLabel("Acesse o sistema", SwingConstants.CENTER);
        rotuloSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rotuloSubtitulo.setForeground(ComponentesUi.COR_TEXTO_ROTULO);

        campoLogin = ComponentesUi.criarCampoTexto();
        campoLogin.setPreferredSize(new Dimension(260, 38));
        campoSenha = ComponentesUi.criarCampoSenha();
        campoSenha.setPreferredSize(new Dimension(260, 38));

        JButton botaoEntrar = ComponentesUi.criarBotaoPrimario("Entrar", ComponentesUi.ROSA_NEON, Color.WHITE);
        botaoEntrar.setPreferredSize(new Dimension(260, 42));
        ComponentesUi.aoClicar(botaoEntrar, this::tentarLogin);
        campoSenha.addActionListener(e -> tentarLogin());

        JButton botaoTema = new JButton(ComponentesUi.modoClaro ? "Tema escuro" : "Tema claro");
        botaoTema.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        botaoTema.setForeground(ComponentesUi.COR_TEXTO_ROTULO);
        botaoTema.setContentAreaFilled(false);
        botaoTema.setBorderPainted(false);
        botaoTema.setFocusPainted(false);
        botaoTema.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botaoTema.setHorizontalAlignment(SwingConstants.CENTER);
        botaoTema.addActionListener(e -> {
            ComponentesUi.alternarTema();
            new LoginFrame().setVisible(true);
            dispose();
        });

        int linha = 0;
        c.gridy = linha++; c.insets = new Insets(0, 0, 4, 0); cartao.add(logo, c);
        c.gridy = linha++; c.insets = new Insets(2, 0, 22, 0); cartao.add(rotuloSubtitulo, c);
        c.gridy = linha++; c.insets = new Insets(0, 0, 4, 0); c.fill = GridBagConstraints.NONE; c.anchor = GridBagConstraints.WEST; cartao.add(ComponentesUi.criarRotuloCampo("Login"), c);
        c.gridy = linha++; c.insets = new Insets(0, 0, 16, 0); c.fill = GridBagConstraints.HORIZONTAL; c.anchor = GridBagConstraints.CENTER; cartao.add(campoLogin, c);
        c.gridy = linha++; c.insets = new Insets(0, 0, 4, 0); c.fill = GridBagConstraints.NONE; c.anchor = GridBagConstraints.WEST; cartao.add(ComponentesUi.criarRotuloCampo("Senha"), c);
        c.gridy = linha++; c.insets = new Insets(0, 0, 26, 0); c.fill = GridBagConstraints.HORIZONTAL; c.anchor = GridBagConstraints.CENTER; cartao.add(campoSenha, c);
        c.gridy = linha++; c.insets = new Insets(0, 0, 12, 0); cartao.add(botaoEntrar, c);
        c.gridy = linha++; c.insets = new Insets(0, 0, 0, 0); cartao.add(botaoTema, c);

        GridBagConstraints centro = new GridBagConstraints();
        painelPrincipal.add(cartao, centro);

        pack();
        setLocationRelativeTo(null);
    }

    private void tentarLogin() {
        String login = campoLogin.getText();
        String senha = new String(campoSenha.getPassword());

        LoginController.ResultadoLogin resultado = loginController.autenticar(login, senha);

        if (!resultado.isSucesso()) {
            JOptionPane.showMessageDialog(this, resultado.getMensagemErro(), "Erro ao entrar", JOptionPane.ERROR_MESSAGE);
            campoSenha.setText("");
            return;
        }

        Usuario usuario = resultado.getUsuario();
        JOptionPane.showMessageDialog(this, "Bem-vindo, " + usuario.getLogin() + "!");
        new MainFrame(usuario).setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
