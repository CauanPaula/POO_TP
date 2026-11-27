package gui;

import entidades.Jogador;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import motor.GerenciadorSom;
import tipos.TipoDeNave;

/**
 * Menu do jogo, com três telas:
 *  - PRINCIPAL: JOGAR / CONFIGURAÇÕES
 *  - SELECAO:   3 quadrados estilo arcade + inseto gigante ao lado
 *  - CONFIG:    som ligado/desligado e volume
 *
 * Cada tela tem o seu próprio background (imagens/fundo_menu.png,
 * imagens/fundo_selecao.png e imagens/fundo_config.png).
 * Se algum arquivo não existir, usa o fundo_menu.png no lugar.
 */
public class Menu {

    private enum Tela { PRINCIPAL, SELECAO, CONFIG }
    public interface OuvinteResolucao {
        void aoMudar(int largura, int altura, boolean telaCheia);
    }
    // ====== Ajustes fáceis de mexer ======
    private static final String TITULO = "BULLET BUGS";
    private static final String[] OPCOES_PRINCIPAL = {"JOGAR", "CONFIGURAÇÕES"};
    private static final String[] OPCOES_CONFIG = {"SOM", "VOLUME", "RESOLUÇÃO", "VOLTAR"};
    private static final int[][] RESOLUCOES = {{800, 600}, {1024, 768}, {1280, 960}, {0, 0}};

    private static final int TAM_CARD = 120;      // lado de cada quadrado
    private static final int ESPACO_CARD = 26;    // espaço entre quadrados
    private static final int ALTURA_INSETO = 300; // altura do inseto gigante
    // =====================================

    private static final Font FONTE_TITULO = new Font("Arial", Font.BOLD, 56);
    private static final Font FONTE_BOTAO = new Font("Arial", Font.BOLD, 24);
    private static final Font FONTE_SUBTITULO = new Font("Arial", Font.BOLD, 30);
    private static final Font FONTE_NOME = new Font("Arial", Font.BOLD, 36);
    private static final Font FONTE_CARD = new Font("Arial", Font.BOLD, 13);
    private static final Font FONTE_STAT = new Font("Arial", Font.BOLD, 13);
    private static final Font FONTE_DICA = new Font("Arial", Font.PLAIN, 14);

    private final Consumer<TipoDeNave> aoEscolherNave;
    private final OuvinteResolucao aoMudarResolucao;
    private int resolucaoAtual = 0;
    private Image fundoPrincipal;
    private Image fundoSelecao;
    private Image fundoConfig;

    // Dados por nave (indexados por ordinal do enum)
    private final TipoDeNave[] tipos = TipoDeNave.values();
    private final Image[][] frames = new Image[tipos.length][];
    private final Color[] cores = new Color[tipos.length];
    private final int[] vida = new int[tipos.length];
    private final int[] dano = new int[tipos.length];
    private final double[] velocidade = new double[tipos.length];
    private final double[] cadencia = new double[tipos.length];
    private double maxVida, maxDano, maxVel, maxCad;

    private Tela tela = Tela.PRINCIPAL;
    private int opPrincipal = 0;
    private int opNave = 0;
    private int opConfig = 0;

    private long tick = 0;
    private int frameIdx = 0;
    private int dirIdx = 1;
    private int contAnim = 0;

   public Menu(Consumer<TipoDeNave> aoEscolherNave, OuvinteResolucao aoMudarResolucao) {
        this.aoEscolherNave = aoEscolherNave;
        this.aoMudarResolucao = aoMudarResolucao;
       

        this.fundoPrincipal = carregarImagem("imagens/fundo_menu.png");
        this.fundoSelecao = carregarImagem("imagens/fundo_selecao.png");
        this.fundoConfig = carregarImagem("imagens/fundo_config.png");
        if (fundoSelecao == null) fundoSelecao = fundoPrincipal;
        if (fundoConfig == null) fundoConfig = fundoPrincipal;

        for (TipoDeNave t : tipos) {
            int i = t.ordinal();
            frames[i] = carregarFrames("imagens/" + t.name().toLowerCase() + "/menu");
            cores[i] = corDoTipo(t);

            // Status reais (mesma fórmula usada dentro do jogo)
            Jogador j = new Jogador(t, 20, 15);
            vida[i] = j.getVida();
            dano[i] = j.getDano();
            velocidade[i] = j.getVel_x();
            cadencia[i] = j.getCadencia();

            maxVida = Math.max(maxVida, vida[i]);
            maxDano = Math.max(maxDano, dano[i]);
            maxVel = Math.max(maxVel, velocidade[i]);
            maxCad = Math.max(maxCad, cadencia[i]);
        }
    }

