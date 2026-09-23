package plantas;

import entidades.Planta;
import jogo.Jogo;
import java.awt.*;

public class Explosiva extends Planta {
    private boolean explodiu = false;

    public Explosiva(Jogo jogo, int linha, int coluna) {
        super(jogo, linha, coluna, 80, 125);
    }

    @Override
    public String getNome() {
        return "Explosiva";
    }

    @Override
    public void atualizar(double delta) {
        if (!explodiu && jogo.existeZumbiPerto(this, 70)) {
            explodiu = true;
            jogo.explodir((int)x + 28, (int)y + 28, linha, 110, 150);
            matar();
        }
    }

    @Override
    public void desenhar(Graphics2D g) {
        int cx = (int)x + 28;
        int cy = (int)y + 28;

        g.setColor(new Color(220, 70, 40));
        g.fillOval(cx - 24, cy - 24, 48, 48);

        g.setColor(Color.YELLOW);
        g.fillOval(cx - 12, cy - 12, 24, 24);

        g.setColor(Color.DARK_GRAY);
        g.drawLine(cx + 8, cy - 17, cx + 18, cy - 30);

        desenharBarraVida(g);
    }
}
