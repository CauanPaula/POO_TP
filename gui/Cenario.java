package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

import motor.Jogo;
import entidades.Buffing;
import entidades.Inimigo;
import entidades.Jogador;
import entidades.Tiro;
import tipos.TipoDeNave;

public class Cenario extends JPanel implements ActionListener, KeyListener {

    private Timer timer;
    private Jogo motorJogo;
    private Image besouroGif;
    private Image abelhaGif;
    private Image libelulaGif;
    private Image aranhaGif;
    private Image tiroAbelha;
    private Image tiroLibelula;
    private Image tiroBesouro;
    private Image tiroAranha;

    private int tamanhoTelaX = 800;
    private int tamanhoTelaY = 600;

    private boolean noMenu = true;

    public Cenario() {
        setPreferredSize(new Dimension(this.tamanhoTelaX, this.tamanhoTelaY));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        this.motorJogo = new Jogo(this.tamanhoTelaX, this.tamanhoTelaY);
        besouroGif = new ImageIcon("imagens/besouro/andando.gif").getImage();
        abelhaGif = new ImageIcon("imagens/abelha/voando.gif").getImage();
        libelulaGif = new ImageIcon("imagens/libelula/zap.gif").getImage();
        aranhaGif = new ImageIcon("imagens/aranha/aranha.gif").getImage();
        tiroAbelha = new ImageIcon("imagens/abelha/tiro.png").getImage();
        tiroLibelula = new ImageIcon("imagens/libelula/tiro.png").getImage();
        tiroBesouro = new ImageIcon("imagens/besouro/tiro.png").getImage();
        tiroAranha = new ImageIcon("imagens/aranha/tiro.png").getImage();

        // Timer para atualizar o jogo a ~60 FPS (16ms)
        timer = new Timer(16, this);
        timer.start();
    }

