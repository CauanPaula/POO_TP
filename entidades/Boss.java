package entidades;

import interfaces.Atirador;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import motor.GerenciadorTiros;

public class Boss extends Entidade implements Atirador {

    private enum Estado {
        ENTRANDO,
        PATRULHA,
        ATAQUE_BOLINHAS,
        PREPARANDO_INVESTIDA,
        ATAQUE_INVESTIDA,
        ATAQUE_INVOCAR
    }

// Proporcao da imagem 64x80 (aumentado para 208x260 na tela)
    private static final double TAM_X_BOSS = 104.0;
    private static final double TAM_Y_BOSS = 130.0;

    // Altura no topo para mostrar so as pincas e parte do rosto
    private static final double Y_TOPO_PATRULHA = -55.0;

    private final int larguraTela;
    private final int alturaTela;
    private final int vidaMaxima;
    private final Random random = new Random();

    // Listas para guardar as animacoes e o tiro
    private final List<Image[]> animacoesPorEstado = new ArrayList<>();
    private final List<Image> framesSequenciaUnica = new ArrayList<>();
    private final boolean usarSequenciaUnica;
    private final Image[] framesProjetil;

    // Variaveis de controle de estado e animacao
    private Estado estadoAtual = Estado.ENTRANDO;
    private int animacaoAtualIndex = 0;
    private int frameAtual = 0;
    private int direcaoAnimacao = 1;
    private int contadorAnimacao = 0;

    private int direcaoHorizontal = 1;
    private int timerEstado = 0;
    private int cooldownAtaque = 90;
    private int cooldownDanoContato = 0;
    private int ultimoAtaque = -1;

    // construtor
    public Boss(int vida, int dano, int larguraTela, int alturaTela) {
        super(vida, dano, 3.2, 0.0, 1.0, TAM_X_BOSS, TAM_Y_BOSS);
        this.vidaMaxima = vida;
        this.larguraTela = larguraTela;
        this.alturaTela = alturaTela;

        // Carrega as animacoes de imagens/boss/escorpiao1 ate 4
        boolean encontrouPastas = false;
        for (int i = 1; i <= 4; i++) {
            Image[] framesPasta = carregarPastaFrames("imagens/boss/escorpiao" + i);
            animacoesPorEstado.add(framesPasta);
            if (framesPasta.length > 0) {
                encontrouPastas = true;
            }
        }

        // Caso use 4 imagens soltas em vez de 4 pastas
        if (!encontrouPastas) {
            for (int i = 1; i <= 4; i++) {
                Image img = carregarImagemUnica("imagens/boss/escorpiao" + i);
                if (img != null) {
                    framesSequenciaUnica.add(img);
                }
            }
            if (framesSequenciaUnica.isEmpty()) {
                Image[] soltos = carregarPastaFrames("imagens/boss");
                framesSequenciaUnica.addAll(Arrays.asList(soltos));
            }
        }

        this.usarSequenciaUnica = !encontrouPastas && !framesSequenciaUnica.isEmpty();

        // Carrega a imagem das bolinhas
        this.framesProjetil = carregarPastaFrames("imagens/boss/atk");

        // Define o sprite inicial
        Image[] iniciais = getFramesAtuais();
        if (iniciais.length > 0) {
            this.sprite = iniciais[0];
        }

        // Posicao inicial no topo da tela
        setPos_x(larguraTela / 2.0);
        setPos_y(-TAM_Y_BOSS - 10);
    }

    // funcoes para carregar imagens
    private Image carregarImagemArquivo(File arquivo) {
        try {
            Image img = ImageIO.read(arquivo);
            if (img != null) {
                return img;
            }
        } catch (Exception ignored) {
        }
        return new ImageIcon(arquivo.getPath()).getImage();
    }

    private Image carregarImagemUnica(String caminhoSemExtensao) {
        String[] extensoes = {".png", ".PNG", ".webp", ".jpg", ".jpeg"};
        for (String ext : extensoes) {
            File f = new File(caminhoSemExtensao + ext);
            if (f.exists() && f.isFile()) {
                return carregarImagemArquivo(f);
            }
        }
        return null;
    }

