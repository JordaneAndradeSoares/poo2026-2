package jogo;

import entidades.*;
import entidades.Projetil.ZumbiRef;
import plantas.*;
import zumbis.*;
import render.Renderizador;
import util.Constantes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class Jogo extends JPanel implements ActionListener, MouseListener, KeyListener {
    private final List<Planta> plantas = new ArrayList<>();
    private final List<Zumbi> zumbis = new ArrayList<>();
    private final List<Projetil> projeteis = new ArrayList<>();
    private final List<Sol> sois = new ArrayList<>();

    private final List<Carta> cartas = new ArrayList<>();
    private int cartaSelecionada = -1;
    private int soisJogador = 150;

    private int onda = 1;
    private double contadorSpawn = 3;
    private double tempo = 0;

    private boolean pausado = false;
    private boolean derrota = false;
    private boolean vitoria = false;

    private long ultimoTempo;
    private final javax.swing.Timer timer;
    private final Renderizador renderizador;
    private JFrame frame;

    public Jogo() {
        setPreferredSize(new Dimension(Constantes.LARGURA, Constantes.ALTURA));
        setFocusable(true);
        addMouseListener(this);
        addKeyListener(this);

        cartas.add(new Carta("Girassol", 50));
        cartas.add(new Carta("Atirador", 100));
        cartas.add(new Carta("Parede", 75));
        cartas.add(new Carta("Explosiva", 125));

        renderizador = new Renderizador(this);
        timer = new javax.swing.Timer(16, this);
    }

    public void iniciar() {
        frame = new JFrame("Plants vs. Zumbis - Java");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setContentPane(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        requestFocusInWindow();

        ultimoTempo = System.nanoTime();
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        long agora = System.nanoTime();
        double delta = Math.min((agora - ultimoTempo) / 1_000_000_000.0, 0.05);
        ultimoTempo = agora;

        if (!pausado && !derrota && !vitoria) {
            atualizar(delta);
        }

        repaint();
    }

    private void atualizar(double delta) {
        tempo += delta;
        contadorSpawn -= delta;

        if (contadorSpawn <= 0) {
            spawnarZumbi();
            contadorSpawn = Math.max(1.5, 5.0 - onda * 0.25);
        }

        if (tempo > 45 && onda < 2) onda = 2;
        if (tempo > 90 && onda < 3) onda = 3;
        if (tempo > 140 && onda < 4) onda = 4;

        for (Planta p : new ArrayList<>(plantas)) {
            if (p.estaViva()) p.atualizar(delta);
        }

        for (Projetil p : new ArrayList<>(projeteis)) {
            if (p.estaViva()) p.atualizar(delta);
        }

        for (Zumbi z : new ArrayList<>(zumbis)) {
            if (z.estaViva()) z.atualizar(delta);
        }

        for (Sol s : new ArrayList<>(sois)) {
            if (s.estaViva()) s.atualizar(delta);
        }

        plantas.removeIf(p -> !p.estaViva());
        zumbis.removeIf(z -> !z.estaViva());
        projeteis.removeIf(p -> !p.estaViva());
        sois.removeIf(s -> !s.estaViva());

        if (tempo > 180 && zumbis.isEmpty()) {
            vitoria = true;
        }
    }

    private void spawnarZumbi() {
        Random r = new Random();
        int linha = r.nextInt(Constantes.LINHAS);

        double chance = r.nextDouble();
        if (onda >= 3 && chance < 0.18) {
            zumbis.add(new ZumbiResistente(this, linha));
        } else if (onda >= 2 && chance < 0.35) {
            zumbis.add(new ZumbiRapido(this, linha));
        } else {
            zumbis.add(new ZumbiBasico(this, linha));
        }
    }

    public void plantar(int linha, int coluna) {
        if (getPlanta(linha, coluna) != null || cartaSelecionada < 0) return;

        Carta carta = cartas.get(cartaSelecionada);
        if (soisJogador < carta.custo) return;

        Planta planta = switch (carta.nome) {
            case "Girassol" -> new Girassol(this, linha, coluna);
            case "Atirador" -> new Atirador(this, linha, coluna);
            case "Parede" -> new Parede(this, linha, coluna);
            case "Explosiva" -> new Explosiva(this, linha, coluna);
            default -> null;
        };

        if (planta != null) {
            soisJogador -= carta.custo;
            plantas.add(planta);
            cartaSelecionada = -1;
        }
    }

    public void coletarSol(int mouseX, int mouseY) {
        for (Sol sol : new ArrayList<>(sois)) {
            if (sol.getBounds().contains(mouseX, mouseY)) {
                soisJogador += 25;
                sol.matar();
            }
        }
    }

    public void criarSol(int x, int y) {
        sois.add(new Sol(x, y));
    }

    public void criarProjetil(double x, double y, int linha, int dano, Color cor) {
        projeteis.add(new Projetil(this, x, y, linha, dano, cor));
    }

    public void explodir(int x, int y, int linha, int raio, int dano) {
        for (Zumbi z : new ArrayList<>(zumbis)) {
            if (z.getLinha() == linha && Math.abs(z.getX() - x) < raio) {
                z.receberDano(dano);
            }
        }
    }

    public boolean existeZumbiNaLinha(int linha) {
        for (Zumbi z : zumbis) {
            if (z.estaViva() && z.getLinha() == linha) return true;
        }
        return false;
    }

    public boolean existeZumbiPerto(Planta planta, double distancia) {
        for (Zumbi z : zumbis) {
            if (z.estaViva() && z.getLinha() == planta.getLinha()
                    && z.getX() - planta.getX() < distancia
                    && z.getX() > planta.getX() - 30) {
                return true;
            }
        }
        return false;
    }

    public Planta getPlantaNaFrente(Zumbi zumbi, int linha) {
        Planta melhor = null;
        double distancia = Double.MAX_VALUE;

        for (Planta p : plantas) {
            if (!p.estaViva() || p.getLinha() != linha) continue;

            double dx = zumbi.getX() - p.getX();
            if (dx >= -10 && dx < distancia && dx < 65) {
                distancia = dx;
                melhor = p;
            }
        }

        return melhor;
    }

    public List<ZumbiRef> getZumbisDaLinha(int linha) {
        List<ZumbiRef> resultado = new ArrayList<>();

        for (Zumbi z : zumbis) {
            if (z.estaViva() && z.getLinha() == linha) {
                resultado.add(new ZumbiRef(z.getBounds(), z::receberDano));
            }
        }

        return resultado;
    }

    private Planta getPlanta(int linha, int coluna) {
        for (Planta p : plantas) {
            if (p.estaViva() && p.getLinha() == linha) {
                int pc = (int)((p.getX() - Constantes.CAMPO_X) / Constantes.CELULA_LARGURA);
                if (pc == coluna) return p;
            }
        }
        return null;
    }

    public int getXDaColuna(int coluna) {
        return Constantes.CAMPO_X + coluna * Constantes.CELULA_LARGURA;
    }

    public int getYDaLinha(int linha) {
        return Constantes.CAMPO_Y + linha * Constantes.CELULA_ALTURA;
    }

    public void derrotar() {
        derrota = true;
    }

    public void reiniciar() {
        plantas.clear();
        zumbis.clear();
        projeteis.clear();
        sois.clear();

        soisJogador = 150;
        onda = 1;
        tempo = 0;
        contadorSpawn = 3;
        cartaSelecionada = -1;
        derrota = false;
        vitoria = false;
        pausado = false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderizador.desenhar((Graphics2D) g);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        requestFocusInWindow();

        if (derrota || vitoria) return;

        int mx = e.getX();
        int my = e.getY();

        if (my < Constantes.TOPO) {
            int cardX = 180;
            for (int i = 0; i < cartas.size(); i++) {
                if (mx >= cardX && mx <= cardX + 125 && my >= 12 && my <= 82) {
                    cartaSelecionada = i;
                    return;
                }
                cardX += 140;
            }
        }

        coletarSol(mx, my);

        if (my >= Constantes.CAMPO_Y
                && my < Constantes.CAMPO_Y + Constantes.CAMPO_ALTURA
                && mx >= Constantes.CAMPO_X
                && mx < Constantes.CAMPO_X + Constantes.CAMPO_LARGURA) {

            int coluna = (mx - Constantes.CAMPO_X) / Constantes.CELULA_LARGURA;
            int linha = (my - Constantes.CAMPO_Y) / Constantes.CELULA_ALTURA;
            plantar(linha, coluna);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_P) {
            pausado = !pausado;
        } else if (e.getKeyCode() == KeyEvent.VK_R) {
            reiniciar();
        }
    }

    public List<Planta> getPlantas() { return plantas; }
    public List<Zumbi> getZumbis() { return zumbis; }
    public List<Projetil> getProjeteis() { return projeteis; }
    public List<Sol> getSois() { return sois; }
    public List<Carta> getCartas() { return cartas; }

    public int getSoisJogador() { return soisJogador; }
    public int getOnda() { return onda; }
    public int getCartaSelecionada() { return cartaSelecionada; }
    public boolean isPausado() { return pausado; }
    public boolean isDerrota() { return derrota; }
    public boolean isVitoria() { return vitoria; }

    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    public static class Carta {
        public final String nome;
        public final int custo;

        public Carta(String nome, int custo) {
            this.nome = nome;
            this.custo = custo;
        }
    }
}
