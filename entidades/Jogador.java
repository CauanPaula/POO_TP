package entidades;

import motor.GerenciadorTiros;
import interfaces.Atirador;
import tipos.TipoDeNave;

public class Jogador extends Entidade implements Atirador {


    private static final int VidaBase = 3;
    private static final int DanoBase = 2;
    private static final double VelocidadeBase = 6.0;
    private static final double CadenciaBase = 1.0;

    private final TipoDeNave tipo;
    private boolean estaAtirando;

    public Jogador(TipoDeNave tipo, double tam_x, double tam_y) {
        super(Math.max(1, (int) Math.round(VidaBase * tipo.getMultiplicaVida())),
            Math.max(1, (int) Math.round(DanoBase * tipo.getMultiplicaDano())),
            VelocidadeBase * tipo.getMultiplicaVelocidade(),
            VelocidadeBase * tipo.getMultiplicaVelocidade(),
            CadenciaBase * tipo.getMultiplicaCadencia(),
            tam_x, tam_y);
        this.tipo = tipo;
        this.estaAtirando = false;
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

    @Override 
    public void disparar (GerenciadorTiros gerenciador){
        double posY = getPos_y() - getTam_y() - 10;
        double velY = 10;

        Tiro newTiro = new Tiro(1,
                    getDano(),
                    0.0,
                    velY,
                    getCadencia(),
                    3.0,
                    8.0);
        
        newTiro.setPos_x(getPos_x());
        newTiro.setPos_y(posY);

        gerenciador.adicionarTiroJogador(newTiro);
    }
}