    private Image[] carregarPastaFrames(String caminhoPasta) {
        File pasta = new File(caminhoPasta);
        if (!pasta.exists() || !pasta.isDirectory()) {
            return new Image[0];
        }

        File[] arquivos = pasta.listFiles((dir, nome) -> {
            String lower = nome.toLowerCase();
            return lower.endsWith(".png") || lower.endsWith(".webp")
                    || lower.endsWith(".jpg") || lower.endsWith(".jpeg");
        });

        if (arquivos == null || arquivos.length == 0) {
            return new Image[0];
        }

        // Ordena os frames em sequencia
        Arrays.sort(arquivos, Comparator.comparing(File::getName, (a, b) -> {
            String numA = a.replaceAll("\\D+", "");
            String numB = b.replaceAll("\\D+", "");
            if (!numA.isEmpty() && !numB.isEmpty()) {
                int cmp = Integer.compare(Integer.parseInt(numA), Integer.parseInt(numB));
                if (cmp != 0) return cmp;
            }
            return a.compareToIgnoreCase(b);
        }));

        Image[] imagens = new Image[arquivos.length];
        for (int i = 0; i < arquivos.length; i++) {
            imagens[i] = carregarImagemArquivo(arquivos[i]);
        }
        return imagens;
    }

    // funcoes de animacao
    private void trocarAnimacao(int indice) {
        if (usarSequenciaUnica) return;
        if (indice < 0 || indice >= animacoesPorEstado.size()) return;
        if (animacoesPorEstado.get(indice).length == 0) return;

        if (this.animacaoAtualIndex != indice) {
            this.animacaoAtualIndex = indice;
            this.frameAtual = 0;
            this.direcaoAnimacao = 1;
            this.contadorAnimacao = 0;
        }
    }

    private Image[] getFramesAtuais() {
        if (usarSequenciaUnica) {
            return framesSequenciaUnica.toArray(new Image[0]);
        }
        if (animacoesPorEstado.isEmpty()) {
            return new Image[0];
        }
        Image[] atual = animacoesPorEstado.get(animacaoAtualIndex);
        if (atual.length == 0) {
            for (Image[] fallback : animacoesPorEstado) {
                if (fallback.length > 0) return fallback;
            }
        }
        return atual;
    }

    private void atualizarAnimacao() {
        Image[] frames = getFramesAtuais();
        if (frames.length == 0) return;

        if (frames.length == 1) {
            this.sprite = frames[0];
            return;
        }

        int ticksPorFrame = switch (estadoAtual) {
            case ATAQUE_INVESTIDA -> 3;
            case ATAQUE_BOLINHAS, ATAQUE_INVOCAR -> 5;
            default -> 8;
        };

        contadorAnimacao++;
        if (contadorAnimacao >= ticksPorFrame) {
            contadorAnimacao = 0;

            if (frameAtual >= frames.length) {
                frameAtual = frames.length - 1;
            }

            this.sprite = frames[frameAtual];
            frameAtual += direcaoAnimacao;

            if (frameAtual >= frames.length - 1) {
                frameAtual = frames.length - 1;
                direcaoAnimacao = -1;
            } else if (frameAtual <= 0) {
                frameAtual = 0;
                direcaoAnimacao = 1;
            }
        }
    }

    // Atualiza o comportamento e ataques do boss a cada frame
    public void atualizar(GerenciadorTiros motorTiros, List<Inimigo> enemies) {
        if (cooldownDanoContato > 0) {
            cooldownDanoContato--;
        }

        atualizarAnimacao();

        switch (estadoAtual) {
            case ENTRANDO -> {
                trocarAnimacao(0);
                if (getPos_y() < Y_TOPO_PATRULHA) {
                    setPos_y(Math.min(Y_TOPO_PATRULHA, getPos_y() + 2.5));
                } else {
                    estadoAtual = Estado.PATRULHA;
                    cooldownAtaque = 75;
                }
            }

            case PATRULHA -> {
                trocarAnimacao(0);
                moverHorizontalNoTopo();

                cooldownAtaque--;
                if (cooldownAtaque <= 0) {
                    escolherProximoAtaque();
                }
            }

            case ATAQUE_BOLINHAS -> {
                trocarAnimacao(1);
                moverHorizontalNoTopo();
                timerEstado++;

                if (timerEstado == 15 || timerEstado == 40) {
                    dispararLequeBolinhas(motorTiros, timerEstado == 40);
                }

                if (timerEstado >= 60) {
                    finalizarAtaque();
                }
            }

            case PREPARANDO_INVESTIDA -> {
                trocarAnimacao(2);
                timerEstado++;
                // Pequeno recuo antes de descer correndo
                setPos_y(Y_TOPO_PATRULHA - Math.min(20, timerEstado));

                if (timerEstado >= 25) {
                    estadoAtual = Estado.ATAQUE_INVESTIDA;
                    timerEstado = 0;
                }
            }

            case ATAQUE_INVESTIDA -> {
                // Desce a tela de norte a sul mostrando o corpo inteiro
                trocarAnimacao(2);
                setPos_y(getPos_y() + 11.5);

                if (getPos_y() - getTam_y() > alturaTela) {
                    setPos_x(TAM_X_BOSS + random.nextInt(Math.max(1, (int) (larguraTela - TAM_X_BOSS * 2))));
                    setPos_y(-TAM_Y_BOSS - 10);
                    estadoAtual = Estado.ENTRANDO;
                    timerEstado = 0;
                }
            }

            case ATAQUE_INVOCAR -> {
                trocarAnimacao(3);
                timerEstado++;

                if (timerEstado == 20) {
                    invocarDoisInimigos(enemies);
                }

                if (timerEstado >= 45) {
                    finalizarAtaque();
                }
            }
        }
    }

