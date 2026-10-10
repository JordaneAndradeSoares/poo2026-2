package com.poo2026_2.model.zumbis;

import com.poo2026_2.model.Zumbi;

/**
 * Tanque: vida extrema e muito lento. (valores provisorios: ajustar no balanceamento)
 *
 * @author thoma
 */
public class Doutorando extends Zumbi {

    public Doutorando(int linha) {
        super(100, linha, 0.006, 10);
    }
}
