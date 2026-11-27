package com.poo2026_2.model.utilizaveis;

import com.poo2026_2.model.Professor;
import com.poo2026_2.model.Tiro;

/**
 * Atira C. Tiro padrao, sem efeito.
 *
 * @author thoma
 */
public class Daniel extends Professor {

    private static final int CUSTO_CAFE = 100;
    private static final double TEMPO_DE_RECARGA = 8;  
    private static final double CADENCIA = 1.425;        

    private static final int DANO = 20;
    private static final double VELOCIDADE_TIRO = 5.0;

    public Daniel(int linha, int coluna) {
        super(linha, coluna, CUSTO_CAFE, CADENCIA);
    }

    @Override
    public Tiro criarTiro() {
        return novoTiro(
            VELOCIDADE_TIRO,
            DANO,
            "/images/tiros/c.png",
            "nenhum",
            "/audio/tiros/tiro3.mp3");
    }

    public double getTempoDeRecarga() {
        return this.TEMPO_DE_RECARGA;
    }
}
