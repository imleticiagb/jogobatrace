import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Sons {

    private Clip clipMusicaTema;
    private Clip clipBatmanGanhou;
    private Clip clipCapuzGanhou;
    private Clip clipColidiu;
    private Clip clipVilao;

    // Construtor
    public Sons() {
        carregarSons();
    }
    // Métodos
    private void carregarSons() {
        clipMusicaTema = carregarSom("audios/musica-tema.wav");
        clipBatmanGanhou = carregarSom("audios/batman-ganhou.wav");
        clipCapuzGanhou = carregarSom("audios/capuz-ganhou.wav");
        clipColidiu = carregarSom("audios/colidiu.wav");
        clipVilao = carregarSom("audios/vilao.wav");
    }

    private Clip carregarSom(String caminho) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(caminho));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void tocarMusicaTema() {
        if (clipMusicaTema != null) {
            clipMusicaTema.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void pararMusicaTema() {
        if (clipMusicaTema != null) {
            clipMusicaTema.stop();
            clipMusicaTema.setFramePosition(0);
        }
    }

    public void tocarBatmanGanhou() {
        if (clipBatmanGanhou != null) {
            clipBatmanGanhou.setFramePosition(0);
            clipBatmanGanhou.start();
        }
    }

    public void tocarCapuzGanhou() {
        if (clipCapuzGanhou != null) {
            clipCapuzGanhou.setFramePosition(0);
            clipCapuzGanhou.start();
        }
    }

    public void tocarColidiu() {
        if (clipColidiu != null) {
            clipColidiu.setFramePosition(0); // Reinicia o áudio
            clipColidiu.start();
        }
    }

    public void tocarVilao() {
        if (clipVilao != null) {
            clipVilao.setFramePosition(0); // Reinicia o áudio
            clipVilao.start();
        }
    }
}