    // COMO NÃO EXISTEM IMAGENS AINDA, O CODIGO DESENHA AS FIGURAS
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (noMenu) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 28));
            g.drawString("Escolha sua nave", 290, 200);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            int y = 260;
            for (TipoDeNave t : TipoDeNave.values()) {
                g.drawString((t.ordinal() + 1) + " - " + t.name()
                        + String.format("  (vida x%.1f, dano x%.1f, vel x%.1f, cad x%.1f)",
                                t.getMultiplicaVida(), t.getMultiplicaDano(), t.getMultiplicaVelocidade(),
                                t.getMultiplicaCadencia()),
                        120, y);
                y += 35;
            }
            return;
        }

        if (motorJogo.isGameOver()) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("FIM DE JOGO", 280, 260);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString(
                    motorJogo.getScore().getNomePlayer() + " - Score Final: " + motorJogo.getScore().getScorePlayer(),
                    265, 310);
            g.drawString("Pressione R para reiniciar", 275, 350);
            return;
        }

        // Desenhar a nave do jogador centralizado
        Jogador jogador = motorJogo.getJogador();
        ArrayList<Tiro> bulletsJogador = motorJogo.getBulletsJogador();
        ArrayList<Tiro> bulletsInimigo = motorJogo.getBulletsInimigo();
        ArrayList<Inimigo> enemies = motorJogo.getEnemies();
        ArrayList<Buffing> buffs = motorJogo.getBuffs();

        int jX = (int) (jogador.getPos_x() - jogador.getTam_x());
        int jY = (int) (jogador.getPos_y() - jogador.getTam_y());
        int jLargura = (int) (jogador.getTam_x() * 2);
        int jAltura = (int) (jogador.getTam_y() * 2);

        // g.setColor(Color.CYAN);

        // NAVE 3 - BESOURO
        if (jogador.getTipo() == TipoDeNave.TIP3) {
            int tamanho = 60;

            int x = (int) jogador.getPos_x() - tamanho / 2;
            int y = (int) jogador.getPos_y() - tamanho / 2;

            g.drawImage(besouroGif, x, y, tamanho, tamanho, this);

        } else if (jogador.getTipo() == TipoDeNave.TIP1) {
            // NAVE 1 - ABELHA
            int tamanho = 55;

            int x = (int) jogador.getPos_x() - tamanho / 2;
            int y = (int) jogador.getPos_y() - tamanho / 2;

            g.drawImage(abelhaGif, x, y, tamanho, tamanho, this);

        } else if (jogador.getTipo() == TipoDeNave.TIP2) {
            // NAVE 2 - LIBÉLULA
            int tamanho = 55;

            int x = (int) jogador.getPos_x() - tamanho / 2;
            int y = (int) jogador.getPos_y() - tamanho / 2;

            g.drawImage(libelulaGif, x, y, tamanho, tamanho, this);
        }

        // Desenhar os tiros do jogador
        g.setColor(Color.YELLOW);
        // Desenhar os tiros do jogador
        for (Tiro b : bulletsJogador) {

            Image imagemTiro;
            int largura;
            int altura;

            switch (jogador.getTipo()) {
                case TIP1 -> {
                    imagemTiro = tiroAbelha;
                    largura = 10;
                    altura = 20;
                }
                case TIP2 -> {
                    imagemTiro = tiroLibelula;
                    largura = 8;
                    altura = 20;
                }
                default -> {
                    imagemTiro = tiroBesouro;
                    largura = 20;
                    altura = 20;
                }
            }

            int x = (int) b.getPos_x() - largura / 2;
            int y = (int) b.getPos_y() - altura / 2;

            g.drawImage(imagemTiro, x, y, largura, altura, this);
        }

        // Desenhar os tiros do inimigo
        for (Tiro b : bulletsInimigo) {
            int largura = 15;
            int altura = 37;

            int x = (int) b.getPos_x() - largura / 2;
            int y = (int) b.getPos_y() - altura / 2;

            g.drawImage(tiroAranha, x, y, largura, altura, this);
        }

        // Desenhar os inimigos
        for (Inimigo en : enemies) {

            if (en.getTipoInimigo() == 1) {

                int tamanho = 70;

                int x = (int) en.getPos_x() - tamanho / 2;
                int y = (int) en.getPos_y() - tamanho / 2;

                g.drawImage(aranhaGif, x, y, tamanho, tamanho, this);

            } else {

                g.setColor(Color.MAGENTA);

                int eX = (int) (en.getPos_x() - en.getTam_x());
                int eY = (int) (en.getPos_y() - en.getTam_y());
                int eW = (int) (en.getTam_x() * 2);
                int eH = (int) (en.getTam_y() * 2);

                g.fillRect(eX, eY, eW, eH);
            }
        }

        // Desenhar os buffs
        for (Buffing b : buffs) {
            g.setColor(Color.GRAY);
            int bX = (int) (b.getPos_x() - b.getTam_x());
            int bY = (int) (b.getPos_y() - b.getTam_y());
            int bW = (int) (b.getTam_x() * 2);
            int bH = (int) (b.getTam_y() * 2);
            g.fillRect(bX, bY, bW, bH);
        }

        // Desenhar HUD
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score: " + motorJogo.getScore().getScorePlayer(), 20, 30);
        g.drawString("Vida: " + motorJogo.getJogador().getVida(), 20, 55);
        g.drawString("Dano: " + motorJogo.getJogador().getDano(), 20, 80);
        g.drawString(String.format("Cadência: %.1f", motorJogo.getJogador().getCadencia()), 20, 105);
    }

    // LIDAR COM ACOPLAMENTO
    @Override
    public void actionPerformed(ActionEvent e) {
        // motorJogo.atualizar();
        if (!noMenu) {
            motorJogo.atualizar();
        }
        repaint();
    }

    // Verificacao de teclas para movimentacao e disparo
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (noMenu) {
            TipoDeNave[] tipos = TipoDeNave.values();
            int idx = key - KeyEvent.VK_1;
            if (idx >= 0 && idx < tipos.length) {
                motorJogo.setNaveSelecionada(tipos[idx]);
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
    // Reduzir e retirar caso necessário partes que não tenham função real mais
    // nesses códigos, um grande exemplo sendo o metodo de colisao

    // MAIN PARA TESTES
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
