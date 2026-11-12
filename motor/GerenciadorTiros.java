package motor;

import java.util.ArrayList;
import java.util.Iterator;

import entidades.Tiro;


public class GerenciadorTiros {

    private ArrayList<Tiro> tirosDoJogador = new ArrayList<>();
    private ArrayList<Tiro> tirosDosInimigos = new ArrayList<>();
    

    public ArrayList<Tiro> getTirosJogador(){
        return this.tirosDoJogador;
    }
    public ArrayList<Tiro> getTirosInimigos(){
        return this.tirosDosInimigos;
    }
    
    public void atualizarTiro(int tamanhoTelaY){

        Iterator<Tiro> iterator = tirosDoJogador.iterator();
        while(iterator.hasNext()){
            Tiro tiro = iterator.next();

            tiro.mover();

            if(tiro.getPos_y() - tiro.getTam_y() < 0){
                iterator.remove();
            }

        }
        Iterator<Tiro> iteratorInimigo = tirosDosInimigos.iterator();
        while(iteratorInimigo.hasNext()){
            Tiro tiroInimigo = iteratorInimigo.next();

            tiroInimigo.mover();

            if(tiroInimigo.getPos_y() + tiroInimigo.getTam_y() > tamanhoTelaY){
                iteratorInimigo.remove();
            }
        }
    }


    public void adicionarTiroJogador(Tiro tiro) {
        tirosDoJogador.add(tiro);
    }
    public void adicionarTiroInimigo(Tiro tiro) {
        tirosDosInimigos.add(tiro);
    }




    public void reiniciar(){
            tirosDoJogador.clear();
            tirosDosInimigos.clear();
    }



}








