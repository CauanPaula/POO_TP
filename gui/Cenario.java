package gui;

import entidades.Buffing;
import entidades.Inimigo;
import entidades.Jogador;
import entidades.Tiro;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
import motor.Jogo;
import tipos.TipoDeNave;

public class Cenario extends JPanel implements ActionListener, KeyListener {
    private Image imgFundo;
    private Image imgFundoMenu;
    private double bgY = 0;
    private double velocidadeFundo = 2.0;

    private Timer timer;
    private Jogo motorJogo;

    private int tamanhoTelaX = 800;
    private int tamanhoTelaY = 600;
    private int opcaoSelecionada = 0;

    private boolean noMenu = true;

    public Cenario() {
        setPreferredSize(new Dimension(this.tamanhoTelaX, this.tamanhoTelaY));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        try {
            this.imgFundo = ImageIO.read(new File("imagens/fundo.jpeg"));
            this.imgFundoMenu = ImageIO.read(new File("imagens/fundo_menu.png"));
        } catch (Exception e) {
            System.out.println("Erro ao carregar imagens de fundo: " + e.getMessage());
        }

        this.motorJogo = new Jogo(this.tamanhoTelaX, this.tamanhoTelaY);

        timer = new Timer(16, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int larguraTela = getWidth();
        int alturaTela = getHeight();

        // 1. Tela de Menu
        if (noMenu) {
            if (imgFundoMenu != null) {
                g.drawImage(imgFundoMenu, 0, 0, larguraTela, alturaTela, null);
            }

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 28));
            g.drawString("Escolha sua nave", 290, 200);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            int y = 260;
            for (TipoDeNave t : TipoDeNave.values()) {
                Jogador j = new Jogador(t, 20, 15);
                boolean selecionada = (t.ordinal() == opcaoSelecionada);
                motor.GerenciadorSom.tocarMusica("sons/musica_fundo.wav");
                g.setColor(selecionada ? Color.RED : Color.WHITE);
                g.drawString((selecionada ? "> " : "   ") + t.name()
                        + String.format(" -> vida: %d, dano: %d, velocidade: %.0f, cadencia: %.0f",
                                j.getVida(), j.getDano(), j.getVel_x(), j.getCadencia()), 120, y);
                y += 35;
            }

            g.setColor(Color.GRAY);
            g.setFont(new Font("Arial", Font.PLAIN, 14));
            g.drawString("W/S ou setas para navegar, Enter para confirmar", 230, y + 20);
            return;
        }

        // 2. Fundo em movimento infinito (congela quando der Game Over)
        if (imgFundo != null) {
            if (!motorJogo.isGameOver()) {
                bgY += velocidadeFundo;
                if (bgY >= alturaTela) {
                    bgY = 0;
                }
            }

            int yAtual = (int) bgY;
            g.drawImage(imgFundo, 0, yAtual, larguraTela, alturaTela, null);
            g.drawImage(imgFundo, 0, yAtual - alturaTela, larguraTela, alturaTela, null);
        }

        // 3. Recupera entidades do motor
        Jogador jogador = motorJogo.getJogador();
        ArrayList<Tiro> bulletsJogador = motorJogo.getBulletsJogador();
        ArrayList<Tiro> bulletsInimigo = motorJogo.getBulletsInimigo();
        ArrayList<Inimigo> enemies = motorJogo.getEnemies();
        ArrayList<Buffing> buffs = motorJogo.getBuffs();

        // 4. Desenhar inimigos
        for (Inimigo en : enemies) {
            switch (en.getTipoInimigo()) {
                case 1 -> g.setColor(Color.ORANGE);
                case 2 -> g.setColor(Color.MAGENTA);
                default -> g.setColor(Color.RED);
            }
            int x = (int) (en.getPos_x() - en.getTam_x());
            int y = (int) (en.getPos_y() - en.getTam_y());
            int largura = (int) (en.getTam_x() * 2);
            int altura = (int) (en.getTam_y() * 2);
            g.fillRect(x, y, largura, altura);
        }

        // 5. Desenhar buffs (com sprite)
        for (Buffing b : buffs) {
            b.desenhar(g);
        }

        // 6. Desenhar tiros do jogador (com sprite)
        for (Tiro tiro : bulletsJogador) {
            tiro.desenhar(g);
        }

        // 7. Desenhar tiros dos inimigos (com sprite)
        for (Tiro tiro : bulletsInimigo) {
            tiro.desenhar(g);
        }

        // 8. Desenhar jogador (congela a animação se deu Game Over)
        if (!motorJogo.isGameOver()) {
            jogador.atualizarAnimacao();
        }
        jogador.desenhar(g);

        // 9. Desenhar HUD
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score: " + motorJogo.getScore().getScorePlayer(), 20, 30);
        g.drawString("Vida: " + jogador.getVida(), 20, 55);
        g.drawString("Dano: " + jogador.getDano(), 20, 80);
        g.drawString(String.format("Cadência: %.1f", jogador.getCadencia()), 20, 105);

        // 10. Tela de Game Over desenhada POR CIMA da cena congelada
        if (motorJogo.isGameOver()) {
            // Película escura semitransparente para dar leitura ao texto sem esconder o jogo congelado
            g.setColor(new Color(0, 0, 0, 160));
            g.fillRect(0, 0, larguraTela, alturaTela);

            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("FIM DE JOGO", 280, 260);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString(motorJogo.getScore().getNomePlayer() + " - Score Final: " + motorJogo.getScore().getScorePlayer(), 265, 310);
            g.drawString("Pressione R para reiniciar", 275, 350);
            g.drawString("Pressione M para voltar ao menu", 255, 380);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Só atualiza posições e colisões enquanto não estiver no menu e não tiver dado Game Over
        if (!noMenu && !motorJogo.isGameOver()) {
            motorJogo.atualizar();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (noMenu) {
            int total = TipoDeNave.values().length;

            if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
                opcaoSelecionada = (opcaoSelecionada - 1 + total) % total;
            } else if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
                opcaoSelecionada = (opcaoSelecionada + 1) % total;
            } else if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_SPACE) {
                motorJogo.setNaveSelecionada(TipoDeNave.values()[opcaoSelecionada]);
                motorJogo.inicializarJogo();
                noMenu = false;
            }
            return;
        }
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
            motorJogo.setLeft(true);
        }
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            motorJogo.setRight(true);
        }
        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
            motorJogo.setUp(true);
        }
        if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
            motorJogo.setDown(true);
        }
        if (key == KeyEvent.VK_SPACE) {
            motorJogo.setSpace(true);
        }
        if (key == KeyEvent.VK_R && motorJogo.isGameOver()) {
            motorJogo.inicializarJogo();
        }
        if (key == KeyEvent.VK_M && motorJogo.isGameOver()) {
            noMenu = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
            motorJogo.setLeft(false);
        }
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            motorJogo.setRight(false);
        }
        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
            motorJogo.setUp(false);
        }
        if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
            motorJogo.setDown(false);
        }
        if (key == KeyEvent.VK_SPACE) {
            motorJogo.setSpace(false);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Teste do jogo");
        Cenario game = new Cenario();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}