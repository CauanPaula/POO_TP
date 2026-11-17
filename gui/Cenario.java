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

public class Cenario extends JPanel implements ActionListener, KeyListener {

    private Timer timer;
    private Jogo motorJogo;

    private int tamanhoTelaX = 800;
    private int tamanhoTelaY = 600;

    public Cenario() {
        setPreferredSize(new Dimension(this.tamanhoTelaX, this.tamanhoTelaY));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        this.motorJogo = new Jogo(this.tamanhoTelaX, this.tamanhoTelaY);

        // Timer para atualizar o jogo a ~60 FPS (16ms)
        timer = new Timer(16, this);
        timer.start();
    }

    
    @Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (motorJogo.isGameOver()) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("FIM DE JOGO", 280, 260);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(
            motorJogo.getScore().getNomePlayer() + " - Score Final: "
            + motorJogo.getScore().getScorePlayer(), 265, 310
        );
        g.drawString("Pressione R para reiniciar", 275, 350);
        return;
    }

    Jogador jogador = motorJogo.getJogador();
    ArrayList<Tiro> bulletsJogador = motorJogo.getBulletsJogador();
    ArrayList<Tiro> bulletsInimigo = motorJogo.getBulletsInimigo();
    ArrayList<Inimigo> enemies = motorJogo.getEnemies();
    ArrayList<Buffing> buffs = motorJogo.getBuffs();

    // Desenhar inimigos
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

    // Desenhar buffs
    for (Buffing b : buffs) {
        b.desenhar(g);
    }

    // Desenhar tiros do jogador (com sprite)
    for (Tiro tiro : bulletsJogador) {
        tiro.desenhar(g);
    }

    // Desenhar tiros dos inimigos
    for (Tiro tiro : bulletsInimigo) {
        tiro.desenhar(g);
    }

    // Desenhar jogador
    jogador.desenhar(g);

    // HUD
    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial", Font.BOLD, 16));
    g.drawString("Score: " + motorJogo.getScore().getScorePlayer(), 20, 30);
    g.drawString("Vida: " + jogador.getVida(), 20, 55);
    g.drawString("Dano: " + jogador.getDano(), 20, 80);
    g.drawString(
        String.format("Cadência: %.1f", jogador.getCadencia()), 20, 105
    );
}

    // LIDAR COM ACOPLAMENTO
    @Override
    public void actionPerformed(ActionEvent e) {

        motorJogo.atualizar();

        repaint();
    }


    //Verificacao de teclas para movimentacao e disparo
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
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
    //Reduzir e retirar caso necessário partes que não tenham função real mais nesses códigos, um grande exemplo sendo o metodo de colisao

    //MAIN PARA TESTES
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
