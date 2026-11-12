package entidades;
import tipos.TipoDeBuff;

public class Buffing extends Entidade {

    private final TipoDeBuff tipoDeBuff;

    public Buffing(int vida, int dano, double vel_x, double vel_y, double cadencia, double tam_x, double tam_y, TipoDeBuff tipoDeBuff) {
        super(vida, dano, vel_x, vel_y, cadencia, tam_x, tam_y);
        this.tipoDeBuff = tipoDeBuff;
    }

    public void ativarBuffing(Jogador p1) {
        switch (this.tipoDeBuff) {
            case VIDA -> {
                p1.setVida(p1.getVida() + 1);
            }
            case DANO -> {
                p1.setDano(p1.getDano() + 1);
            }
            case CADENCIA -> {
                p1.setCadencia(p1.getCadencia() + 0.25);
            }
        }
    }
}
