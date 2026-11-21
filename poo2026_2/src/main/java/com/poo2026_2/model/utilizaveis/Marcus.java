package com.poo2026_2.model.utilizaveis;

import com.poo2026_2.model.Professor;

/**
 * Tanque (Matlab): nao atira, so segura os zumbis com muita vida.
 * @author thoma
 */
public class Marcus extends Professor {

    private static final int CUSTO_CAFE = 150;
    private static final double TEMPO_DE_RECARGA = 8;
    private static final int VIDA = 3000;

    public Marcus(int linha, int coluna) {
        super(VIDA, linha, coluna, CUSTO_CAFE, 0);
    }

    public static int getCustoCafeMarcus() {
        return CUSTO_CAFE;
    }
    
    public double getTempoDeRecarga() {
        return this.TEMPO_DE_RECARGA;
    }
}
