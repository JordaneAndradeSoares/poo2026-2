package entidades;

import java.awt.*;
import jogo.Jogo;

public abstract class Planta extends Entidade {
    protected final Jogo jogo;
    protected final int linha;
    protected final int coluna;
    protected int vida;
    protected int vidaMaxima;
    protected int custo;

    public Planta(Jogo jogo, int linha, int coluna, int vida, int custo) {
        super(
            jogo.getXDaColuna(coluna) + 22,
            jogo.getYDaLinha(linha) + 25,
            56,
            56
        );
        this.jogo = jogo;
        this.linha = linha;
        this.coluna = coluna;
        this.vida = vida;
        this.vidaMaxima = vida;
        this.custo = custo;
    }

    public abstract String getNome();

    public int getCusto() {
        return custo;
    }

    public int getLinha() {
        return linha;
    }

    public void receberDano(int dano) {
        vida -= dano;
        if (vida <= 0) matar();
    }

    protected void desenharBarraVida(Graphics2D g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect((int)x, (int)y - 9, largura, 5);
        g.setColor(Color.GREEN);
        int w = (int)(largura * Math.max(0, vida) / (double)vidaMaxima);
        g.fillRect((int)x, (int)y - 9, w, 5);
    }
}
