package plantas;

import entidades.Planta;
import jogo.Jogo;
import java.awt.*;

public class Girassol extends Planta {
    private double contador = 4;

    public Girassol(Jogo jogo, int linha, int coluna) {
        super(jogo, linha, coluna, 100, 50);
    }

    @Override
    public String getNome() {
        return "Girassol";
    }

    @Override
    public void atualizar(double delta) {
        contador -= delta;
        if (contador <= 0) {
            jogo.criarSol((int)x + 10, (int)y - 5);
            contador = 7;
        }
    }

    @Override
    public void desenhar(Graphics2D g) {
        int cx = (int)x + 28;
        int cy = (int)y + 28;

        g.setColor(new Color(110, 190, 55));
        g.fillRect(cx - 4, cy + 10, 8, 32);

        g.setColor(new Color(60, 160, 55));
        g.fillOval(cx - 27, cy + 5, 30, 18);
        g.fillOval(cx - 3, cy + 5, 30, 18);

        g.setColor(Color.YELLOW);
        for (int i = 0; i < 8; i++) {
            double a = i * Math.PI / 4;
            int px = (int)(cx + Math.cos(a) * 22) - 10;
            int py = (int)(cy + Math.sin(a) * 22) - 10;
            g.fillOval(px, py, 20, 20);
        }

        g.setColor(new Color(120, 75, 20));
        g.fillOval(cx - 13, cy - 13, 26, 26);

        desenharBarraVida(g);
    }
}
