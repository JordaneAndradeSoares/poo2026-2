package entidades;

import java.awt.*;
import java.util.Random;

public class Sol extends Entidade {
    private final boolean coletavel;
    private double tempoVida = 10;

    public Sol(double x, double y) {
        super(x, y, 34, 34);
        this.coletavel = true;
    }

    @Override
    public void atualizar(double delta) {
        tempoVida -= delta;
        if (tempoVida <= 0) matar();
    }

    @Override
    public void desenhar(Graphics2D g) {
        int cx = (int)x + 17;
        int cy = (int)y + 17;

        g.setColor(new Color(255, 205, 30));
        for (int i = 0; i < 8; i++) {
            double a = i * Math.PI / 4;
            int x1 = (int)(cx + Math.cos(a) * 22);
            int y1 = (int)(cy + Math.sin(a) * 22);
            g.drawLine(cx, cy, x1, y1);
        }

        g.fillOval((int)x, (int)y, 34, 34);
        g.setColor(Color.ORANGE);
        g.drawOval((int)x, (int)y, 34, 34);
    }

    public boolean isColetavel() {
        return coletavel;
    }
}
