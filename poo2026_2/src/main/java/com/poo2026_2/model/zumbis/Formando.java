package com.poo2026_2.model.zumbis;

import com.poo2026_2.model.Zumbi;

/**
 * Minitanque: muita vida e lento. (valores provisorios: ajustar no balanceamento)
 *
 * @author thoma
 */
public class Formando extends Zumbi {

    public Formando(int linha) {
        super(100, linha, 0.006, 10);
    }
}
