import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("BATRACE");
            BoasVindas boasvindas = new BoasVindas(frame);
        });
    }
};