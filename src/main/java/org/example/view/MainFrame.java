package org.example.view;

import org.example.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Menu principal com layout de painel lateral fixo (sidebar) + área de
 * conteúdo — padrão comum em sistemas de gestão profissionais. Evita o
 * problema de uma grade de botões que cresce desproporcionalmente ao
 * maximizar a janela: a barra lateral tem largura fixa, só a área de
 * conteúdo se expande.
 */
public class MainFrame extends JFrame {

    private static final int LARGURA_SIDEBAR = 240;

    private final Usuario usuarioLogado;

    public MainFrame(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        montarTela();
    }

    private void montarTela() {
        setTitle("Forja Bar — Sistema de Gestão");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(880, 560));
        setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(ComponentesUi.COR_FUNDO);
        setContentPane(raiz);

        raiz.add(montarSidebar(), BorderLayout.WEST);
        raiz.add(montarConteudo(), BorderLayout.CENTER);

        setSize(960, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private JPanel montarSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(ComponentesUi.COR_PAINEL);
        sidebar.setPreferredSize(new Dimension(LARGURA_SIDEBAR, 10));
        sidebar.setMinimumSize(new Dimension(LARGURA_SIDEBAR, 10));
        sidebar.setMaximumSize(new Dimension(LARGURA_SIDEBAR, Integer.MAX_VALUE));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ComponentesUi.COR_GRADE));

        JPanel topo = new JPanel();
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        topo.setOpaque(false);
        topo.setBorder(BorderFactory.createEmptyBorder(24, 0, 18, 0));
        JPanel logo = ComponentesUi.criarLogo(170);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        topo.add(logo);
        topo.setAlignmentX(Component.LEFT_ALIGNMENT);
        topo.setMaximumSize(new Dimension(LARGURA_SIDEBAR, 130));

        JSeparator separador = new JSeparator();
        separador.setForeground(ComponentesUi.COR_GRADE);
        separador.setMaximumSize(new Dimension(LARGURA_SIDEBAR, 1));

        JPanel navegacao = new JPanel();
        navegacao.setLayout(new BoxLayout(navegacao, BoxLayout.Y_AXIS));
        navegacao.setOpaque(false);
        navegacao.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        navegacao.setAlignmentX(Component.LEFT_ALIGNMENT);

        navegacao.add(itemNavegacao("Novo Pedido", ComponentesUi.ROSA_NEON, e -> abrirNovoPedido()));
        navegacao.add(itemNavegacao("Fichas", ComponentesUi.ROSA_PROFUNDO, e -> new FichaFrame().setVisible(true)));
        navegacao.add(itemNavegacao("Estoque", ComponentesUi.ROSA_CLARO, e -> new ProdutoFrame().setVisible(true)));
        navegacao.add(itemNavegacao("Novo Produto", ComponentesUi.ROSA_FUCSIA, e -> new EstoqueFrame().setVisible(true)));
        navegacao.add(itemNavegacao("Relatórios", ComponentesUi.ROSA_SALMAO, e -> new RelatorioFrame().setVisible(true)));
        navegacao.add(itemNavegacao("Usuários", ComponentesUi.ROSA_MAGENTA, e -> new UsuarioListaFrame(usuarioLogado).setVisible(true)));

        JPanel rodapeSidebar = new JPanel();
        rodapeSidebar.setLayout(new BoxLayout(rodapeSidebar, BoxLayout.Y_AXIS));
        rodapeSidebar.setOpaque(false);
        rodapeSidebar.setAlignmentX(Component.LEFT_ALIGNMENT);
        rodapeSidebar.setBorder(BorderFactory.createEmptyBorder(0, 22, 18, 18));

        JLabel rotuloUsuario = new JLabel(usuarioLogado.getLogin() + " · " + usuarioLogado.getPerfil());
        rotuloUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rotuloUsuario.setForeground(ComponentesUi.COR_TEXTO_ROTULO);
        rotuloUsuario.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton botaoTema = botaoSidebarSecundario(ComponentesUi.modoClaro ? "Tema escuro" : "Tema claro");
        botaoTema.addActionListener(e -> {
            ComponentesUi.alternarTema();
            new MainFrame(usuarioLogado).setVisible(true);
            dispose();
        });

        JButton botaoSair = botaoSidebarSecundario("Sair do sistema");
        botaoSair.addActionListener(e -> System.exit(0));

        rodapeSidebar.add(rotuloUsuario);
        rodapeSidebar.add(Box.createVerticalStrut(10));
        rodapeSidebar.add(botaoTema);
        rodapeSidebar.add(Box.createVerticalStrut(4));
        rodapeSidebar.add(botaoSair);

        sidebar.add(topo);
        sidebar.add(separador);
        sidebar.add(navegacao);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(rodapeSidebar);
        return sidebar;
    }

    private JButton itemNavegacao(String texto, Color cor, java.awt.event.ActionListener acao) {
        JButton botao = ComponentesUi.criarItemMenuLateral(texto, cor);
        botao.setAlignmentX(Component.LEFT_ALIGNMENT);
        botao.addActionListener(acao);
        return botao;
    }

    private JButton botaoSidebarSecundario(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        botao.setForeground(ComponentesUi.COR_TEXTO_ROTULO);
        botao.setContentAreaFilled(false);
        botao.setBorderPainted(false);
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setHorizontalAlignment(SwingConstants.LEFT);
        botao.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        botao.setAlignmentX(Component.LEFT_ALIGNMENT);
        return botao;
    }

    private JPanel montarConteudo() {
        JPanel conteudo = new JPanel(new GridBagLayout());
        conteudo.setBackground(ComponentesUi.COR_FUNDO);

        JPanel bloco = new JPanel();
        bloco.setLayout(new BoxLayout(bloco, BoxLayout.Y_AXIS));
        bloco.setOpaque(false);

        String saudacao = saudacaoConformeHorario() + ", " + usuarioLogado.getLogin();
        JLabel rotuloSaudacao = new JLabel(saudacao);
        rotuloSaudacao.setFont(new Font("Segoe UI", Font.BOLD, 30));
        rotuloSaudacao.setForeground(ComponentesUi.COR_TEXTO_CLARO);
        rotuloSaudacao.setAlignmentX(Component.CENTER_ALIGNMENT);

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy");
        JLabel rotuloData = new JLabel(capitalizar(LocalDateTime.now().format(formato)));
        rotuloData.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rotuloData.setForeground(ComponentesUi.COR_TEXTO_ROTULO);
        rotuloData.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel rotuloDica = new JLabel("Selecione uma opção no menu à esquerda para começar.");
        rotuloDica.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rotuloDica.setForeground(ComponentesUi.COR_TEXTO_ROTULO);
        rotuloDica.setAlignmentX(Component.CENTER_ALIGNMENT);

        bloco.add(rotuloSaudacao);
        bloco.add(Box.createVerticalStrut(6));
        bloco.add(rotuloData);
        bloco.add(Box.createVerticalStrut(28));
        bloco.add(rotuloDica);

        conteudo.add(bloco);
        return conteudo;
    }

    private static String saudacaoConformeHorario() {
        int hora = LocalDateTime.now().getHour();
        if (hora < 12) return "Bom dia";
        if (hora < 18) return "Boa tarde";
        return "Boa noite";
    }

    private static String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    private void abrirNovoPedido() {
        Integer numeroMesa = PedidoFrame.perguntarNumeroMesa(this);
        if (numeroMesa == null) {
            return;
        }
        new PedidoFrame(numeroMesa).setVisible(true);
    }
}
