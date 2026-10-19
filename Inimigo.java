public class Inimigo extends Entidade{
    private int tipoInimigo;
    public Inimigo(int vida, int dano, double vel_x, double vel_y, double cadencia, int tipoInimigo){
        super(vida, dano, vel_x, vel_y, cadencia);
        this.tipoInimigo = tipoInimigo;
    }
    public void setTipoInimigo(int tipoInimigo){
        this.tipoInimigo = tipoInimigo;
    }
    public int getTipoInimigo(){
        return this.tipoInimigo;
    }
}