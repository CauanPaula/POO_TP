package entidades;
import motor.GerenciadorTiros;
import interfaces.Atirador;
import javax.swing.ImageIcon;
import java.awt.Image;
public class Jogador extends Entidade implements Atirador {

    private int tipoDaNave;
    private boolean estaAtirando;

    private Image imgFrente;
    private Image imgEsquerda;
    private Image imgDireita;
    private Image imgtras;

    public Jogador(int vida, int dano, double vel_x, double vel_y, double cadencia, int tipoDaNave, double tam_x, double tam_y) 
        {
        super(vida, dano, vel_x, vel_y, cadencia, tam_x, tam_y);
        this.tipoDaNave = tipoDaNave;
        this.estaAtirando = false;

        String pasta = "imagens/tipo" + (tipoDaNave + 1) + "/";

        this.imgFrente = new ImageIcon(pasta + "nave_pr.png").getImage();
        this.imgEsquerda = new ImageIcon(pasta + "nave_esq.png").getImage();
        this.imgDireita = new ImageIcon(pasta + "nave_dir.png").getImage();
        this.imgtras = new ImageIcon(pasta +"nave_tras.png").getImage();
            
        this.sprite = this.imgFrente;
    
        }
       

        
    
    

    public void moverCima() {
        setPos_y(this.getPos_y() - this.getVel_y());
        this.sprite = this.imgFrente;
    }

    public void moverBaixo() {
        setPos_y(this.getPos_y() + this.getVel_y());
        this.sprite = this.imgtras;
    }

    public void moverDireita() {
        setPos_x(this.getPos_x() + this.getVel_x());
        this.sprite = this.imgDireita;
    }

    public void moverEsquerda() {
        setPos_x(this.getPos_x() - this.getVel_x());
        this.sprite = this.imgEsquerda;
    }

    @Override 
 
public void disparar(GerenciadorTiros gerenciador) {

    double posY = getPos_y() - getTam_y() - 10;
    double velY = 10;

    String pasta = "imagens/tipoTiro/";

    Image imgInit = new ImageIcon(
        pasta + "tiro_init.png"
    ).getImage();
    
    Tiro newTiro = new Tiro(
        1,
        getDano(),
        0.0,
        velY,
        getCadencia(),
        30.0,
        35.0
    );

    newTiro.setSprite(imgInit);

    newTiro.setPos_x(getPos_x());
    newTiro.setPos_y(posY);

    gerenciador.adicionarTiroJogador(newTiro);
}
}