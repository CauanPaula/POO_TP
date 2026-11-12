package motor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import entidades.Buffing;
import entidades.Inimigo;
import entidades.Jogador;
import entidades.Tiro;
import tipos.TipoDeBuff;
import tipos.TipoDeNave;

public class Jogo {
    private int tamanhoTelaX;
    private int tamanhoTelaY;

    private Random random = new Random();

    private Jogador jogador;
    private Score score;
    private GerenciadorTiros motorTiros;
    private GerenciadorColisoes motorColisoes;
    private TipoDeNave naveSelecionada;

    private ArrayList<Inimigo> enemies;
    private ArrayList<Buffing> buffs;
    private ArrayList<Buffing> buffsPendentes;

    private boolean gameOver;

    private boolean leftPressed, rightPressed, upPressed, downPressed, spacePressed;

    private int coolDownTiroJogador;
    private int enemySpawnTimer;
    private int buffSpawnTimer;

    public Jogo (int tamTelaX, int tamTelaY){
        this.enemies = new ArrayList<>();
        this.buffs = new ArrayList<>();
        this.buffsPendentes = new ArrayList<>();
        this.tamanhoTelaX = tamTelaX;
        this.tamanhoTelaY = tamTelaY;
        inicializarJogo();
    }

    public void inicializarJogo(){
        this.naveSelecionada = TipoDeNave.TIPO1;

        //Instanciaçoes
        this.jogador = new Jogador(3, 2, 6, 6, 1, naveSelecionada.ordinal(), 20, 15);
        this.jogador.setPos_x(500);
        this.jogador.setPos_y(420);

        this.score = new Score(0);
        score.setNomePlayer("Jogador 1"); 

        this.motorTiros = new GerenciadorTiros();
        this.motorColisoes = new GerenciadorColisoes();

        //Limpar listas
        this.enemies.clear();
        this.buffs.clear();

        this.gameOver = false;

        //Zerar variaveis de logica
        this.enemySpawnTimer = 0;
        this.buffSpawnTimer = 0;
        this.coolDownTiroJogador = 0;

        //Setar teclados
        this.leftPressed = false; 
        this.rightPressed = false;
        this.upPressed = false;
        this.downPressed = false;
        this.spacePressed = false;

        //Limpar listas de tiros
        this.motorTiros.reiniciar();
    }

    public void atualizar (){
        //Chamada a cada frame do jogo, atualiza a situação
        if(this.gameOver) return ;
        if (this.coolDownTiroJogador > 0) {this.coolDownTiroJogador--;}

        atualizarMovimentacaoDoJogador();
        gerarEAtualizarInimigos();
        gerarEAtualizarBuffs();
        atualizarTiros();
        verificarColisao();

        this.buffs.addAll(buffsPendentes);
        this.buffsPendentes.clear();

    }

    //Setar cliques para movimentações
    public void setLeft(boolean pressed) {this.leftPressed = pressed;}
    public void setRight(boolean pressed) {this.rightPressed = pressed;}
    public void setUp(boolean pressed) {this.upPressed = pressed;}
    public void setDown(boolean pressed) {this.downPressed = pressed;}
    public void setSpace(boolean pressed) {this.spacePressed = pressed;}

    public void atualizarMovimentacaoDoJogador(){

        if (this.upPressed && jogador.getPos_y() - jogador.getTam_y() >= 0) {
            this.jogador.moverCima();
        }
        if (this.downPressed && jogador.getPos_y() + jogador.getTam_y() <= this.tamanhoTelaY){
            this.jogador.moverBaixo();
        }
        if (this.leftPressed && jogador.getPos_x() - jogador.getTam_x() > 0){
            this.jogador.moverEsquerda();
        }
        if (this.rightPressed && jogador.getPos_x() + jogador.getTam_x() < this.tamanhoTelaX){
            this.jogador.moverDireita();
        }
        
    }


