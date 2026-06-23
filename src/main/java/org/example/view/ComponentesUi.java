package org.example.view;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Componentes visuais reaproveitáveis entre as telas, evitando que cada
 * Frame repita o mesmo código de estilização (cores, fontes, bordas).
 *
 * Identidade visual: rosa neon (navegação/destaque) + verde neon (ações
 * positivas: salvar, confirmar, cadastrar), com tema claro e escuro.
 */
final class ComponentesUi {

    /** true = tema claro, false = tema escuro (padrão). */
    static boolean modoClaro = false;

    // ---------- Paleta que muda entre tema claro/escuro ----------
    static Color COR_FUNDO;
    static Color COR_PAINEL;
    static Color COR_CAMPO;
    static Color COR_BORDA_CAMPO;
    static Color COR_TEXTO_CLARO;
    static Color COR_TEXTO_ROTULO;
    static Color COR_DESTAQUE;
    static Color COR_GRADE;
    static Color COR_CABECALHO_BASE;

    // ---------- Tons de rosa (navegação/identidade) ----------
    static final Color ROSA_NEON       = new Color(255, 45, 149);  // principal (login / menu / pedidos)
    static final Color ROSA_CLARO      = new Color(255, 120, 200); // estoque (visualização)
    static final Color ROSA_FUCSIA     = new Color(255, 0, 200);
    static final Color ROSA_MAGENTA    = new Color(200, 20, 150);  // cadastro de usuário
    static final Color ROSA_PROFUNDO   = new Color(160, 20, 110);  // fichas
    static final Color ROSA_SALMAO     = new Color(255, 110, 150); // relatório
    static final Color ROSA_ALERTA     = new Color(235, 30, 95);   // ações destrutivas (remover)

    // ---------- Tons de verde (ações positivas: salvar/confirmar/cadastrar) ----------
    static final Color VERDE_NEON      = new Color(35, 220, 140);
    static final Color VERDE_CLARO     = new Color(120, 240, 180);
    static final Color VERDE_PROFUNDO  = new Color(20, 150, 95);

    static final Color CINZA_BOTAO_SEC = new Color(40, 30, 38);    // botão secundário (voltar)

    static {
        aplicarTemaEscuro();
    }

    private ComponentesUi() {
    }

    /** Alterna entre tema claro e escuro. Telas já abertas precisam ser reabertas para refletir a troca. */
    static void alternarTema() {
        modoClaro = !modoClaro;
        if (modoClaro) {
            aplicarTemaClaro();
        } else {
            aplicarTemaEscuro();
        }
    }

    private static void aplicarTemaEscuro() {
        COR_FUNDO        = new Color(8, 6, 10);
        COR_PAINEL       = new Color(18, 13, 18);
        COR_CAMPO        = new Color(28, 18, 26);
        COR_BORDA_CAMPO  = new Color(255, 45, 149);
        COR_TEXTO_CLARO  = new Color(248, 232, 242);
        COR_TEXTO_ROTULO = new Color(255, 175, 215);
        COR_DESTAQUE     = new Color(255, 45, 149);
        COR_GRADE        = new Color(45, 25, 38);
        COR_CABECALHO_BASE = new Color(20, 8, 16);
    }

    private static void aplicarTemaClaro() {
        COR_FUNDO        = new Color(248, 244, 246);
        COR_PAINEL       = new Color(255, 255, 255);
        COR_CAMPO        = new Color(240, 232, 238);
        COR_BORDA_CAMPO  = new Color(220, 30, 130);
        COR_TEXTO_CLARO  = new Color(35, 25, 32);
        COR_TEXTO_ROTULO = new Color(150, 20, 90);
        COR_DESTAQUE     = new Color(220, 30, 130);
        COR_GRADE        = new Color(225, 205, 215);
        COR_CABECALHO_BASE = new Color(255, 235, 245);
    }

    // =========================================================
    // Logo
    // =========================================================

