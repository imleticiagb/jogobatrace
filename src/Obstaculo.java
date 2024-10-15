import java.awt.*;
import java.awt.image.BufferedImage;

public class Obstaculo {
    private BufferedImage imagem;
    private int x, y;

    // Construtor
    public Obstaculo(BufferedImage imagem, int x, int y) {
        this.imagem = imagem;
        this.x = x;
        this.y = y;
    }

    // Metodos
    public BufferedImage getImagem() {
        return imagem;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, imagem.getWidth(), imagem.getHeight());
    }
}