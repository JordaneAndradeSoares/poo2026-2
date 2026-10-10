package com.poo2026_2.model.zumbis;

import com.poo2026_2.model.Zumbi;

/**
 * Rapido e com pouca vida. (valores provisorios: ajustar no balanceamento)
 *
 * @author thoma
 */
public class Trote extends Zumbi {

    public Trote(int linha) {
        super(100, linha, 0.006, 10);
    }
}
