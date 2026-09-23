package zumbis;

import entidades.Zumbi;
import jogo.Jogo;

public class ZumbiRapido extends Zumbi {
    public ZumbiRapido(Jogo jogo, int linha) {
        super(jogo, linha, 115, 30);
        dano = 14;
    }
}