    /** Painel com o logo "FORJA BAR" desenhado (ícone de chama + wordmark com glow rosa neon). */
    static JPanel criarLogo(int alturaIcone) {
        JPanel painel = new JPanel();
        painel.setOpaque(false);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));

        JComponent icone = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                int cx = w / 2;

                int[] xs = {cx, cx + h / 5, cx + h / 8, cx - h / 8, cx - h / 5};
                int[] ys = {(int) (h * 0.08), (int) (h * 0.45), (int) (h * 0.95), (int) (h * 0.95), (int) (h * 0.45)};

                for (int i = 4; i >= 0; i--) {
                    float alpha = 0.10f + (4 - i) * 0.05f;
                    g2.setColor(new Color(255, 45, 149, (int) (alpha * 255)));
                    g2.setStroke(new BasicStroke(i * 3f));
                    g2.drawPolygon(xs, ys, xs.length);
                }
                GradientPaint gradiente = new GradientPaint(
                        cx, 0, new Color(255, 170, 215),
                        cx, h, new Color(255, 0, 170));
                g2.setPaint(gradiente);
                g2.fillPolygon(xs, ys, xs.length);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(alturaIcone, alturaIcone);
            }
        };
        icone.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel wordmark = new JLabel("FORJA BAR");
        wordmark.setFont(new Font("Segoe UI", Font.BOLD, Math.max(18, alturaIcone / 2)));
        wordmark.setForeground(ROSA_NEON);
        wordmark.setAlignmentX(Component.CENTER_ALIGNMENT);
        wordmark.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel sublinha = new JLabel("S U S H I   B A R");
        sublinha.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sublinha.setForeground(COR_TEXTO_ROTULO);
        sublinha.setAlignmentX(Component.CENTER_ALIGNMENT);
        sublinha.setHorizontalAlignment(SwingConstants.CENTER);

        painel.add(icone);
        painel.add(Box.createVerticalStrut(6));
        painel.add(wordmark);
        painel.add(sublinha);
        return painel;
    }

    // =========================================================
    // Campos de texto
    // =========================================================

    static JTextField criarCampoTexto() {
        JTextField campo = new CampoArredondado();
        estilizarCampo(campo);
        return campo;
    }

    static JPasswordField criarCampoSenha() {
        JPasswordField campo = new CampoSenhaArredondado();
        estilizarCampo(campo);
        return campo;
    }

    static void estilizarCampo(JTextField campo) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        campo.setOpaque(false);
        campo.setBackground(COR_CAMPO);
        campo.setForeground(COR_TEXTO_CLARO);
        campo.setCaretColor(ROSA_NEON);
        campo.setBorder(new BordaArredondadaCampo());
    }

    /**
     * Preenche o fundo arredondado do campo. Precisa ser feito aqui (em
     * paintComponent, ANTES do texto ser desenhado pela classe pai) e não na
     * borda — se o preenchimento for feito na borda (que é pintada DEPOIS do
     * texto), ele cobre cada caractere digitado, dando a impressão de que o
     * campo não aceita digitação.
     */
    private static void pintarFundoArredondado(JComponent campo, Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(campo.getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, campo.getWidth() - 1, campo.getHeight() - 1, 14, 14));
        g2.dispose();
    }

    private static class CampoArredondado extends JTextField {
        @Override
        protected void paintComponent(Graphics g) {
            pintarFundoArredondado(this, g);
            super.paintComponent(g);
        }
    }

    private static class CampoSenhaArredondado extends JPasswordField {
        @Override
        protected void paintComponent(Graphics g) {
            pintarFundoArredondado(this, g);
            super.paintComponent(g);
        }
    }

    /** Borda neon arredondada usada nos campos de texto/senha (só o contorno; o fundo é pintado no próprio campo). */
    private static class BordaArredondadaCampo extends AbstractBorder {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COR_BORDA_CAMPO);
            g2.setStroke(new BasicStroke(1.4f));
            g2.draw(new RoundRectangle2D.Float(x + 1, y + 1, width - 3, height - 3, 14, 14));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, 12, 8, 12);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(8, 12, 8, 12);
            return insets;
        }
    }

    static JLabel criarRotuloCampo(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rotulo.setForeground(COR_TEXTO_ROTULO);
        rotulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        return rotulo;
    }

    static JComboBox<String> criarComboBox(String... opcoes) {
        JComboBox<String> combo = new JComboBox<>(opcoes);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        combo.setBackground(COR_CAMPO);
        combo.setForeground(COR_TEXTO_CLARO);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA_CAMPO, 1, true),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        return combo;
    }

    // =========================================================
    // Botões neon
    // =========================================================

    /** Botão "preenchido" com cantos arredondados e leve glow na cor informada. */
    static JButton criarBotaoPrimario(String texto, Color cor, Color corTexto) {
        BotaoNeon botao = new BotaoNeon(texto, cor, corTexto, true);
        botao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        return botao;
    }

    /** Botão "fantasma" (contorno neon, fundo escuro) usado para ações secundárias. */
    static JButton criarBotaoSecundario(String texto) {
        BotaoNeon botao = new BotaoNeon(texto, ROSA_NEON, COR_TEXTO_CLARO, false);
        botao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return botao;
    }

    /** Botão customizado com cantos arredondados, preenchimento em gradiente e glow neon. */
    private static class BotaoNeon extends JButton {
        private final Color corBase;
        private final boolean preenchido;
        private boolean sobreMouse = false;

        BotaoNeon(String texto, Color corBase, Color corTexto, boolean preenchido) {
            super(texto);
            this.corBase = corBase;
            this.preenchido = preenchido;
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(preenchido ? corTexto : corBase.brighter());
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    sobreMouse = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    sobreMouse = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            RoundRectangle2D.Float forma = new RoundRectangle2D.Float(1, 1, w - 3, h - 3, 16, 16);

            if (!isEnabled()) {
                g2.setColor(modoClaro ? new Color(0, 0, 0, 25) : new Color(255, 255, 255, 18));
                g2.fill(forma);
                g2.setColor(corBase.darker());
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(forma);
                g2.dispose();
                super.paintComponent(g);
                return;
            }

            if (preenchido) {
                Color claro = sobreMouse ? corBase.brighter() : corBase;
                GradientPaint gradiente = new GradientPaint(0, 0, claro, 0, h, corBase.darker());
                g2.setPaint(gradiente);
                g2.fill(forma);
                if (sobreMouse) {
                    g2.setColor(new Color(255, 255, 255, 40));
                    g2.fill(forma);
                }
            } else {
                g2.setColor(sobreMouse ? new Color(corBase.getRed(), corBase.getGreen(), corBase.getBlue(), 35) : COR_PAINEL);
                g2.fill(forma);
                g2.setColor(corBase);
                g2.setStroke(new BasicStroke(1.6f));
                g2.draw(forma);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =========================================================
    // Tabela
    // =========================================================

    static JTable criarTabelaEstilizada(javax.swing.table.TableModel modelo, Color corSelecao) {
        JTable tabela = new JTable(modelo);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(28);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabela.setBackground(COR_PAINEL);
        tabela.setForeground(COR_TEXTO_CLARO);
        tabela.getTableHeader().setBackground(COR_CABECALHO_BASE);
        tabela.getTableHeader().setForeground(corSelecao);
        tabela.setSelectionBackground(corSelecao);
        tabela.setSelectionForeground(Color.WHITE);
        tabela.setGridColor(COR_GRADE);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return tabela;
    }

    // =========================================================
    // Cabeçalho
    // =========================================================

    static JPanel criarCabecalho(String titulo, Color corDestaque) {
        JPanel cabecalho = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradiente = new GradientPaint(
                        0, 0, COR_CABECALHO_BASE,
                        getWidth(), 0, blendComBase(corDestaque, 0.55f));
                g2.setPaint(gradiente);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(corDestaque);
                g2.fillRect(0, getHeight() - 3, getWidth(), 3);
                g2.dispose();
            }
        };
        cabecalho.setOpaque(false);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));
        JLabel rotulo = new JLabel(titulo);
        rotulo.setFont(new Font("Segoe UI", Font.BOLD, 19));
        rotulo.setForeground(modoClaro ? new Color(40, 20, 35) : Color.WHITE);
        cabecalho.add(rotulo);
        return cabecalho;
    }

    private static Color blendComBase(Color cor, float intensidade) {
        Color base = COR_CABECALHO_BASE;
        int r = (int) (cor.getRed() * intensidade + base.getRed() * (1 - intensidade));
        int g = (int) (cor.getGreen() * intensidade + base.getGreen() * (1 - intensidade));
        int b = (int) (cor.getBlue() * intensidade + base.getBlue() * (1 - intensidade));
        return new Color(Math.min(r, 255), Math.min(g, 255), Math.min(b, 255));
    }

    // =========================================================
    // Proteção contra duplo clique
    // =========================================================

    /**
     * Liga a ação ao botão desabilitando-o durante a execução, evitando que
     * um segundo clique (ex.: duplo clique acidental) dispare a mesma ação
     * duas vezes antes da primeira terminar.
     */
    static void aoClicar(JButton botao, Runnable acao) {
        botao.addActionListener(e -> {
            botao.setEnabled(false);
            try {
                acao.run();
            } finally {
                botao.setEnabled(true);
            }
        });
    }

    // =========================================================
    // Barra de ações (rodapé de telas — não estica feio ao maximizar)
    // =========================================================

    /** Barra de botões alinhada à direita, com largura própria de cada botão (não estica ao maximizar a janela). */
    static JPanel montarBarraAcoes(JButton... botoes) {
        JPanel barra = new JPanel();
        barra.setOpaque(false);
        barra.setLayout(new BoxLayout(barra, BoxLayout.X_AXIS));
        barra.add(Box.createHorizontalGlue());
        for (int i = 0; i < botoes.length; i++) {
            if (i > 0) {
                barra.add(Box.createHorizontalStrut(10));
            }
            botoes[i].setMaximumSize(new Dimension(220, 42));
            barra.add(botoes[i]);
        }
        return barra;
    }

    // =========================================================
    // Item de menu lateral (sidebar)
    // =========================================================

    static JButton criarItemMenuLateral(String texto, Color corAccent) {
        return new ItemMenuLateral(texto, corAccent);
    }

    private static class ItemMenuLateral extends JButton {
        private final Color corAccent;
        private boolean sobreMouse = false;

        ItemMenuLateral(String texto, Color corAccent) {
            super(texto);
            this.corAccent = corAccent;
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(COR_TEXTO_CLARO);
            setHorizontalAlignment(SwingConstants.LEFT);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(13, 24, 13, 16));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            setPreferredSize(new Dimension(220, 46));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    sobreMouse = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    sobreMouse = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (sobreMouse) {
                g2.setColor(new Color(corAccent.getRed(), corAccent.getGreen(), corAccent.getBlue(), modoClaro ? 28 : 38));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
            g2.setColor(corAccent);
            g2.fillRect(0, 0, 4, getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