    public void atualizarTiros (){
        
        //Movimentação do tiro
        motorTiros.atualizarTiro(this.tamanhoTelaY);
        //Entidade atirando
        if (this.spacePressed && !this.gameOver && this.coolDownTiroJogador <= 0){
            this.jogador.disparar(motorTiros);
            
            //Calculo para quanto maior cadencia menor o cd de tiro
            this.coolDownTiroJogador = Math.max(4, (int) (20 / jogador.getCadencia()));
        }

        //Inimigo (atirador) atirando
        Iterator<Inimigo> inimigoIterator = this.enemies.iterator();
        while (inimigoIterator.hasNext()) {
            Inimigo inimigo = inimigoIterator.next();

            inimigo.atualizarCooldown();
            if (inimigo.getTipoInimigo() == 1 && inimigo.podeAtirar()){
                inimigo.disparar(motorTiros);
                inimigo.resetarCooldown();
            }
        }

    }


    public void gerarEAtualizarInimigos(){
        //Gerando  inimigos
        this.enemySpawnTimer++;

        if (this.enemySpawnTimer >= 40){
            int tipoDeInimigo = random.nextInt(2)+1;
            int vidaInimigo = tipoDeInimigo*4;
            int dano;
            double velY = (5 * tipoDeInimigo)/2;
            if (tipoDeInimigo == 1){
                dano = 1;
            } else {
                dano = 10;
            }

            Inimigo novoInimigo = new Inimigo(vidaInimigo, dano, 0, velY, 1, tipoDeInimigo, 15, 15);

            //Setar a posição do inimigo aleatoriamente no eixo X lidando com o nascimento nas bordas
            double enemyX = 20 + random.nextInt(Math.max(1, this.tamanhoTelaX - 40));
            novoInimigo.setPos_x(enemyX);
            novoInimigo.setPos_y(0);

            enemies.add(novoInimigo);
            this.enemySpawnTimer = 0;
        }

        //Atualizar posição dos inimigos
        Iterator <Inimigo> inimigoIter = enemies.iterator();
        while (inimigoIter.hasNext()) {
            Inimigo inimigo = inimigoIter.next();
            inimigo.mover();

            if (inimigo.getPos_y() - inimigo.getTam_y() > this.tamanhoTelaY) {
                inimigoIter.remove();
                continue;
            }
        }
    }

    public void gerarEAtualizarBuffs() {
        this.buffSpawnTimer++;

        if (this.buffSpawnTimer >= 1000){
            TipoDeBuff[] opcoesBuff = TipoDeBuff.values();
            int indiceSorteado = random.nextInt(opcoesBuff.length);

            Buffing buff = new Buffing(1, 0, 0, 3, 0, 10, 10, opcoesBuff[indiceSorteado]);

            double buffX = 20 + random.nextInt(Math.max(1, this.tamanhoTelaX - 40));
            buff.setPos_x(buffX);
            buff.setPos_y(0);

            this.buffsPendentes.add(buff);
            this.buffSpawnTimer = 0;
        }

        Iterator <Buffing> buffingIter = buffs.iterator();
        while (buffingIter.hasNext()) {
            Buffing buff = buffingIter.next();
            buff.mover();

            if (buff.getPos_y() - buff.getTam_y() > this.tamanhoTelaY) {
                buffingIter.remove();
                continue;
            }
        }
    }

    public void verificarColisao(){
        boolean jogadorMorreu = motorColisoes.verificarColisoes(this.jogador, this.enemies, this.motorTiros.getTirosJogador(), this.motorTiros.getTirosInimigos(), this.buffs, this.score);

        if (jogadorMorreu){
            this.gameOver = true;
        }
    }

    public Jogador getJogador() { return this.jogador; }
    public ArrayList<Inimigo> getEnemies() { return this.enemies; }
    public ArrayList<Buffing> getBuffs() { return this.buffs; }
    public ArrayList<Tiro> getBulletsJogador() { return this.motorTiros.getTirosJogador(); }
    public ArrayList<Tiro> getBulletsInimigo() { return this.motorTiros.getTirosInimigos(); }
    public boolean isGameOver() { return this.gameOver; }
    public Score getScore() { return this.score; }

}
