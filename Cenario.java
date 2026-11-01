
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.*;

public class Cenario extends JPanel implements ActionListener, KeyListener {

    private Timer timer;
    private Jogador jogador;
    private Score score;
    private boolean leftPressed = false, rightPressed = false, upPressed = false, downPressed = false;
    //TEMPORARIO VERIFICAR POSTERIORMENTE ISSO DO TIPO DA NAVE
    private TipoDeNave naveSelecionada = TipoDeNave.TIPO1;

    //Lista para armazenar itens na tela
    private ArrayList<Tiro> bullets = new ArrayList<>();
    private ArrayList<Inimigo> enemies = new ArrayList<>();
    private ArrayList<Buffing> buffs = new ArrayList<>();

    private int enemySpawnTimer = 0;
    private boolean gameOver = false;
    private Random random = new Random();
    private int cooldownTiro = 0;

    public Cenario() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        inicializarJogo();

        // Timer para atualizar o jogo a ~60 FPS (16ms)
        timer = new Timer(16, this);
        timer.start();
    }

    public void inicializarJogo() {
        jogador = new Jogador(3, 2, 6, 6, 1, naveSelecionada.ordinal(), 20, 15);
        jogador.setPos_x(500);
        jogador.setPos_y(420);
        score = new Score(0);
        score.setNomePlayer("Jogador 1");

        bullets.clear();
        enemies.clear();
        buffs.clear();
        enemySpawnTimer = 0;
        gameOver = false;
        cooldownTiro = 0;
    }

    //COMO NÃO EXISTEM IMAGENS AINDA, O CODIGO DESENHA AS FIGURAS
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("FIM DE JOGO", 280, 260);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString(score.getNomePlayer() + " - Score Final: " + score.getScorePlayer(), 265, 310);
            g.drawString("Pressione R para reiniciar", 275, 350);
            return;
        }

        // Desenhar a nave do jogador centralizado
        int jX = (int) (jogador.getPos_x() - jogador.getTam_x());
        int jY = (int) (jogador.getPos_y() - jogador.getTam_y());
        int jLargura = (int) (jogador.getTam_x() * 2);
        int jAltura = (int) (jogador.getTam_y() * 2);

        g.setColor(Color.CYAN);
        g.fillRect(jX, jY, jLargura, jAltura);
        g.setColor(Color.BLUE);
        g.fillRect((int) jogador.getPos_x() - 5, jY - 10, 10, 10); // Canhão

        //Desenhar os tiros
        g.setColor(Color.YELLOW);
        for (Tiro b : bullets) {
            int bX = (int) (b.getPos_x() - b.getTam_x());
            int bY = (int) (b.getPos_y() - b.getTam_y());
            int bW = (int) (b.getTam_x() * 2);
            int bH = (int) (b.getTam_y() * 2);
            g.fillRect(bX, bY, bW, bH);
        }

        //Desenhar os inimigos
        for (Inimigo en : enemies) {
            switch (en.getTipoInimigo()) {
                case 2 ->
                    g.setColor(Color.ORANGE);
                case 3 ->
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

        // Desenhar HUD
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score: " + score.getScorePlayer(), 20, 30);
        g.drawString("Vida: " + jogador.getVida(), 20, 55);
        g.drawString("Dano: " + jogador.getDano(), 20, 80);
        g.drawString(String.format("Cadência: %.1f", jogador.getCadencia()), 20, 105);
    }

    // LIDAR COM ACOPLAMENTO
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) {
            repaint();
            return;
        }

        if (cooldownTiro > 0) {
            cooldownTiro--;
        }

        // Movimentação do jogador utilizando os métodos da classe Jogador
        if (leftPressed && jogador.getPos_x() - jogador.getTam_x() > 0) {
            jogador.moverEsquerda();
        }
        if (rightPressed && jogador.getPos_x() + jogador.getTam_x() < getWidth()) {
            jogador.moverDireita();
        }
        // Nota: em Jogador.java, moverBaixo() subtrai vel_y (sobe na tela) e moverCima() soma vel_y (desce na tela)
        if (upPressed && jogador.getPos_y() - jogador.getTam_y() > 0) {
            jogador.moverBaixo();
        }
        if (downPressed && jogador.getPos_y() + jogador.getTam_y() < getHeight()) {
            jogador.moverCima();
        }

        // Atualizar posição dos tiros (movem-se para cima subtraindo vel_y)
        Iterator<Tiro> bIter = bullets.iterator();
        while (bIter.hasNext()) {
            Tiro b = bIter.next();
            b.setPos_y(b.getPos_y() - b.getVel_y());
            if (b.getPos_y() < 0) {
                bIter.remove();
            }
        }

        // Gerar inimigos periodicamente
        enemySpawnTimer++;
        if (enemySpawnTimer > 40) {
            int tipo = random.nextInt(3) + 1;
            int vidaInimigo = tipo * 10;
            double velY = 2.0 + (4 - tipo);

            Inimigo novoInimigo = new Inimigo(vidaInimigo, 10 * tipo, 0.0, velY, 1.0, tipo, 15.0, 15.0);
            double enemyX = 20 + random.nextInt(Math.max(1, getWidth() - 40));
            novoInimigo.setPos_x(enemyX);
            novoInimigo.setPos_y(0);

            enemies.add(novoInimigo);
            enemySpawnTimer = 0;
        }

        // Atualizar posição dos inimigos e verificar colisões
        Iterator<Inimigo> eIter = enemies.iterator();
        while (eIter.hasNext()) {
            Inimigo en = eIter.next();
            en.mover(); // Usa o método mover() de Entidade (pos_y += vel_y)

            // Remover se sair da tela
            if (en.getPos_y() - en.getTam_y() > getHeight()) {
                eIter.remove();
                continue;
            }

            // Verificar colisão do inimigo com o jogador
            if (jogador.colisao(en.getPos_x(), en.getPos_y()) || en.colisao(jogador.getPos_x(), jogador.getPos_y())) {
                boolean vivo = jogador.receberDano(en.getDano());
                eIter.remove();
                if (!vivo) {
                    gameOver = true;
                }
                continue;
            }

            // Verificar colisão do inimigo com os tiros
            Iterator<Tiro> bIter2 = bullets.iterator();
            boolean inimigoDestruido = false;
            while (bIter2.hasNext()) {
                Tiro b = bIter2.next();
                b.acertarAlvo(en.getPos_x(), en.getPos_y());

                if (b.getAcertou() || en.colisao(b.getPos_x(), b.getPos_y())) {
                    bIter2.remove();
                    boolean aindaVivo = en.receberDano(b.getDano());
                    if (!aindaVivo) {
                        inimigoDestruido = true;
                        score.pontuar(10 * en.getTipoInimigo());
                        // tentarGerarBuff(en.getPos_x(), en.getPos_y(), en.getTipoInimigo());
                        break;
                    }
                }
            }

            if (inimigoDestruido) {
                eIter.remove();
            }
        }

        // Atualizar buffs e verificar coleta pelo jogador
        Iterator<Buffing> buffIter = buffs.iterator();
        while (buffIter.hasNext()) {
            Buffing buff = buffIter.next();
            buff.mover();

            if (buff.getPos_y() - buff.getTam_y() > getHeight()) {
                buffIter.remove();
                continue;
            }

            if (jogador.colisao(buff.getPos_x(), buff.getPos_y()) || buff.colisao(jogador.getPos_x(), jogador.getPos_y())) {
                buff.ativarBuffing(jogador);
                buffIter.remove();
            }
        }

        repaint();
    }

    //Verificacao de teclas para movimentacao e disparo
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
            leftPressed = true;
        }
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            rightPressed = true;
        }
        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
            upPressed = true;
        }
        if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
            downPressed = true;
        }

        if (key == KeyEvent.VK_SPACE && !gameOver && cooldownTiro <= 0) {
            Tiro novoTiro = new Tiro(
                    1,
                    jogador.getDano(),
                    0.0,
                    10.0,
                    jogador.getCadencia(),
                    naveSelecionada.ordinal() + 1,
                    3.0,
                    8.0
            );
            novoTiro.setPos_x(jogador.getPos_x());
            novoTiro.setPos_y(jogador.getPos_y() - jogador.getTam_y() - 10);
            bullets.add(novoTiro);

            //Calculo para quanto maior cadencia menor o cd de tiro
            cooldownTiro = Math.max(4, (int) (20 / jogador.getCadencia()));
        }

        if (key == KeyEvent.VK_R && gameOver) {
            inicializarJogo();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            rightPressed = false;
        }
        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
            upPressed = false;
        }
        if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
            downPressed = false;
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
