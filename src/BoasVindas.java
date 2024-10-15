import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class BoasVindas extends JDialog {
    private JLabel labelTexto;
    private Timer timer;

    public BoasVindas(JFrame parent) {
        super(parent, "Welcome", true);
        setSize(600, 600);
        setLayout(new GridBagLayout());

        try {
            BufferedImage imagemFundo = ImageIO.read(new File("assets/boas-vindas.jpg"));
            setContentPane(new JLabel(new ImageIcon(imagemFundo)));
        } catch (IOException e) {
            System.out.println("Erro: " + e.getMessage());
        }

        getContentPane().setLayout(new GridBagLayout());

        labelTexto = new JLabel("press ENTER to continue");
        labelTexto.setHorizontalAlignment(SwingConstants.CENTER);

        Fonte fontePersonalizada = new Fonte();
        Font fonteCustomizada = fontePersonalizada.getFonteCustomizada();
        fonteCustomizada = fonteCustomizada.deriveFont(17f);

        labelTexto.setFont(fonteCustomizada);
        labelTexto.setForeground(Color.white);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.PAGE_END;
        gbc.insets = new Insets(0, 0, 20, 0);
        getContentPane().add(labelTexto, gbc);

        timer = new Timer(500, new ActionListener() {
            private boolean visible = true;

            @Override
            public void actionPerformed(ActionEvent e) {
                labelTexto.setVisible(visible);
                visible = !visible;
            }
        });
        timer.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("Tecla pressionada: " + e.getKeyCode());
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    timer.stop();
                    dispose();
                    abrirJanelaInicio();
                }
            }
        });

        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setLocationRelativeTo(null);
        setFocusable(true);
        setVisible(true);
    }

    private void abrirJanelaInicio() {
        JFrame janelaFrame = new JFrame("BATRACE");
        JanelaInicio janelainicio = new JanelaInicio(janelaFrame);
        janelaFrame.setContentPane(janelainicio);
        janelaFrame.setSize(600, 600);
        janelaFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janelaFrame.setLocationRelativeTo(null);
        janelaFrame.setVisible(true);
    }

}
