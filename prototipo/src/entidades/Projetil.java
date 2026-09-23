package entidades;

import java.awt.*;
import jogo.Jogo;
import util.Constantes;

public class Projetil extends Entidade {
    private final int linha;
    private final int dano;
    private final Color cor;
    private final Jogo jogo;
    private final double velocidade = 360;

    public Projetil(Jogo jogo, double x, double y, int linha, int dano, Color cor) {
        super(x, y, 18, 18);
        this.jogo = jogo;
        this.linha = linha;
        this.dano = dano;
        this.cor = cor;
    }

    @Override
    public void atualizar(double delta) {
        x += velocidade * delta;

        if (x > Constantes.LARGURA) {
            matar();
            return;
        }

        for (ZumbiRef z : jogo.getZumbisDaLinha(linha)) {
            if (getBounds().intersects(z.bounds)) {
                z.receberDano(dano);
                matar();
                break;
            }
        }
    }

    @Override
    public void desenhar(Graphics2D g) {
        g.setColor(cor);
        g.fillOval((int)x, (int)y, largura, altura);
        g.setColor(Color.WHITE);
        g.drawOval((int)x, (int)y, largura, altura);
    }

    public int getLinha() {
        return linha;
    }

    public static class ZumbiRef {
        public Rectangle bounds;
        private final java.util.function.IntConsumer dano;

        public ZumbiRef(Rectangle bounds, java.util.function.IntConsumer dano) {
            this.bounds = bounds;
            this.dano = dano;
        }

        public void receberDano(int valor) {
            dano.accept(valor);
        }
    }
}
