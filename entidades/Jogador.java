package entidades;

import interfaces.Atirador;
import java.awt.Image;
import java.io.File;
import java.util.Arrays;
import javax.swing.ImageIcon;
import motor.GerenciadorTiros;
import tipos.TipoDeNave;

public class Jogador extends Entidade implements Atirador {

    private static final int VidaBase = 5;
    private static final int DanoBase = 2;
    private static final double VelocidadeBase = 6.0;
    private static final double CadenciaBase = 1.0;

    private final TipoDeNave tipo;

    private Image[] framesIdle;
    private Image[] framesFire;
    private Image[] framesImpact; 
    private Image imgTiro;

    private int frameAtual = 0;
    private int contadorAnimacao = 0;
    private int direcaoIdle = 1;

    public Jogador(TipoDeNave tipo, double tam_x, double tam_y) {
        super(Math.max(1, (int) Math.round(VidaBase * tipo.getMultiplicaVida())),
            Math.max(1, (int) Math.round(DanoBase * tipo.getMultiplicaDano())),
            VelocidadeBase * tipo.getMultiplicaVelocidade(),
            VelocidadeBase * tipo.getMultiplicaVelocidade(),
            CadenciaBase * tipo.getMultiplicaCadencia(),
            tam_x, tam_y);
        this.tipo = tipo;

        String pastaBase = "imagens/" + tipo.name().toLowerCase();
        this.framesIdle = carregarPastaFrames(pastaBase + "/idle");
        this.framesFire = carregarPastaFrames(pastaBase + "/fire");
        this.framesImpact = carregarPastaFrames(pastaBase + "/impact");

        this.imgTiro = new ImageIcon("imagens/tipoTiro/tiro_init.png").getImage();

        if (this.framesIdle.length > 0) {
            this.sprite = this.framesIdle[0];
        }
    }

    private Image[] carregarPastaFrames(String caminhoPasta) {
        File pasta = new File(caminhoPasta);
        File[] arquivos = pasta.listFiles((dir, nome) -> nome.toLowerCase().endsWith(".png"));

        if (arquivos == null || arquivos.length == 0) {
            return new Image[0];
        }

        Arrays.sort(arquivos);
        Image[] imagens = new Image[arquivos.length];
        for (int i = 0; i < arquivos.length; i++) {
            imagens[i] = new ImageIcon(arquivos[i].getPath()).getImage();
        }
        return imagens;
    }

    public TipoDeNave getTipo() {
        return this.tipo;
    }

    public void moverCima() {
        setPos_y(this.getPos_y() - this.getVel_y());
    }

    public void moverBaixo() {
        setPos_y(this.getPos_y() + this.getVel_y());
    }

    public void moverDireita() {
        setPos_x(this.getPos_x() + this.getVel_x());
    }

    public void moverEsquerda() {
        setPos_x(this.getPos_x() - this.getVel_x());
    }

    public void atualizarAnimacao() {
        if (framesIdle.length == 0) return;

        contadorAnimacao++;
        if (contadorAnimacao >= 7) {
            contadorAnimacao = 0;

            if (frameAtual >= framesIdle.length) {
                frameAtual = framesIdle.length - 1;
            }

            this.sprite = framesIdle[frameAtual];
            frameAtual += direcaoIdle;

            if (frameAtual >= framesIdle.length - 1) {
                frameAtual = framesIdle.length - 1;
                direcaoIdle = -1;
            } else if (frameAtual <= 0) {
                frameAtual = 0;
                direcaoIdle = 1;
            }
        }
    }

    @Override
    public void disparar(GerenciadorTiros gerenciador) {
        double posY = getPos_y() - getTam_y() - 10;
        double velY = 10;

        Tiro newTiro = new Tiro(
            1,
            getDano(),
            0.0,
            velY,
            getCadencia(),
            30.0,
            35.0
        );
         

        if (this.framesFire.length > 0) {
            newTiro.setFramesAnimacao(this.framesFire);
        } else {
            newTiro.setSprite(this.imgTiro);
        }
        if (this.framesImpact.length > 0) {
             newTiro.setFramesImpacto(this.framesImpact);
        }

        newTiro.setPos_x(getPos_x());
        newTiro.setPos_y(posY);
        
        gerenciador.adicionarTiroJogador(newTiro);
        motor.GerenciadorSom.tocarEfeito("sons/disparo2.wav");
    }
}