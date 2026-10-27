package com.poo2026_2.model.utilizaveis;

import com.poo2026_2.model.Professor;
import com.poo2026_2.model.Tiro;

/**
 * Atira Scala. (valores provisorios: ajustar no balanceamento)
 *
 * @author thoma
 */
public class Fabricio extends Professor {

    private static final int CUSTO_CAFE = 100;
    private static final double TEMPO_DE_RECARGA = 8;

    public Fabricio(int linha, int coluna) {
        super(100, linha, coluna, CUSTO_CAFE, 1.5);
    }

    @Override
    public Tiro criarTiro() {
        return novoTiro(
            5.0, 
            20,
            "/images/tiros/scala.png",
            "nenhum",
            "/audio/tiros/scala.mp3");
    }

    public int getCustoCafe() {
        return this.CUSTO_CAFE;    
    }

    public double getTempoDeRecarga() {
        return this.TEMPO_DE_RECARGA;
    }
}