    // ------------------------------------------------------------------
    // Carregamento
    // ------------------------------------------------------------------

    private static Image carregarImagem(String caminho) {
        try {
            return ImageIO.read(new File(caminho));
        } catch (Exception e) {
            System.out.println("Menu: não consegui carregar " + caminho + " (" + e.getMessage() + ")");
            return null;
        }
    }

    private static Image[] carregarFrames(String pasta) {
        File dir = new File(pasta);
        File[] arquivos = dir.listFiles((d, nome) -> nome.toLowerCase().endsWith(".png"));
        if (arquivos == null || arquivos.length == 0) {
            return new Image[0];
        }
        Arrays.sort(arquivos);
        ArrayList<Image> lista = new ArrayList<>();
        for (File f : arquivos) {
            Image img = carregarImagem(f.getPath());
            if (img != null) lista.add(img);
        }
        return lista.toArray(new Image[0]);
    }

    private static Color corDoTipo(TipoDeNave t) {
        switch (t) {
            case Abelha:   return new Color(255, 196, 40);
            case Libelula: return new Color(60, 220, 255);
            case Besouro:  return new Color(160, 120, 255);
            default:       return Color.WHITE;
        }
    }

    // ------------------------------------------------------------------
    // Controle
    // ------------------------------------------------------------------

    public void voltarAoInicio() {
        tela = Tela.PRINCIPAL;
    }

    /** Chamado a cada tick do timer (anima o inseto gigante). */
    public void atualizar() {
        tick++;
        contAnim++;
        if (contAnim >= 7) {
            contAnim = 0;
            int len = frames[opNave].length;
            if (len <= 1) {
                frameIdx = 0;
            } else {
                frameIdx += dirIdx;
                if (frameIdx >= len - 1) {
                    frameIdx = len - 1;
                    dirIdx = -1;
                } else if (frameIdx <= 0) {
                    frameIdx = 0;
                    dirIdx = 1;
                }
            }
        }
    }

    public void teclaPressionada(int key) {
        boolean cima = key == KeyEvent.VK_UP || key == KeyEvent.VK_W;
        boolean baixo = key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S;
        boolean esq = key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A;
        boolean dir = key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D;
        boolean ok = key == KeyEvent.VK_ENTER || key == KeyEvent.VK_SPACE;
        boolean voltar = key == KeyEvent.VK_ESCAPE || key == KeyEvent.VK_BACK_SPACE;

        switch (tela) {
            case PRINCIPAL -> {
                int n = OPCOES_PRINCIPAL.length;
                if (cima) { opPrincipal = (opPrincipal - 1 + n) % n; som(); }
                else if (baixo) { opPrincipal = (opPrincipal + 1) % n; som(); }
                else if (ok) {
                    som();
                    tela = (opPrincipal == 0) ? Tela.SELECAO : Tela.CONFIG;
                }
            }
            case SELECAO -> {
                int n = tipos.length;
                if (cima || esq) { mudarNave((opNave - 1 + n) % n); }
                else if (baixo || dir) { mudarNave((opNave + 1) % n); }
                else if (ok) {
                    som();
                    aoEscolherNave.accept(tipos[opNave]);
                } else if (voltar) {
                    som();
                    tela = Tela.PRINCIPAL;
                }
            }
           case CONFIG -> {
                int n = OPCOES_CONFIG.length;
                if (cima) { opConfig = (opConfig - 1 + n) % n; som(); }
                else if (baixo) { opConfig = (opConfig + 1) % n; som(); }
                else if (esq || dir) {
                    if (opConfig == 0) {
                        alternarSom();
                    } else if (opConfig == 1) {
                        int passo = dir ? 10 : -10;
                        GerenciadorSom.setVolume(GerenciadorSom.getVolume() + passo);
                        som();
                    } else if (opConfig == 2) {
                        mudarResolucao(dir ? 1 : -1);
                    }
                } else if (ok) {
                    if (opConfig == 0) {
                        alternarSom();
                    } else if (opConfig == 2) {
                        mudarResolucao(1);
                    } else if (opConfig == 3) {
                        som();
                        tela = Tela.PRINCIPAL;
                    }
                } else if (voltar) {
                    som();
                    tela = Tela.PRINCIPAL;
                }
            }
        }
    }

