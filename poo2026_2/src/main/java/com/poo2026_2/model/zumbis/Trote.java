package com.poo2026_2.model.zumbis;

/**
 * Rapido e com pouca vida. (valores provisorios: ajustar no balanceamento)
 *
 * @author thoma
 */
public class Trote extends Zumbi {

    public Trote(int linha) {
        super(400, linha, VELOCIDADE_PADRAO * 2.5, DANO_PADRAO);
    }
}