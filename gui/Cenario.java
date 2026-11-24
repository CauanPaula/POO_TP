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

    private int tamanhoTelaX = 800;
    private int tamanhoTelaY = 600;
    private int opcaoSelecionada = 0;

    private boolean noMenu = true;

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

    //COMO NÃO EXISTEM IMAGENS AINDA, O CODIGO DESENHA AS FIGURAS
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
               Jogador j = new Jogador(t, 20, 15);
               boolean selecionada = (t.ordinal() == opcaoSelecionada);

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

        if (motorJogo.isGameOver()) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("FIM DE JOGO", 280, 260);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString(motorJogo.getScore().getNomePlayer() + " - Score Final: " + motorJogo.getScore().getScorePlayer(), 265, 310);
            g.drawString("Pressione R para reiniciar", 275, 350);
            g.drawString("Pressione M para voltar ao menu", 255, 380);  
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

        //g.setColor(Color.CYAN);
        switch (jogador.getTipo()) {
            case Abelha -> g.setColor(Color.CYAN);
            case Libelula -> g.setColor(Color.PINK);
            case Besouro -> g.setColor(Color.LIGHT_GRAY);
        }
        g.fillRect(jX, jY, jLargura, jAltura);
        g.setColor(Color.BLUE);
        g.fillRect((int) jogador.getPos_x() - 5, jY - 10, 10, 10); // Canhão

        //Desenhar os tiros do jogador
        g.setColor(Color.YELLOW);
        for (Tiro b : bulletsJogador) {
            int bX = (int) (b.getPos_x() - b.getTam_x());
            int bY = (int) (b.getPos_y() - b.getTam_y());
            int bW = (int) (b.getTam_x() * 2);
            int bH = (int) (b.getTam_y() * 2);
            g.fillRect(bX, bY, bW, bH);
        }

        //Desenhar os tiros do inimigo
        g.setColor(Color.GREEN);
        for (Tiro b : bulletsInimigo) {
            int bX = (int) (b.getPos_x() - b.getTam_x());
            int bY = (int) (b.getPos_y() - b.getTam_y());
            int bW = (int) (b.getTam_x() * 2);
            int bH = (int) (b.getTam_y() * 2);
            g.fillRect(bX, bY, bW, bH);
        }

        //Desenhar os inimigos
        for (Inimigo en : enemies) {
            switch (en.getTipoInimigo()) {
                case 1 ->
                    g.setColor(Color.ORANGE);
                case 2 ->
                    g.setColor(Color.MAGENTA);
                default ->
                    g.setColor(Color.RED);
            }
            int eX = (int) (en.getPos_x() - en.getTam_x());
            int eY = (int) (en.getPos_y() - en.getTam_y());
            int eW = (int) (en.getTam_x() * 2);
            int eH = (int) (en.getTam_y() * 2);
            g.fillRect(eX, eY, eW, eH);
        }

        //Desenhar os buffs
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
        g.drawString(String.format("Cadência: %.0f", motorJogo.getJogador().getCadencia()), 20, 105);
    }

    // LIDAR COM ACOPLAMENTO
    @Override
    public void actionPerformed(ActionEvent e) {
        //motorJogo.atualizar();
        if (!noMenu){
            motorJogo.atualizar();
        }
        repaint();
    }


    //Verificacao de teclas para movimentacao e disparo
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
