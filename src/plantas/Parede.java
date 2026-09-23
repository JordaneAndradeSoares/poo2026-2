package plantas;

import entidades.Planta;
import jogo.Jogo;
import java.awt.*;

public class Parede extends Planta {
    public Parede(Jogo jogo, int linha, int coluna) {
        super(jogo, linha, coluna, 450, 75);
    }

    @Override
    public String getNome() {
        return "Parede";
    }

    @Override
    public void atualizar(double delta) {
    }

    @Override
    public void desenhar(Graphics2D g) {
        g.setColor(new Color(205, 160, 65));
        g.fillOval((int)x, (int)y, 56, 56);

        g.setColor(new Color(120, 75, 20));
        g.drawOval((int)x, (int)y, 56, 56);

        g.setColor(Color.BLACK);
        g.fillOval((int)x + 16, (int)y + 18, 7, 9);
        g.fillOval((int)x + 34, (int)y + 18, 7, 9);

        g.drawArc((int)x + 18, (int)y + 25, 22, 15, 180, 180);

        desenharBarraVida(g);
    }
}