    private void mudarNave(int novo) {
        opNave = novo;
        frameIdx = 0;
        dirIdx = 1;
        contAnim = 0;
        som();
    }
   private void mudarResolucao(int sentido) {
        int n = RESOLUCOES.length;
        resolucaoAtual = (resolucaoAtual + sentido + n) % n;
        int[] r = RESOLUCOES[resolucaoAtual];
        aoMudarResolucao.aoMudar(r[0], r[1], r[0] == 0);
        som();
    }
    private void alternarSom() {
        boolean novo = !GerenciadorSom.isSomAtivado();
        GerenciadorSom.setSomAtivado(novo);
        if (novo) {
            // pararMusica() zera o clip, então precisa tocar de novo
            GerenciadorSom.tocarMusica("sons/musica_fundo.wav");
            som();
        }
    }

    private void som() {
        GerenciadorSom.tocarEfeito("sons/selecao.wav");
    }

    // ------------------------------------------------------------------
    // Desenho
    // ------------------------------------------------------------------

    public void desenhar(Graphics g0, int w, int h) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        switch (tela) {
            case PRINCIPAL -> desenharPrincipal(g, w, h);
            case SELECAO -> desenharSelecao(g, w, h);
            case CONFIG -> desenharConfig(g, w, h);
        }
        g.dispose();
    }

    private void fundo(Graphics2D g, Image img, int w, int h) {
        if (img != null) {
            g.drawImage(img, 0, 0, w, h, null);
        } else {
            g.setPaint(new GradientPaint(0, 0, new Color(10, 20, 30), 0, h, new Color(0, 0, 0)));
            g.fillRect(0, 0, w, h);
        }
    }

    private void vinheta(Graphics2D g, int w, int h, int alpha) {
        g.setPaint(new RadialGradientPaint(w / 2f, h / 2f, Math.max(w, h) * 0.75f,
                new float[]{0f, 1f},
                new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, alpha)}));
        g.fillRect(0, 0, w, h);
    }

    // ---------- Tela principal ----------

    private void desenharPrincipal(Graphics2D g, int w, int h) {
        fundo(g, fundoPrincipal, w, h);
        vinheta(g, w, h, 170);

        g.setFont(FONTE_TITULO);
        textoBrilho(g, TITULO, w / 2, 150, Color.WHITE, new Color(80, 255, 220));

        int bw = 340, bh = 58;
        for (int i = 0; i < OPCOES_PRINCIPAL.length; i++) {
            int x = (w - bw) / 2;
            int y = 270 + i * 85;
            botao(g, x, y, bw, bh, OPCOES_PRINCIPAL[i], i == opPrincipal, new Color(80, 255, 220));
        }
        dica(g, w, h, "W/S ou setas para navegar  •  Enter para confirmar");
    }

    private void botao(Graphics2D g, int x, int y, int bw, int bh, String texto, boolean sel, Color neon) {
        g.setColor(sel ? new Color(0, 0, 0, 190) : new Color(0, 0, 0, 120));
        g.fillRoundRect(x, y, bw, bh, 18, 18);

        if (sel) {
            float pulso = 0.6f + 0.4f * (float) Math.sin(tick * 0.12);
            g.setColor(comAlpha(neon, (int) (70 * pulso)));
            g.setStroke(new BasicStroke(9f));
            g.drawRoundRect(x, y, bw, bh, 18, 18);
            g.setColor(neon);
            g.setStroke(new BasicStroke(3f));
            g.drawRoundRect(x, y, bw, bh, 18, 18);

            // setinha do seletor
            int cy = y + bh / 2;
            int[] px = {x + 18, x + 18, x + 34};
            int[] py = {cy - 9, cy + 9, cy};
            g.fillPolygon(px, py, 3);
        } else {
            g.setColor(new Color(255, 255, 255, 70));
            g.setStroke(new BasicStroke(1.5f));
            g.drawRoundRect(x, y, bw, bh, 18, 18);
        }
        g.setStroke(new BasicStroke(1f));
        g.setFont(FONTE_BOTAO);
        g.setColor(sel ? Color.WHITE : new Color(200, 210, 210));
        centralizado(g, texto, x + bw / 2, y + bh / 2 + 9);
    }

    // ---------- Seleção de inseto ----------

    private void desenharSelecao(Graphics2D g, int w, int h) {
        fundo(g, fundoSelecao, w, h);
        vinheta(g, w, h, 190);

        g.setFont(FONTE_SUBTITULO);
       

        // Coluna de quadrados (esquerda do centro)
        int cx = w / 2 - 190;
        int total = tipos.length * TAM_CARD + (tipos.length - 1) * ESPACO_CARD;
        int y0 = (h - total) / 2 + 28;
        for (int i = 0; i < tipos.length; i++) {
            desenharCard(g, i, cx, y0 + i * (TAM_CARD + ESPACO_CARD) + TAM_CARD / 2);
        }

        // Inseto gigante (direita)
        desenharInsetoGigante(g, opNave, w / 2 + 150, 255);
        desenharStats(g, opNave, w / 2 + 150, 445);

        dica(g, w, h, "W/S ou setas para escolher  •  Enter para jogar  •  Esc para voltar");
    }

    private void desenharCard(Graphics2D g, int i, int cx, int cy) {
        boolean sel = (i == opNave);
        Color cor = cores[i];
        float escala = sel ? 1.12f : 1f;
        int s = Math.round(TAM_CARD * escala);
        int x = cx - s / 2;
        int y = cy - s / 2;

        if (sel) {
            float pulso = 0.6f + 0.4f * (float) Math.sin(tick * 0.12);
            g.setColor(comAlpha(cor, (int) (60 * pulso)));
            g.fillRoundRect(x - 10, y - 10, s + 20, s + 20, 26, 26);
        }

        g.setColor(sel ? new Color(0, 0, 0, 200) : new Color(0, 0, 0, 140));
        g.fillRoundRect(x, y, s, s, 16, 16);

        g.setStroke(new BasicStroke(sel ? 4f : 1.5f));
        g.setColor(sel ? cor : new Color(255, 255, 255, 80));
        g.drawRoundRect(x, y, s, s, 16, 16);
        g.setStroke(new BasicStroke(1f));

        // miniatura
        int area = s - 34;
        Image[] fr = frames[i];
        if (fr.length > 0) {
            desenharAjustada(g, fr[0], cx, y + 8 + area / 2, area, area);
        } else {
            placeholder(g, tipos[i].name(), cores[i], cx, y + 8 + area / 2, area);
        }

        g.setFont(FONTE_CARD);
        g.setColor(sel ? Color.WHITE : new Color(190, 200, 200));
        centralizado(g, tipos[i].name().toUpperCase(), cx, y + s - 9);
    }

    private void desenharInsetoGigante(Graphics2D g, int i, int cx, int cy) {
        Color cor = cores[i];
        double bob = Math.sin(tick * 0.06) * 9;

        // aura (brilho pulsante)
        float pulso = 0.85f + 0.15f * (float) Math.sin(tick * 0.09);
        float raio = 235f * pulso;
        g.setPaint(new RadialGradientPaint(cx, cy, raio,
                new float[]{0f, 0.55f, 1f},
                new Color[]{comAlpha(cor, 150), comAlpha(cor, 55), comAlpha(cor, 0)}));
        g.fillOval((int) (cx - raio), (int) (cy - raio), (int) (raio * 2), (int) (raio * 2));

        // anel girando com faíscas
        for (int k = 0; k < 14; k++) {
            double ang = tick * 0.02 + k * (2 * Math.PI / 14);
            double r = 175 + 14 * Math.sin(tick * 0.05 + k);
            int sx = (int) (cx + Math.cos(ang) * r);
            int sy = (int) (cy + Math.sin(ang) * r * 0.55);
            int tam = 4 + (k % 3) * 2;
            g.setColor(comAlpha(cor, 190));
            g.fillOval(sx - tam / 2, sy - tam / 2, tam, tam);
        }

        // sombra no "chão"
        int sombraW = (int) (190 - bob * 2);
        g.setColor(new Color(0, 0, 0, 90));
        g.fillOval(cx - sombraW / 2, cy + ALTURA_INSETO / 2 + 6, sombraW, 26);

        // o bicho
        Image[] fr = frames[i];
        if (fr.length > 0) {
            Image img = fr[Math.min(frameIdx, fr.length - 1)];
            desenharAjustada(g, img, cx, (int) (cy + bob), ALTURA_INSETO * 2, ALTURA_INSETO);
        } else {
            placeholder(g, tipos[i].name(), cor, cx, (int) (cy + bob), ALTURA_INSETO - 40);
        }
    }

    private void desenharStats(Graphics2D g, int i, int cx, int yNome) {
        g.setFont(FONTE_NOME);
        textoBrilho(g, tipos[i].name().toUpperCase(), cx, yNome, Color.WHITE, cores[i]);

        String[] nomes = {"VIDA", "DANO", "VELOCIDADE", "CADÊNCIA"};
        double[] vals = {vida[i] / maxVida, dano[i] / maxDano, velocidade[i] / maxVel, cadencia[i] / maxCad};
        String[] txt = {
            String.valueOf(vida[i]), String.valueOf(dano[i]),
            String.format("%.1f", velocidade[i]), String.format("%.1f", cadencia[i])
        };

        int xLabel = cx - 150;
        int xBarra = cx - 40;
        int larg = 150;
        int y = yNome + 28;
        g.setFont(FONTE_STAT);
        for (int k = 0; k < nomes.length; k++) {
            g.setColor(new Color(220, 230, 230));
            g.drawString(nomes[k], xLabel, y + 11);

            g.setColor(new Color(255, 255, 255, 40));
            g.fillRoundRect(xBarra, y, larg, 12, 8, 8);
            g.setColor(cores[i]);
            g.fillRoundRect(xBarra, y, (int) Math.max(8, larg * vals[k]), 12, 8, 8);

            g.setColor(Color.WHITE);
            g.drawString(txt[k], xBarra + larg + 10, y + 11);
            y += 24;
        }
    }

    // ---------- Configurações ----------

   private void desenharConfig(Graphics2D g, int w, int h) {
        fundo(g, fundoConfig, w, h);
        vinheta(g, w, h, 120);

        int pw = 520, ph = 345;
        int px = (w - pw) / 2, py = (h - ph) / 2 + 25;
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRoundRect(px, py, pw, ph, 28, 28);
        g.setColor(new Color(255, 170, 90, 150));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(px, py, pw, ph, 28, 28);
        g.setStroke(new BasicStroke(1f));

        g.setFont(FONTE_SUBTITULO);
        textoBrilho(g, "CONFIGURAÇÕES", w / 2, py - 20, Color.WHITE, new Color(255, 170, 90));

        Color neon = new Color(255, 170, 90);
        int bw = 420, bh = 54;
        for (int i = 0; i < OPCOES_CONFIG.length; i++) {
            int x = (w - bw) / 2;
            int y = py + 30 + i * 74;
            boolean sel = (i == opConfig);
            botao(g, x, y, bw, bh, "", sel, neon);

            g.setFont(FONTE_BOTAO);
            g.setColor(sel ? Color.WHITE : new Color(200, 210, 210));
            if (i == 0) {
                g.drawString("SOM", x + 50, y + bh / 2 + 9);
                boolean on = GerenciadorSom.isSomAtivado();
                g.setColor(on ? new Color(120, 255, 140) : new Color(255, 110, 110));
                String t = on ? "LIGADO" : "DESLIGADO";
                g.drawString(t, x + bw - 30 - g.getFontMetrics().stringWidth(t), y + bh / 2 + 9);
            } else if (i == 1) {
                g.drawString("VOLUME", x + 50, y + bh / 2 + 9);
                int vol = GerenciadorSom.getVolume();
                int bx = x + 195, bwid = 160;
                int blocos = 10;
                for (int b = 0; b < blocos; b++) {
                    boolean cheio = vol >= (b + 1) * 10;
                    g.setColor(cheio ? neon : new Color(255, 255, 255, 50));
                    g.fillRoundRect(bx + b * (bwid / blocos), y + bh / 2 - 9, bwid / blocos - 3, 18, 4, 4);
                }
                g.setFont(FONTE_STAT);
                g.setColor(Color.WHITE);
                g.drawString(vol + "%", bx + bwid + 10, y + bh / 2 + 5);
            } else if (i == 2) {
                g.drawString("RESOLUÇÃO", x + 50, y + bh / 2 + 9);
                int[] r = RESOLUCOES[resolucaoAtual];
                String t = r[0] == 0 ? "< TELA CHEIA >" : "< " + r[0] + " x " + r[1] + " >";
                g.setColor(neon);
                g.drawString(t, x + bw - 30 - g.getFontMetrics().stringWidth(t), y + bh / 2 + 9);
            } else {
                g.setFont(FONTE_BOTAO);
                g.setColor(sel ? Color.WHITE : new Color(200, 210, 210));
                centralizado(g, "VOLTAR", x + bw / 2, y + bh / 2 + 9);
            }
        }
        dica(g, w, h, "W/S navega  •  A/D ou setas ajustam  •  Esc para voltar");
    }
    // ---------- Utilitários de desenho ----------

    /** Desenha a imagem mantendo a proporção, cabendo em maxW x maxH, centrada em (cx, cy). */
    private void desenharAjustada(Graphics2D g, Image img, int cx, int cy, int maxW, int maxH) {
        int iw = img.getWidth(null);
        int ih = img.getHeight(null);
        if (iw <= 0 || ih <= 0) return;
        double esc = Math.min((double) maxW / iw, (double) maxH / ih);
        int dw = (int) (iw * esc);
        int dh = (int) (ih * esc);
        g.drawImage(img, cx - dw / 2, cy - dh / 2, dw, dh, null);
    }

    /** Quando ainda não existe sprite daquele inseto, mostra uma silhueta com a inicial. */
    private void placeholder(Graphics2D g, String nome, Color cor, int cx, int cy, int tam) {
        g.setColor(comAlpha(cor, 90));
        g.fillOval(cx - tam / 2, cy - tam / 2, tam, tam);
        g.setColor(cor);
        g.setStroke(new BasicStroke(3f));
        g.drawOval(cx - tam / 2, cy - tam / 2, tam, tam);
        g.setStroke(new BasicStroke(1f));
        g.setFont(new Font("Arial", Font.BOLD, Math.max(16, tam / 2)));
        g.setColor(Color.WHITE);
        centralizado(g, nome.substring(0, 1), cx, cy + Math.max(16, tam / 2) / 3);
    }

    private void textoBrilho(Graphics2D g, String texto, int cx, int y, Color cor, Color brilho) {
        for (int r = 6; r >= 1; r -= 2) {
            g.setColor(comAlpha(brilho, 35));
            for (int dx = -r; dx <= r; dx += r) {
                for (int dy = -r; dy <= r; dy += r) {
                    centralizado(g, texto, cx + dx, y + dy);
                }
            }
        }
        g.setColor(new Color(0, 0, 0, 160));
        centralizado(g, texto, cx + 3, y + 3);
        g.setColor(cor);
        centralizado(g, texto, cx, y);
    }

    private void centralizado(Graphics2D g, String texto, int cx, int y) {
        int larg = g.getFontMetrics().stringWidth(texto);
        g.drawString(texto, cx - larg / 2, y);
    }

    private void dica(Graphics2D g, int w, int h, String texto) {
        g.setFont(FONTE_DICA);
        g.setColor(new Color(200, 210, 210, 200));
        centralizado(g, texto, w / 2, h - 14);
    }

    private static Color comAlpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, Math.min(255, alpha)));
    }
}
