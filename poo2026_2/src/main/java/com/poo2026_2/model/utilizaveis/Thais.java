package com.poo2026_2.model.utilizaveis;

import com.poo2026_2.model.Professor;
import com.poo2026_2.model.Tiro;

/**
 * Atira Go. A cada 4 tiros, o 4o paralisa o zumbi atingido por 2 segundos.
 * (valores provisorios: ajustar no balanceamento)
 *
 * @author thoma
 */
public class Thais extends Professor {

    private static final int CUSTO_CAFE = 125;
    private static final double TEMPO_DE_RECARGA = 8;

    private static final int TIROS_ATE_PARALISAR = 4;   // a cada quantos tiros um paralisa
    private static final double DURACAO_PARALISIA = 2.0; // segundos

    private int tirosDados = 0;

    public Thais(int linha, int coluna) {
        super(
            100, 
            linha, 
            coluna, 
            CUSTO_CAFE, 
            1.425
        );
    }

    @Override
    public Tiro criarTiro() {
        tirosDados++;
        if (tirosDados % TIROS_ATE_PARALISAR == 0) {
            return novoTiro(
                5.0,
                20,
                "/images/tiros/go.png",
                "paralisia",
                "/audio/tiros/tiro1.mp3",
                zumbi -> zumbi.aplicarParalisia(DURACAO_PARALISIA)
            );
        }
        return novoTiro(
            5.0, 
            20,
            "/images/tiros/go.png",
            "nenhum",
            "/audio/tiros/tiro1.mp3"
        );
    }

    public int getCustoCafe() {
        return this.CUSTO_CAFE;    
    }

    public double getTempoDeRecarga() {
        return this.TEMPO_DE_RECARGA;
    }
}