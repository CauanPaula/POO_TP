package entidades;
public class Tiro extends Entidade {

    private boolean acertou;

    public Tiro(int vida, int dano, double vel_x, double vel_y, double cadencia, double tam_x, double tam_y) {
        super(vida, dano, vel_x, vel_y, cadencia, tam_x, tam_y);
        this.acertou = false;
    }

    public void acertarAlvo(double x, double y) {
        if (this.colisao(x, y)) {
            this.acertou = true;
        }
    }

    public boolean getAcertou() {
        return this.acertou;
    }

    @Override 
    public void mover (){
        setPos_y(getPos_y() - getVel_y());
    }
}
