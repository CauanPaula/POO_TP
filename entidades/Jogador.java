package entidades;
import motor.GerenciadorTiros;
import interfaces.Atirador;

public class Jogador extends Entidade implements Atirador {

    private int tipoDaNave;
    private boolean estaAtirando;

    public Jogador(int vida, int dano, double vel_x, double vel_y, double cadencia, int tipoDaNave, double tam_x, double tam_y) {
        super(vida, dano, vel_x, vel_y, cadencia, tam_x, tam_y);
        this.tipoDaNave = tipoDaNave;
        this.estaAtirando = false;
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
