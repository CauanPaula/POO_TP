
public class Buffing extends Entidade {

    private TipoDeBuff tipoDeBuff;

    public Buffing(int vida, int dano, double vel_x, double vel_y, double cadencia, int tipoInimigo, double tam_x, double tam_y, TipoDeBuff tipoDeBuff) {
        super(vida, dano, vel_x, vel_y, cadencia, tam_x, tam_y);
        this.tipoDeBuff = tipoDeBuff;
    }

    public void ativarBuffing(Jogador p1) {
        switch (this.tipoDeBuff) {
            case VIDA -> {
                p1.setVida(p1.getVida() + (int) this.getCadencia());
            }
            case DANO -> {
                p1.setDano(p1.getDano() + (int) this.getCadencia());
            }
            case CADENCIA -> {
                p1.setCadencia(p1.getCadencia() + this.getCadencia());
            }
        }
    }
}
