package org.example.view;

import org.example.controller.UsuarioController;
import org.example.model.Perfil;
import org.example.model.Usuario;
import org.example.util.AppContext;
import org.example.util.ResultadoOperacao;
import org.example.util.ErroUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Lista todos os usuários do sistema (sem nunca exibir a senha). Qualquer
 * usuário autenticado pode visualizar; editar, excluir e redefinir senha
 * são ações restritas ao perfil ADMIN (os botões ficam desabilitados para
 * quem não é admin, em vez de simplesmente escondidos, para deixar claro
 * que a função existe mas exige outro perfil de acesso).
 */
public class UsuarioListaFrame extends JFrame {

    private final UsuarioController usuarioController;
    private final Usuario usuarioLogado;

    private DefaultTableModel modeloTabela;
    private JTable tabela;
    private List<Usuario> usuariosExibidos;

    public UsuarioListaFrame(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        this.usuarioController = AppContext.usuarioController();
        montarTela();
        carregarUsuarios();
    }

    private boolean isAdmin() {
        return usuarioLogado.getPerfil() == Perfil.ADMIN;
    }

    private void montarTela() {
        setTitle("Forja Bar — Usuários");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(680, 520));
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(ComponentesUi.COR_FUNDO);
        setContentPane(painelPrincipal);

        JPanel cabecalho = ComponentesUi.criarCabecalho("Usuários", ComponentesUi.ROSA_MAGENTA);

