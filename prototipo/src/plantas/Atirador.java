package plantas;

import entidades.Planta;
import jogo.Jogo;
import java.awt.*;

public class Atirador extends Planta {
    private double contador = 0;

    public Atirador(Jogo jogo, int linha, int coluna) {
        super(jogo, linha, coluna, 120, 100);
    }

    @Override
    public String getNome() {
        return "Atirador";
    }

    @Override
    public void atualizar(double delta) {
        contador -= delta;
        if (contador <= 0 && jogo.existeZumbiNaLinha(linha)) {
            jogo.criarProjetil(x + largura - 3, y + 20, linha, 30, Color.GREEN);
            contador = 1.25;
        }
    }

    @Override
    public void desenhar(Graphics2D g) {
        int cx = (int)x + 28;
        int cy = (int)y + 30;

        g.setColor(new Color(80, 180, 60));
        g.fillOval(cx - 25, cy - 23, 50, 50);

        g.setColor(new Color(45, 130, 45));
        g.fillOval(cx + 5, cy - 11, 35, 27);

        g.setColor(new Color(30, 100, 35));
        g.fillOval(cx + 27, cy - 5, 15, 15);

        g.setColor(Color.WHITE);
        g.fillOval(cx - 5, cy - 13, 12, 12);
        g.setColor(Color.BLACK);
        g.fillOval(cx - 1, cy - 9, 5, 5);

        desenharBarraVida(g);
    }
}
