package entidades;
import motor.GerenciadorTiros;
import interfaces.Atirador;

public class Inimigo extends Entidade implements Atirador {

    private int tipoInimigo;
    private int cooldownAtual;

    public Inimigo(int vida, int dano, double vel_x, double vel_y, double cadencia, int tipoInimigo, double tam_x, double tam_y) {
        super(vida, dano, vel_x, vel_y, cadencia, tam_x, tam_y);
        this.tipoInimigo = tipoInimigo;
        this.cooldownAtual = 0;
    }

    public void setTipoInimigo(int tipoInimigo) {
        this.tipoInimigo = tipoInimigo;
    }

    public int getTipoInimigo() {
        return this.tipoInimigo;
    }
    @Override 
    public void disparar (GerenciadorTiros gerenciador){
        double posY = getPos_y() + getTam_y();
        double velY = -8;

        Tiro newTiro = new Tiro(1,
                    getDano(),
                    0.0,
                    velY,
                    getCadencia(),
                    3.0,
                    8.0);
        
        newTiro.setPos_x(getPos_x());
        newTiro.setPos_y(posY);

        gerenciador.adicionarTiroInimigo(newTiro);
    }

    public void atualizarCooldown() {
        if (this.cooldownAtual > 0) {
            this.cooldownAtual--;
        }
    }

    public boolean podeAtirar() {
        return this.cooldownAtual <= 0;
    }

    public void resetarCooldown() {
        this.cooldownAtual = 50; 
    }
}