        String[] colunas = {"ID", "Login", "Perfil"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int linha, int coluna) { return false; }
        };
        tabela = ComponentesUi.criarTabelaEstilizada(modeloTabela, ComponentesUi.ROSA_MAGENTA);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(ComponentesUi.COR_PAINEL);

        JLabel avisoPermissao = new JLabel(isAdmin()
                ? "Como administrador, você pode editar, excluir e redefinir senhas."
                : "Apenas administradores podem editar, excluir ou redefinir senhas.");
        avisoPermissao.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        avisoPermissao.setForeground(ComponentesUi.COR_TEXTO_ROTULO);
        avisoPermissao.setBorder(BorderFactory.createEmptyBorder(8, 20, 0, 20));

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(ComponentesUi.COR_FUNDO);
        centro.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        centro.add(avisoPermissao, BorderLayout.NORTH);
        centro.add(scroll, BorderLayout.CENTER);

        JButton botaoNovo = ComponentesUi.criarBotaoPrimario("Novo Usuário", ComponentesUi.VERDE_NEON, Color.WHITE);
        botaoNovo.addActionListener(e -> {
            new CadastroUsuarioFrame().setVisible(true);
        });
        botaoNovo.setEnabled(isAdmin());

        JButton botaoEditar = ComponentesUi.criarBotaoSecundario("Editar");
        ComponentesUi.aoClicar(botaoEditar, this::editarSelecionado);
        botaoEditar.setEnabled(isAdmin());

        JButton botaoRedefinirSenha = ComponentesUi.criarBotaoSecundario("Redefinir Senha");
        ComponentesUi.aoClicar(botaoRedefinirSenha, this::redefinirSenhaSelecionado);
        botaoRedefinirSenha.setEnabled(isAdmin());

        JButton botaoExcluir = ComponentesUi.criarBotaoPrimario("Excluir", ComponentesUi.ROSA_ALERTA, Color.WHITE);
        ComponentesUi.aoClicar(botaoExcluir, this::excluirSelecionado);
        botaoExcluir.setEnabled(isAdmin());

        JButton botaoAtualizar = ComponentesUi.criarBotaoSecundario("Atualizar");
        ComponentesUi.aoClicar(botaoAtualizar, this::carregarUsuarios);

        JPanel rodape = ComponentesUi.montarBarraAcoes(botaoAtualizar, botaoNovo, botaoEditar, botaoRedefinirSenha, botaoExcluir);
        rodape.setBorder(BorderFactory.createEmptyBorder(10, 20, 16, 20));

        painelPrincipal.add(cabecalho, BorderLayout.NORTH);
        painelPrincipal.add(centro, BorderLayout.CENTER);
        painelPrincipal.add(rodape, BorderLayout.SOUTH);
        pack();
    }

    private void carregarUsuarios() {
        modeloTabela.setRowCount(0);
        try {
            usuariosExibidos = usuarioController.listarTodos();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar usuários: " + ErroUtil.causaRaiz(e), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        for (Usuario usuario : usuariosExibidos) {
            modeloTabela.addRow(new Object[]{usuario.getId(), usuario.getLogin(), usuario.getPerfil()});
        }
    }

    private Usuario obterSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário na tabela.");
            return null;
        }
        return usuariosExibidos.get(linha);
    }

    private void editarSelecionado() {
        Usuario usuario = obterSelecionado();
        if (usuario == null) {
            return;
        }

        JTextField campoLogin = ComponentesUi.criarCampoTexto();
        campoLogin.setText(usuario.getLogin());
        JComboBox<String> comboPerfil = ComponentesUi.criarComboBox("ADMIN", "FUNCIONARIO");
        comboPerfil.setSelectedItem(usuario.getPerfil().name());

        JPanel painel = new JPanel(new GridLayout(2, 2, 8, 10));
        painel.add(new JLabel("Login:"));
        painel.add(campoLogin);
        painel.add(new JLabel("Perfil:"));
        painel.add(comboPerfil);

        int opcao = JOptionPane.showConfirmDialog(this, painel, "Editar Usuário", JOptionPane.OK_CANCEL_OPTION);
        if (opcao != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            ResultadoOperacao resultado = usuarioController.editarUsuario(
                    usuarioLogado.getPerfil(), usuario.getId(), campoLogin.getText(), (String) comboPerfil.getSelectedItem());
            JOptionPane.showMessageDialog(this, resultado.getMensagem());
            if (resultado.isSucesso()) {
                carregarUsuarios();
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Erro ao editar usuário: " + ErroUtil.causaRaiz(e), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void redefinirSenhaSelecionado() {
        Usuario usuario = obterSelecionado();
        if (usuario == null) {
            return;
        }

        JPasswordField campoNovaSenha = ComponentesUi.criarCampoSenha();
        JPanel painel = new JPanel(new GridLayout(2, 1, 4, 8));
        painel.add(new JLabel("Nova senha provisória para '" + usuario.getLogin() + "':"));
        painel.add(campoNovaSenha);

        int opcao = JOptionPane.showConfirmDialog(this, painel, "Redefinir Senha", JOptionPane.OK_CANCEL_OPTION);
        if (opcao != JOptionPane.OK_OPTION) {
            return;
        }

        String novaSenha = new String(campoNovaSenha.getPassword());
        try {
            ResultadoOperacao resultado = usuarioController.redefinirSenha(usuarioLogado.getPerfil(), usuario.getId(), novaSenha);
            JOptionPane.showMessageDialog(this, resultado.getMensagem());
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Erro ao redefinir senha: " + ErroUtil.causaRaiz(e), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirSelecionado() {
        Usuario usuario = obterSelecionado();
        if (usuario == null) {
            return;
        }
        if (usuario.getId() == usuarioLogado.getId()) {
            JOptionPane.showMessageDialog(this, "Você não pode excluir o próprio usuário enquanto está logado nele.", "Ação não permitida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Excluir o usuário '" + usuario.getLogin() + "'? Esta ação não pode ser desfeita.",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            ResultadoOperacao resultado = usuarioController.excluirUsuario(usuarioLogado.getPerfil(), usuario.getId());
            JOptionPane.showMessageDialog(this, resultado.getMensagem());
            if (resultado.isSucesso()) {
                carregarUsuarios();
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir usuário: " + ErroUtil.causaRaiz(e), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
