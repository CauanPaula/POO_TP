package entidades;

import motor.GerenciadorTiros;
import interfaces.Atirador;
import javax.swing.ImageIcon;
import java.awt.Image;
import tipos.TipoDeNave;

public class Jogador extends Entidade implements Atirador {

    private static final int VidaBase = 5;
    private static final int DanoBase = 2;
    private static final double VelocidadeBase = 6.0;
    private static final double CadenciaBase = 1.0;

    private final TipoDeNave tipo;
    private boolean estaAtirando;

    // 1. Declaração das imagens (tinha sido removida no merge)
    private Image imgFrente;
    private Image imgEsquerda;
    private Image imgDireita;
    private Image imgtras;
    private Image imgTiro;

    public Jogador(TipoDeNave tipo, double tam_x, double tam_y) {
        super(Math.max(1, (int) Math.round(VidaBase * tipo.getMultiplicaVida())),
            Math.max(1, (int) Math.round(DanoBase * tipo.getMultiplicaDano())),
            VelocidadeBase * tipo.getMultiplicaVelocidade(),
            VelocidadeBase * tipo.getMultiplicaVelocidade(),
            CadenciaBase * tipo.getMultiplicaCadencia(),
            tam_x, tam_y);
        this.tipo = tipo;
        this.estaAtirando = false;

        // 2. Carregamento das imagens da nave selecionada (tipo1, tipo2, tipo3...)
        String pasta = "imagens/tipo" + (tipo.ordinal() + 1) + "/";
        this.imgFrente = new ImageIcon(pasta + "nave_pr.png").getImage();
        this.imgEsquerda = new ImageIcon(pasta + "nave_esq.png").getImage();
        this.imgDireita = new ImageIcon(pasta + "nave_dir.png").getImage();
        this.imgtras = new ImageIcon(pasta + "nave_tras.png").getImage();

        // Carrega a imagem do tiro uma única vez na memória
        this.imgTiro = new ImageIcon("imagens/tipoTiro/tiro_init.png").getImage();

        // Define o sprite inicial da nave
        this.sprite = this.imgFrente;
    }

    public TipoDeNave getTipo() {
        return this.tipo;
    }

    public void moverCima() {
        setPos_y(this.getPos_y() - this.getVel_y());
        this.sprite = this.imgFrente;
    }

    public void moverBaixo() {
        setPos_y(this.getPos_y() + this.getVel_y());
        this.sprite = this.imgtras;
    }

    public void moverDireita() {
        setPos_x(this.getPos_x() + this.getVel_x());
        this.sprite = this.imgDireita;
    }

    public void moverEsquerda() {
        setPos_x(this.getPos_x() - this.getVel_x());
        this.sprite = this.imgEsquerda;
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

        // Reutiliza a imagem já carregada (evita travamentos ao atirar rápido)
        newTiro.setSprite(this.imgTiro);
        newTiro.setPos_x(getPos_x());
        newTiro.setPos_y(posY);

        gerenciador.adicionarTiroJogador(newTiro);
    }
}