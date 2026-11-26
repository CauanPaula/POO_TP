package motor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.*;

public class GerenciadorSom {

    private static boolean somAtivado = true;
    private static int volumePercentual = 80;
    private static Clip clipMusica;
    private static final Map<String, byte[]> cacheSons = new HashMap<>();

    public static void setSomAtivado(boolean ativado) {
        somAtivado = ativado;
        if (!somAtivado) {
            pararMusica();
        } else if (clipMusica != null && !clipMusica.isRunning()) {
            aplicarVolume(clipMusica);
            clipMusica.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public static boolean isSomAtivado() {
        return somAtivado;
    }

    public static void setVolume(int percentual) {
        volumePercentual = Math.max(0, Math.min(100, percentual));
        if (clipMusica != null) {
            aplicarVolume(clipMusica);
        }
    }

    public static int getVolume() {
        return volumePercentual;
    }

    private static Clip criarClipCompativel(AudioFormat formato) throws LineUnavailableException {
        DataLine.Info info = new DataLine.Info(Clip.class, formato);

        // No Linux, busca explicitamente o mixer "default" (PulseAudio/PipeWire) primeiro
        for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
            String nome = mixerInfo.getName().toLowerCase();
            if (nome.contains("default") || nome.contains("pulse") || nome.contains("pipewire")) {
                Mixer mixer = AudioSystem.getMixer(mixerInfo);
                if (mixer.isLineSupported(info)) {
                    return (Clip) mixer.getLine(info);
                }
            }
        }
        return (Clip) AudioSystem.getLine(info);
    }

    private static byte[] carregarBytes(String caminhoArquivo) throws Exception {
        if (cacheSons.containsKey(caminhoArquivo)) {
            return cacheSons.get(caminhoArquivo);
        }
        File arquivo = new File(caminhoArquivo);
        if (!arquivo.exists()) {
            System.out.println("ARQUIVO DE SOM NÃO ENCONTRADO: " + arquivo.getAbsolutePath());
            return null;
        }
        byte[] dados = Files.readAllBytes(arquivo.toPath());
        cacheSons.put(caminhoArquivo, dados);
        return dados;
    }

    public static void tocarEfeito(String caminhoArquivo) {
        if (!somAtivado || volumePercentual == 0) return;

        try {
            byte[] dados = carregarBytes(caminhoArquivo);
            if (dados == null) return;

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new ByteArrayInputStream(dados));
            Clip clip = criarClipCompativel(audioIn.getFormat());
            clip.open(audioIn);
            aplicarVolume(clip);

            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });

            clip.start();
        } catch (Exception e) {
            System.out.println("ERRO DE ÁUDIO (" + caminhoArquivo + "): " + e.getMessage());
            e.printStackTrace();
        }
    }

   public static void tocarMusica(String caminhoArquivo) {
        pararMusica();
        if (!somAtivado || volumePercentual == 0) return;

        try {
            byte[] dados = carregarBytes(caminhoArquivo);
            if (dados == null) return;

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new ByteArrayInputStream(dados));
            clipMusica = criarClipCompativel(audioIn.getFormat());
            clipMusica.open(audioIn);
            aplicarVolume(clipMusica);
            clipMusica.loop(Clip.LOOP_CONTINUOUSLY); // Inicia o loop contínuo sozinho
        } catch (Exception e) {
            System.out.println("ERRO DE MÚSICA (" + caminhoArquivo + "): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void pararMusica() {
        if (clipMusica != null) {
            clipMusica.stop();
            clipMusica.close();
            clipMusica = null;
        }
    }

    private static void aplicarVolume(Clip clip) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log10(Math.max(0.0001, volumePercentual / 100.0)) * 20.0);
            dB = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB));
            gainControl.setValue(dB);
        }
    }
}