import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JogoCorrida extends JPanel {
    private BufferedImage pistaImage;
    private BufferedImage batmovelImage;
    private BufferedImage motoCapuzImage;
    private int posXCarro, posYCarro;
    private int posXMoto, posYMoto;
    private double anguloCarro = 0;
    private double anguloMoto = 0;
    private int velocidadeCarro = 1;
    private int velocidadeMoto = 1;
    private boolean acelerarCarro = false;
    private boolean acelerarMoto = false;
    private Rectangle areaVerde;
    private Rectangle linhaChegada;

    private int voltasCarro = 0;
    private int voltasMoto = 0;
    private final int MAX_VOLTAS = 3;
    private boolean jogoEncerrado = false;

    private boolean carroPassouLinhaChegada = false;
    private boolean motoPassouLinhaChegada = false;

    private List<Obstaculo> obstaculos;
    private BufferedImage[] imagensObstaculos;
    private Timer timerObstaculos;
    private Random random;

    private boolean batmanGanhou;
    private boolean capuzGanhou;

    private static final double ANGULO_CARRO_INICIAL = 0;
    private static final double ANGULO_MOTO_INICIAL = 0;


    private Sons sons;

    // Lista para rastrear o tempo de criação de cada obstáculo
    private List<Long> obstaculoTempos;

    //Construtor
    public JogoCorrida() {
        inicializarImagens();
        posXMoto = 5;
        posYMoto = 5;
        posXCarro = 2;
        posYCarro = 40;

        areaVerde = new Rectangle(0, 0, 340, 340);
        linhaChegada = new Rectangle(0, 120, 110, 30);

        obstaculos = new ArrayList<>();
        imagensObstaculos = new BufferedImage[3];
        random = new Random();
        obstaculoTempos = new ArrayList<>();

        carregarImagensObstaculos();
        iniciarTimerObstaculos();

        sons = new Sons();
        sons.tocarMusicaTema();



        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            //Controla a tecla pressionada
            public void keyPressed(KeyEvent e) {
                if (!jogoEncerrado) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_W: anguloCarro = Math.toRadians(270); break;
                        case KeyEvent.VK_S: anguloCarro = Math.toRadians(90); break;
                        case KeyEvent.VK_A: anguloCarro = Math.toRadians(180); break;
                        case KeyEvent.VK_D: anguloCarro = 0; break;
                        case KeyEvent.VK_UP: anguloMoto = Math.toRadians(270); break;
                        case KeyEvent.VK_DOWN: anguloMoto = Math.toRadians(90); break;
                        case KeyEvent.VK_LEFT: anguloMoto = Math.toRadians(180); break;
                        case KeyEvent.VK_RIGHT: anguloMoto = 0; break;
                        case KeyEvent.VK_F: acelerarCarro = true; break;
                        case KeyEvent.VK_M: acelerarMoto = true; break;
                    }
                }
            }

            @Override
            //Controla a tecla liberada
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_F: acelerarCarro = false; break;
                    case KeyEvent.VK_M: acelerarMoto = false; break;
                }
            }
        });

        Timer timer = new Timer(16, e -> {
            if (!jogoEncerrado) {
                atualizarPosicao();
                removerObstaculosExpirados();
                repaint();
            }
        });
        timer.start();
    }

    //Métodos
    private void inicializarImagens() {
        try {
            pistaImage = ImageIO.read(new File("assets/fundo.jpg"));
            batmovelImage = redimensionarImagem(ImageIO.read(new File("assets/batmovel.png")), 120, 60);
            motoCapuzImage = redimensionarImagem(ImageIO.read(new File("assets/moto-capuz.png")), 85, 45);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void carregarImagensObstaculos() {
        try {
            imagensObstaculos[0] = redimensionarImagem(ImageIO.read(new File("assets/arlequina.png")), 45, 45);
            imagensObstaculos[1] = redimensionarImagem(ImageIO.read(new File("assets/coringa.png")), 45, 45);
            imagensObstaculos[2] = redimensionarImagem(ImageIO.read(new File("assets/ivy.png")), 45, 45);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void iniciarTimerObstaculos() {
        timerObstaculos = new Timer(2000, e -> {
            if (obstaculos.size() < 3) {
                adicionarObstaculo();
            }
        });
        timerObstaculos.start();
    }
    //Adiciona obstáculos na pista, delimitando a área
    // verde, que é proibida
    private void adicionarObstaculo() {
        BufferedImage imagemObstaculo = imagensObstaculos[random.nextInt(imagensObstaculos.length)];
        int x, y;
        boolean posicaoValida;
        int largura = imagemObstaculo.getWidth();
        int altura = imagemObstaculo.getHeight();
        do {
            x = random.nextInt(Math.max(1, getWidth() - largura));
            y = random.nextInt(Math.max(1, getHeight() - altura));
            posicaoValida = !colideComAreaVerde(x, y, largura, altura) &&
                    !colideComOutrosObstaculos(x, y, largura, altura);
        } while (!posicaoValida);

        obstaculos.add(new Obstaculo(imagemObstaculo, x, y));
        obstaculoTempos.add(System.currentTimeMillis());
    }

        //Verifica se o veículo colidiu com algum obstáculo
    private boolean colideComOutrosObstaculos(int x, int y, int largura, int altura) {
        Rectangle novoObstaculo = new Rectangle(x, y, largura, altura);
        for (Obstaculo obstaculo : obstaculos) {
            if (novoObstaculo.intersects(obstaculo.getBounds())) {
                return true;
            }
        }
        return false;
    }

    private BufferedImage redimensionarImagem(BufferedImage originalImage, int largura, int altura) {
        Image imagemRedimensionada = originalImage.getScaledInstance(largura, altura, Image.SCALE_SMOOTH);
        BufferedImage imagemFinal = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = imagemFinal.createGraphics();
        g2d.drawImage(imagemRedimensionada, 0, 0, null);
        g2d.dispose();
        return imagemFinal;
    }

    //Atualiza a posição dos jogadores
    private void atualizarPosicao() {
        double velocidadeAtualCarro = acelerarCarro ? 8.5 : 2;
        double velocidadeAtualMoto = acelerarMoto ? 8.5 : 2;

        int novoXCarro = posXCarro + (int)(Math.cos(anguloCarro) * velocidadeAtualCarro);
        int novoYCarro = posYCarro + (int)(Math.sin(anguloCarro) * velocidadeAtualCarro);
        int novoXMoto = posXMoto + (int)(Math.cos(anguloMoto) * velocidadeAtualMoto);
        int novoYMoto = posYMoto + (int)(Math.sin(anguloMoto) * velocidadeAtualMoto);

        if (!colideComAreaVerde(novoXCarro, novoYCarro, batmovelImage.getWidth(), batmovelImage.getHeight()) &&
                dentroDosPistaLimites(novoXCarro, novoYCarro, batmovelImage.getWidth(), batmovelImage.getHeight())) {
            posXCarro = novoXCarro;
            posYCarro = novoYCarro;
            verificarVoltasCarro();
        }

        if (!colideComAreaVerde(novoXMoto, novoYMoto, motoCapuzImage.getWidth(), motoCapuzImage.getHeight()) &&
                dentroDosPistaLimites(novoXMoto, novoYMoto, motoCapuzImage.getWidth(), motoCapuzImage.getHeight())) {
            posXMoto = novoXMoto;
            posYMoto = novoYMoto;
            verificarVoltasMoto();
        }

        verificarColisoes();
    }

    private boolean colideComAreaVerde(int x, int y, int largura, int altura) {
        Rectangle veiculo = new Rectangle(x, y, largura, altura);
        return veiculo.intersects(areaVerde);
    }

    private boolean dentroDosPistaLimites(int x, int y, int largura, int altura) {
        return x >= 0 && y >= 0 && x + largura <= getWidth() && y + altura <= getHeight();
    }

    //Verifica a quantidade de voltas do Batmóvel
    private void verificarVoltasCarro() {
        Rectangle carroBounds = new Rectangle(posXCarro, posYCarro, batmovelImage.getWidth(), batmovelImage.getHeight());

        if (linhaChegada.intersects(carroBounds)) {
            if (!carroPassouLinhaChegada) {
                carroPassouLinhaChegada = true;
                voltasCarro++;

                if (voltasCarro >= MAX_VOLTAS) {
                    batmanGanhou = true;
                    sons.pararMusicaTema();
                    sons.tocarBatmanGanhou();
                    encerrarJogo("Batman wins!");
                }
            }
        } else {
            carroPassouLinhaChegada = false;
        }
    }

    //Verifica a quantidade de voltas da Moto
    private void verificarVoltasMoto() {
        Rectangle motoBounds = new Rectangle(posXMoto, posYMoto, motoCapuzImage.getWidth(), motoCapuzImage.getHeight());

        if (linhaChegada.intersects(motoBounds)) {
            if (!motoPassouLinhaChegada) {
                motoPassouLinhaChegada = true;
                voltasMoto++;

                if (voltasMoto >= MAX_VOLTAS) {
                    capuzGanhou = true;
                    sons.pararMusicaTema();
                    sons.tocarCapuzGanhou();
                    encerrarJogo("Red Hood wins!");
                }
            }
        } else {
            motoPassouLinhaChegada = false;
        }
    }

    //Encerra e dá ao jogador a opção de reiniciar o jogo
    private void encerrarJogo(String mensagem) {
        jogoEncerrado = true;
        int resposta = JOptionPane.showConfirmDialog(this, mensagem + "\nRestart?", "Game Over", JOptionPane.YES_NO_OPTION);
        if (resposta == JOptionPane.YES_OPTION) {
            reiniciarJogo();
        } else {
            System.exit(0);
        }

    }

        //Remove os obstáculos depois de 3s
    private void removerObstaculosExpirados() {
        long agora = System.currentTimeMillis();
        for (int i = obstaculos.size() - 1; i >= 0; i--) {
            long tempoCriacao = obstaculoTempos.get(i);
            if (agora - tempoCriacao >= 3000) {
                obstaculos.remove(i);
                obstaculoTempos.remove(i);
            }
        }
    }

    //Verifica se houve colisão
    private void verificarColisoes() {
        for (int i = 0; i < obstaculos.size(); i++) {
            Obstaculo obstaculo = obstaculos.get(i);
            Rectangle obstaculoRect = obstaculo.getBounds();

            Rectangle carroRect = new Rectangle(posXCarro, posYCarro, batmovelImage.getWidth(), batmovelImage.getHeight());
            Rectangle motoRect = new Rectangle(posXMoto, posYMoto, motoCapuzImage.getWidth(), motoCapuzImage.getHeight());

            if (carroRect.intersects(obstaculoRect)) {
                sons.tocarColidiu();
                sons.tocarVilao();
                voltasCarro = 0;
                posXCarro = 2;
                posYCarro = 40;
                obstaculos.remove(i);
                obstaculoTempos.remove(i);
                i--;
                continue;
            }

            if (motoRect.intersects(obstaculoRect)) {
                sons.tocarColidiu();
                sons.tocarVilao();
                voltasMoto = 0;
                posXMoto = 5;
                posYMoto = 5;
                obstaculos.remove(i);
                obstaculoTempos.remove(i);
                i--;
            }
        }
    }

    @Override
    //Desenha a pista
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (pistaImage != null) {
            g.drawImage(pistaImage, 0, 0, getWidth(), getHeight(), null);
        }

        int areaVerdeX = (getWidth() - areaVerde.width) / 2;
        int areaVerdeY = (getHeight() - areaVerde.height) / 2;
        areaVerde.setLocation(areaVerdeX, areaVerdeY);
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fill(areaVerde);


        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fill(linhaChegada);

        AffineTransform transformCarro = AffineTransform.getTranslateInstance(posXCarro, posYCarro);
        transformCarro.rotate(anguloCarro, batmovelImage.getWidth() / 2.0, batmovelImage.getHeight() / 2.0);
        g2d.drawImage(batmovelImage, transformCarro, null);

        AffineTransform transformMoto = AffineTransform.getTranslateInstance(posXMoto, posYMoto);
        transformMoto.rotate(anguloMoto, motoCapuzImage.getWidth() / 2.0, motoCapuzImage.getHeight() / 2.0);
        g2d.drawImage(motoCapuzImage, transformMoto, null);

        for (Obstaculo obstaculo : obstaculos) {
            g2d.drawImage(obstaculo.getImagem(), obstaculo.getX(), obstaculo.getY(), null);
        }

        Fonte fontePersonalizada = new Fonte();
        Font fonteCustomizada = fontePersonalizada.getFonteCustomizada();
        fonteCustomizada = fonteCustomizada.deriveFont(15f);
        g2d.setColor(Color.YELLOW);

        String textoBatman = "BATMAN: " + voltasCarro;
        String textoCapuz = "RED HOOD: " + voltasMoto;

        AttributedString asBatman = new AttributedString(textoBatman);
        asBatman.addAttribute(TextAttribute.FONT, fonteCustomizada);
        asBatman.addAttribute(TextAttribute.TRACKING, 0.3);

        AttributedString asCapuz = new AttributedString(textoCapuz);
        asCapuz.addAttribute(TextAttribute.FONT, fonteCustomizada);
        asCapuz.addAttribute(TextAttribute.TRACKING, 0.3);

        FontMetrics fm = g2d.getFontMetrics(fonteCustomizada);
        int larguraBatman = fm.stringWidth(textoBatman);
        int larguraCapuz = fm.stringWidth(textoCapuz);
        int alturaTexto = fm.getHeight();

        int textoXBatman = (getWidth() - larguraBatman) / 2;
        int textoXCapuz = (getWidth() - larguraCapuz) / 2;

        int textoYBatman = areaVerdeY + areaVerde.height - alturaTexto - 20;
        int textoYCapuz = textoYBatman + alturaTexto + 10;

        g2d.drawString(asBatman.getIterator(), textoXBatman, textoYBatman);
        g2d.drawString(asCapuz.getIterator(), textoXCapuz, textoYCapuz);
    }

    //Reinicia o jogo
    private void reiniciarJogo() {
        // Reiniciar a posição dos veículos
        posXCarro = 2;
        posYCarro = 40;
        posXMoto = 5;
        posYMoto = 5;

        // Reiniciar o número de voltas
        voltasCarro = 0;
        voltasMoto = 0;

        // Reiniciar estados
        carroPassouLinhaChegada = false;
        motoPassouLinhaChegada = false;
        jogoEncerrado = false;

        // Limpar obstáculos
        obstaculos.clear();
        obstaculoTempos.clear();

        // Reiniciar a música
        sons.pararMusicaTema();
        sons.tocarMusicaTema();

        anguloCarro = ANGULO_CARRO_INICIAL;
        anguloMoto = ANGULO_MOTO_INICIAL;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("BATRACE");
        JogoCorrida jogoCorrida = new JogoCorrida();
        frame.add(jogoCorrida);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
