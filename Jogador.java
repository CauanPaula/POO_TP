public class Jogador extends Entidade{
    private int tipoDaNave;
    private boolean estaAtirando;
    public Jogador(int vida, int dano, double vel_x, double vel_y, double cadencia, int tipoDaNave, double tam_x, double tam_y){
        super(vida, dano, vel_x, vel_y, cadencia, tam_x, tam_y);
        this.tipoDaNave = tipoDaNave;
        this.estaAtirando = false;
    }
    public void moverCima(){
        setPos_y(this.getPos_y()+this.getVel_y());
    }
    public void moverBaixo(){
        setPos_y(this.getPos_y()-this.getVel_y());
    }
    public void moverDireita(){
        setPos_x(this.getPos_x()+this.getVel_x());
    }
    public void moverEsquerda(){
        setPos_x(this.getPos_x()-this.getVel_x());
    }
}