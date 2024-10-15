import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

public class Fonte {
    private Font fonteCustomizada;

    public Fonte() {
        try {
            File arquivoFonte = new File("retro_computer/retro_computer_personal_use.ttf");
            fonteCustomizada = Font.createFont(Font.TRUETYPE_FONT, arquivoFonte).deriveFont(20f);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            fonteCustomizada = new Font("Serif", Font.PLAIN, 20);
        }
    }

    public Font getFonteCustomizada() {
        return fonteCustomizada;
    }
}
