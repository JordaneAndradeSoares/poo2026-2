package zumbis;

import entidades.Zumbi;
import jogo.Jogo;

public class ZumbiResistente extends Zumbi {
    public ZumbiResistente(Jogo jogo, int linha) {
        super(jogo, linha, 360, 11);
        dano = 28;
        intervaloAtaque = 0.8;
    }
}
