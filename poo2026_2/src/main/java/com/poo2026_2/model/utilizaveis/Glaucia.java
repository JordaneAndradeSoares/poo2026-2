package com.poo2026_2.model.utilizaveis;

import com.poo2026_2.model.Professor;
import com.poo2026_2.model.Tiro;

/**
 * Atira JavaScript. Disparo de fogo: alem do dano, queima o zumbi.
 *
 * @author thoma
 */
public class Glaucia extends Professor {

    private static final int CUSTO_CAFE = 100;
    private static final double TEMPO_DE_RECARGA = 8;  
    private static final double CADENCIA = 1.5;    

    private static final int DANO = 40;
    private static final double VELOCIDADE_TIRO = 5.0;

    private static final double DANO_QUEIMADURA_POR_SEGUNDO = 10;
    private static final double DURACAO_QUEIMADURA = 2.0; 

    public Glaucia(int linha, int coluna) {
        super(linha, coluna, CUSTO_CAFE, CADENCIA);
    }

    @Override
    public Tiro criarTiro() {
        return novoTiro(
            VELOCIDADE_TIRO,
            DANO,
            "/images/tiros/javaScript.png",
            "fogo",
            "/audio/tiros/javaScript.mp3",
            zumbi -> zumbi.aplicarQueimadura(DANO_QUEIMADURA_POR_SEGUNDO, DURACAO_QUEIMADURA)
        );
    }

    public double getTempoDeRecarga() {
        return this.TEMPO_DE_RECARGA;
    }
}