    // Movimenta da esquerda para a direita no topo
    private void moverHorizontalNoTopo() {
        double proximoX = getPos_x() + (getVel_x() * direcaoHorizontal);
        double limiteEsquerdo = getTam_x();
        double limiteDireito = larguraTela - getTam_x();

        if (proximoX <= limiteEsquerdo) {
            proximoX = limiteEsquerdo;
            direcaoHorizontal = 1;
        } else if (proximoX >= limiteDireito) {
            proximoX = limiteDireito;
            direcaoHorizontal = -1;
        }

        setPos_x(proximoX);
    }

    // Sorteia o proximo ataque sem repetir o anterior
    private void escolherProximoAtaque() {
        int ataque;
        do {
            ataque = random.nextInt(3);
        } while (ataque == ultimoAtaque);

        ultimoAtaque = ataque;
        timerEstado = 0;

        switch (ataque) {
            case 0 -> estadoAtual = Estado.ATAQUE_BOLINHAS;
            case 1 -> estadoAtual = Estado.PREPARANDO_INVESTIDA;
            case 2 -> estadoAtual = Estado.ATAQUE_INVOCAR;
        }
    }

    private void finalizarAtaque() {
        estadoAtual = Estado.PATRULHA;
        timerEstado = 0;
        cooldownAtaque = 110;
    }

    // Ataque 1: joga bolinhas espalhadas pela tela
    private void dispararLequeBolinhas(GerenciadorTiros gerenciador, boolean alternado) {
        double origemX = getPos_x();
        double origemY = getPos_y() + getTam_y() - 10;

        double[] angulos = alternado
                ? new double[]{35, 55, 75, 90, 105, 125, 145}
                : new double[]{25, 45, 65, 85, 95, 115, 135, 155};

        double velocidadeTotal = 6.5;

        for (double graus : angulos) {
            double rad = Math.toRadians(graus);
            double vx = Math.cos(rad) * velocidadeTotal;
            double vy = -Math.sin(rad) * velocidadeTotal;

            Tiro bolinha = new Tiro(1, 1, vx, vy, 1.0, 10.0, 10.0);
            if (framesProjetil.length > 0) {
                bolinha.setFramesAnimacao(framesProjetil);
            }
            bolinha.setPos_x(origemX);
            bolinha.setPos_y(origemY);
            gerenciador.adicionarTiroInimigo(bolinha);
        }
    }

    // Ataque 3: invoca 2 inimigos
    private void invocarDoisInimigos(List<Inimigo> enemies) {
        double[] posicoesX = {90.0, larguraTela - 90.0};

        for (double x : posicoesX) {
            int tipo = random.nextInt(2) + 1;
            int vidaInimigo = tipo * 4;
            int danoInimigo = (tipo == 1) ? 1 : 4;
            double velY = (5.0 * tipo) / 2.0;

            Inimigo lacaio = new Inimigo(vidaInimigo, danoInimigo, 0, velY, 1, tipo, 15, 15);
            lacaio.setPos_x(x);
            lacaio.setPos_y(15);
            enemies.add(lacaio);
        }
    }

    @Override
    public void disparar(GerenciadorTiros gerenciador) {
        dispararLequeBolinhas(gerenciador, false);
    }

    // funcoes de get e controle de dano
    public boolean podeCausarDanoContato() {
        if (cooldownDanoContato <= 0) {
            cooldownDanoContato = 45;
            return true;
        }
        return false;
    }

    public int getVidaMaxima() {
        return vidaMaxima;
    }
}