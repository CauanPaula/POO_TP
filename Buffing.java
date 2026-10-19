public class Buffing extends Entidade{
    private TipoDeBuff tipoDeBuff;
    public Buffing(int vida, int dano, double vel_x, double vel_y, double cadencia, int tipoInimigo, double tam_x, double tam_y, TipoDeBuff tipoDeBuff){
        super(vida, dano, vel_x, vel_y, cadencia, tam_x, tam_y);
        this.tipoDeBuff = tipoDeBuff;
    }
    public Void ativarBuffing(Jogador p1){
        if(this.tipoDeBuff == TipoDeBuff.VIDA){
            p1.setVida(p1.getVida()+this.getCadencia());
        }else if(this.tipoDeBuff == TipoDeBuff.DANO){
            p1.setDano(p1.getDano()+this.getCadencia());
        }else if(this.tipoDeBuff == TipoDeBuff.CADENCIA){
            p1.setCadencia(p1.getCadencia()+this.getCadencia());
        }
    }
}