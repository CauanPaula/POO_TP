public class Entidade{
    private int vida;
    private int dano;
    private double vel_x;
    private double vel_y;
    private double pos_x;
    private double pos_y;
    private double cadencia;

    //construtor
    public Entidade(int vida, int dano, double vel_x, double vel_y, double cadencia){
        if (vida <1){
            throw new ArgumentException("Valor invalido para vida");
        }
        if (dano <1){
            throw new ArgumentException("Valor invalido para dano");
        }
        if (vel_x <0){
            throw new ArgumentException("Valor invalido para velocidade horizontal");
        }
        if (vel_y <0){
            throw new ArgumentException("Valor invalido para velocidade vertical");
        }
        if (cadencia <0){
            throw new ArgumentException("Valor invalido para cadencia");
        }
        this.vida = vida;
        this.dano = dano;
        this.vel_x = vel_x;
        this.vel_y = vel_y;
        this.pos_x = 0;
        this.pos_y = 0;
        this.cadencia = cadencia;
    }

    //funcoes de get
    public int getVida(){
        return this.vida;
    }
    public int getDano(){
        return this.dano;
    }
    public double getPos_x(){
        return this.pos_x;
    }
    public double getPos_y(){
        return this.pos_y;
    }
    public double getCadencia(){
        return this.cadencia;
    }

    //funcionalidades gerais
    public int receberDano(int dano){
        return 0;
    }
    public void mover(){
        this.pos_x = this.pos_x + vel_x;
        this.pos_y = this.pos_y + vel_y;
    }
    public void atirar(){
        System.out.println("Aguardar implementacao da classe tiro");
    }

    //funcoes de set
    public void setVida(int vida){
        this.vida = vida;
    }
    public void setDano(int dano){
        if (dano < 1){
            this.dano = dano;
        }
    }
    public void setVelocidade(double vel_x, double vel_y){
        if (vel_x <0){
            throw new ArgumentException("Valor invalido para velocidade horizontal");
        }
        if (vel_y <0){
            throw new ArgumentException("Valor invalido para velocidade vertical");
        }
        this.vel_x = vel_x;
        this.vel_y = vel_y;
    }
    public void setCadencia(double cadencia){
        if (cadencia < 0){
            throw new ArgumentException("Valor invalido para cadencia");
        }
        this.cadencia = cadencia;
    }
}
