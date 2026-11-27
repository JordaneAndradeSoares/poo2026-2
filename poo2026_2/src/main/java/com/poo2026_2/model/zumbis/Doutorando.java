package com.poo2026_2.model.zumbis;

/**
 * Tanque: vida extrema e muito lento. (valores provisorios: ajustar no balanceamento)
 *
 * @author thoma
 */
public class Doutorando extends Zumbi {

    public Doutorando(int linha) {
        super(20000, linha, VELOCIDADE_PADRAO, DANO_HITKILL);
    }
}