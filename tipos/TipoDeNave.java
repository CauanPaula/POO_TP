package tipos;

public enum TipoDeNave {

    TIP1( 1.0, 1.0, 1.0 , 1.0 ), // vida 3, dano 2, vel 6, cooldown 20
    TIP2( 0.7, 1.0, 1.5 , 1.5 ), // vida 2, dano 2, vel 9, cooldown 13
    TIP3( 2.0, 1.5, 0.7 , 0.75 ); // vida 6, dano 3,vel 4.2, cooldown 26

    private final double multiplicaVida;
    private final double multiplicaDano;
    private final double multiplicaVelocidade;
    private final double multiplicaCadencia;


    TipoDeNave(double multiplicaVida, double multiplicaDano, double multiplicaVelocidade, double multiplicaCadencia){
        this.multiplicaVida = multiplicaVida;
        this.multiplicaDano = multiplicaDano;
        this.multiplicaVelocidade = multiplicaVelocidade;
        this.multiplicaCadencia = multiplicaCadencia;
    }

    public double getMultiplicaVida() {
         return multiplicaVida; 
    }
    public double getMultiplicaDano() {
         return multiplicaDano; 
    }
    public double getMultiplicaVelocidade() {
         return multiplicaVelocidade; 
    }
    public double getMultiplicaCadencia() {
         return multiplicaCadencia; 
    }
}
