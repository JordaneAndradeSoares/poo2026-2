package zumbis;

import entidades.Zumbi;
import jogo.Jogo;

public class ZumbiBasico extends Zumbi {
    public ZumbiBasico(Jogo jogo, int linha) {
        super(jogo, linha, 180, 18);
    }
}
