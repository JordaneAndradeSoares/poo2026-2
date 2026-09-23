package entidades;

import java.awt.*;
import jogo.Jogo;

public abstract class Zumbi extends Entidade {
    protected final Jogo jogo;
    protected final int linha;
    protected int vida;
    protected int vidaMaxima;
    protected double velocidade;
    protected int dano = 18;
    protected double intervaloAtaque = 0.7;
    protected double contadorAtaque = 0;

    public Zumbi(Jogo jogo, int linha, int vida, double velocidade) {
        super(jogo.getXDaColuna(9) + 35, jogo.getYDaLinha(linha) + 20, 58, 70);
        this.jogo = jogo;
        this.linha = linha;
        this.vida = vida;
        this.vidaMaxima = vida;
        this.velocidade = velocidade;
    }

    public int getLinha() {
        return linha;
    }

    public void receberDano(int dano) {
        vida -= dano;
        if (vida <= 0) matar();
    }

    @Override
    public void atualizar(double delta) {
        contadorAtaque -= delta;

        Planta alvo = jogo.getPlantaNaFrente(this, linha);
        if (alvo != null) {
            if (contadorAtaque <= 0) {
                alvo.receberDano(dano);
                contadorAtaque = intervaloAtaque;
            }
        } else {
            x -= velocidade * delta;
        }

        if (x < 35) {
            jogo.derrotar();
        }
    }

    @Override
    public void desenhar(Graphics2D g) {
        // Corpo
        g.setColor(new Color(100, 135, 105));
        g.fillRoundRect((int)x + 8, (int)y + 22, 42, 45, 15, 15);

        // Cabeça
        g.setColor(new Color(130, 165, 125));
        g.fillOval((int)x + 5, (int)y, 48, 45);

        // Olhos
        g.setColor(Color.WHITE);
        g.fillOval((int)x + 15, (int)y + 13, 12, 13);
        g.fillOval((int)x + 32, (int)y + 13, 12, 13);
        g.setColor(Color.BLACK);
        g.fillOval((int)x + 19, (int)y + 17, 5, 6);
        g.fillOval((int)x + 36, (int)y + 17, 5, 6);

        // Braços
        g.setColor(new Color(100, 135, 105));
        g.drawLine((int)x + 12, (int)y + 32, (int)x - 8, (int)y + 48);
        g.drawLine((int)x + 48, (int)y + 32, (int)x + 67, (int)y + 45);

        // Barra de vida
        g.setColor(Color.DARK_GRAY);
        g.fillRect((int)x, (int)y - 9, largura, 5);
        g.setColor(Color.RED);
        g.fillRect((int)x, (int)y - 9,
            (int)(largura * Math.max(0, vida) / (double)vidaMaxima), 5);
    }
}
