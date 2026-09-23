package entidades;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public abstract class Entidade {
    protected double x;
    protected double y;
    protected int largura;
    protected int altura;
    protected boolean viva = true;

    public Entidade(double x, double y, int largura, int altura) {
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
    }

    public abstract void atualizar(double delta);

    public abstract void desenhar(Graphics2D g);

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, largura, altura);
    }

    public boolean estaViva() {
        return viva;
    }

    public double getX() { return x; }
    public double getY() { return y; }

    public void matar() {
        viva = false;
    }
}
