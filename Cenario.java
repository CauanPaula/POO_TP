
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
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

        //Desenhar
        g.setColor(Color.GREEN);
        for (Buffing buff : buffs) {
            int bfX = (int) (buff.getPos_x() - buff.getTam_x());
            int bfY = (int) (buff.getPos_y() - buff.getTam_y());
            int bfW = (int) (buff.getTam_x() * 2);
            int bfH = (int) (buff.getTam_y() * 2);
            g.fillOval(bfX, bfY, bfW, bfH);
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
