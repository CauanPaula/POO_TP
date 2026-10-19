public class Entidade{
    private int vida;
    private int dano;
    private double vel_x;
    private double vel_y;
    private double pos_x;
    private double pos_y;
    private double cadencia;
    private double tam_x;
    private double tam_y; 

    //construtor
    public Entidade(int vida, int dano, double vel_x, double vel_y, double cadencia, double tam_x, double tam_y){
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
        if (tam_x <0){
            throw new ArgumentException("Valor invalido para tamanho horizontal");
        }
        if (tam_y <0){
            throw new ArgumentException("Valor invalido para tamanho vertical");
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
        this.tam_x = tam_x;
        this.tam_y = tam_y;
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
    public double getVel_x(){
        return this.vel_x;
    }
    public double getVel_y(){
        return this.vel_y;
    }
    public double getTam_x(){
        return this.tam_x;
    }
    public double getTam_y(){
        return this.tam_y;
    }

    //funcionalidades gerais
    public boolean receberDano(int dano){
        this.vida -= dano;
        if (this.vida<1){
            return false;
        }
        return true;
    }
    public void mover(){
        this.pos_x = this.pos_x + vel_x;
        this.pos_y = this.pos_y + vel_y;
    }
    public void atirar(){
        System.out.println("Aguardar implementacao da classe tiro");
    }
    public boolean colisao(double x, double y){
        if(this.pos_x -this.tam_x <= x && x <= this.pos_x + this.tam_x && this.pos_y -this.tam_y <= y && y <= this.pos_y+ this.tam_y){
            return  true;
        }
        return false;
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
    public void setPos_x(double pos_x){
        this.pos_x = pos_x;
    }
    public void setPos_y(double pos_y){
        this.pos_y = pos_y;
    }
}
