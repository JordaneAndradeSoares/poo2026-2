package com.poo2026_2.model.zumbis;

/**
 * Minitanque: muita vida e lento. (valores provisorios: ajustar no balanceamento)
 *
 * @author thoma
 */
public class Formando extends Zumbi {

    public Formando(int linha) {
        super(5000, linha, VELOCIDADE_PADRAO, DANO_HITKILL);
    }
}