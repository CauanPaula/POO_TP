package entidades;
import java.awt.Image;

public class Tiro extends Entidade {

        private boolean acertou;
        private boolean emImpacto = false;
        private int tempoImpacto = 10;

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
 // Inicia o efeito de impacto
    public void iniciarImpacto(Image imagem) {
        this.emImpacto = true;
        setSprite(imagem);
    }

    // Verifica se o tiro está em impacto
    public boolean isEmImpacto() {
        return emImpacto;
    }

    // Controla a duração do impacto
    public boolean atualizarImpacto() {
        tempoImpacto--;
        return tempoImpacto <= 0;
    }
    @Override 
    public void mover (){
        setPos_y(getPos_y() - getVel_y());
    }
     public void trocarSpriteImpacto(Image imagem) {
        setSprite(imagem);}
}
