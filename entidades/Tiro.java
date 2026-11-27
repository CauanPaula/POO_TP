package entidades;

import java.awt.Image;

public class Tiro extends Entidade {

    private boolean acertou;
    private boolean emImpacto = false;
    private int tempoImpacto = 10;

    private Image[] framesAnimacao;
    private Image[] framesImpacto;
    private int frameAtual = 0;
    private int contadorAnimacao = 0;

    public Tiro(int vida, int dano, double vel_x, double vel_y, double cadencia, double tam_x, double tam_y) {
        super(vida, dano, vel_x, vel_y, cadencia, tam_x, tam_y);
        this.acertou = false;
        this.escalaVisual = 1.7;
    }

    public void setFramesAnimacao(Image[] framesAnimacao) {
        this.framesAnimacao = framesAnimacao;
        if (framesAnimacao != null && framesAnimacao.length > 0) {
            setSprite(framesAnimacao[0]);
        }
    }

    public void setFramesImpacto(Image[] framesImpacto) {
        this.framesImpacto = framesImpacto;
    }

   private int direcaoVoo = 1;
    private boolean jaNasceu = false;

    private void atualizarAnimacao() {
        if (emImpacto) {
            contadorAnimacao++;
            if (framesImpacto != null && framesImpacto.length > 0) {
                // Mantém exatamente o ritmo de impacto que já ficou fluido
                if (contadorAnimacao >= 2) {
                    contadorAnimacao = 0;
                    int indice = Math.min(frameAtual, framesImpacto.length - 1);
                    setSprite(framesImpacto[indice]);
                    frameAtual++;
                }
            }
        } else if (framesAnimacao != null && framesAnimacao.length > 0) {
            // Voo em 60 FPS (1 frame por tick): nasce completo e depois pulsa na ponta
            int indiceSeguro = Math.min(frameAtual, framesAnimacao.length - 1);
            setSprite(framesAnimacao[indiceSeguro]);

            if (!jaNasceu) {
                frameAtual++;
                if (frameAtual >= framesAnimacao.length - 1) {
                    frameAtual = framesAnimacao.length - 1;
                    jaNasceu = true;
                    direcaoVoo = -1;
                }
            } else {
                int inicioPulso = Math.max(0, framesAnimacao.length - 6);
                frameAtual += direcaoVoo;

                if (frameAtual >= framesAnimacao.length - 1) {
                    frameAtual = framesAnimacao.length - 1;
                    direcaoVoo = -1;
                } else if (frameAtual <= inicioPulso) {
                    frameAtual = inicioPulso;
                    direcaoVoo = 1;
                }
            }
        }
    }

    public void acertarAlvo(double x, double y) {
        if (this.colisao(x, y)) {
            this.acertou = true;
        }
    }

    public boolean getAcertou() {
        return this.acertou;
    }

    // Inicia o efeito de impacto (usa framesImpacto se existirem, ou a imagem única como fallback)
    public void iniciarImpacto(Image imagemFallback) {
        this.emImpacto = true;
        this.frameAtual = 0;
        this.contadorAnimacao = 0;

        if (framesImpacto != null && framesImpacto.length > 0) {
            setSprite(framesImpacto[0]);
            // Faz a duração do impacto bater exatamente com a quantidade de frames (2 ticks por frame)
            this.tempoImpacto = framesImpacto.length * 2;
        } else if (imagemFallback != null) {
            setSprite(imagemFallback);
        }
    }

    public boolean isEmImpacto() {
        return emImpacto;
    }

    // Controla a duração e avança os quadros do impacto
    public boolean atualizarImpacto() {
        atualizarAnimacao();
        tempoImpacto--;
        return tempoImpacto <= 0;
    }

// Atualiza a posicao horizontal e vertical do tiro
    @Override 
    public void mover() {
       setPos_x(getPos_x() + getVel_x()); 
        setPos_y(getPos_y() - getVel_y());
        atualizarAnimacao();
    }

    public void trocarSpriteImpacto(Image imagem) {
        setSprite(imagem);
    }
}