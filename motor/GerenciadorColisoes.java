package motor;

import entidades.Buffing;
import entidades.Entidade;
import entidades.Inimigo;
import entidades.Jogador;
import entidades.Tiro;
import java.awt.Image;
import java.util.Iterator;
import java.util.List;
import javax.swing.ImageIcon;

public class GerenciadorColisoes {
    private Image spriteImpacto = new ImageIcon(
    "imagens/tipoTiro/tiro_impacto.png"
    ).getImage();
    
    private boolean colidem(Entidade a, Entidade b) {
        return Math.abs(a.getPos_x() - b.getPos_x())
                <= a.getTam_x() + b.getTam_x()
            && Math.abs(a.getPos_y() - b.getPos_y())
                <= a.getTam_y() + b.getTam_y();
    }


    public boolean verificarColisoes(Jogador jogador,List<Inimigo> inimigos,List<Tiro> tiroJogador, List<Tiro> tiroInimigo,List<Buffing> buffs,Score score) {
            
            boolean jogadorMorreu = false;

            Iterator<Inimigo> inimigoIterator  = inimigos.iterator();

            while(inimigoIterator.hasNext()){
                Inimigo inimigo = inimigoIterator.next();

                if(colidem(jogador, inimigo)){
                    if(!jogador.receberDano(inimigo.getDano())){
                        jogadorMorreu = true;
                    }
                    motor.GerenciadorSom.tocarEfeito("sons/dano.wav");
                    inimigoIterator.remove();
                    continue;

                }


                Iterator<Tiro> tiroJogadorIterator = tiroJogador.iterator();

while(tiroJogadorIterator.hasNext()){
    Tiro tiro = tiroJogadorIterator.next();

    if(colidem(tiro, inimigo) && !tiro.isEmImpacto()){

        // Troca a imagem do tiro pela imagem de impacto
        tiro.iniciarImpacto(spriteImpacto);

        // Aplica o dano no inimigo
        boolean vivo = inimigo.receberDano(tiro.getDano());

        if(!vivo){
            score.pontuar(10 * inimigo.getTipoInimigo());
            GerenciadorSom.tocarEfeito("sons/inimigo_morte.wav");
            inimigoIterator.remove();
            break;
        }
    }
}


                Iterator<Tiro> tiroInimigoIterator = tiroInimigo.iterator();

                while(tiroInimigoIterator.hasNext()){
                    Tiro tiro = tiroInimigoIterator.next();

                    if(colidem(tiro,jogador)){
                        tiroInimigoIterator.remove();

                        boolean vivo = jogador.receberDano(tiro.getDano());

                        if(!vivo){
                            jogadorMorreu = true;
                            break;
                        }

                    }
                }

            }

            Iterator<Buffing> buffIterator = buffs.iterator();

            while(buffIterator.hasNext()){
                Buffing buff = buffIterator.next();

                if(colidem(jogador,buff)){
                    buff.ativarBuffing(jogador);
                    GerenciadorSom.tocarEfeito("sons/buff.wav");
                    buffIterator.remove();
                }
            }

            return jogadorMorreu;

        }



}
