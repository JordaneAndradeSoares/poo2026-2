package com.poo2026_2.model.utilizaveis;

import com.poo2026_2.model.Professor;
import com.poo2026_2.model.Tiro;

/**
 * Atira Python. O tiro causa lentidao no zumbi atingido.
 *
 * @author thoma
 */
public class Philipe extends Professor {

    private static final int CUSTO_CAFE = 150;
    private static final double TEMPO_DE_RECARGA = 8; 
    private static final double CADENCIA = 1.425;     

    private static final int DANO = 20;
    private static final double VELOCIDADE_TIRO = 5.0;

    private static final double REDUCAO_VELOCIDADE = 0.50; 
    private static final double DURACAO_LENTIDAO = 3.0;    

    public Philipe(int linha, int coluna) {
        super(linha, coluna, CUSTO_CAFE, CADENCIA);
    }

    @Override
    public Tiro criarTiro() {
        return novoTiro(
            VELOCIDADE_TIRO,
            DANO,
            "/images/tiros/Python.png",
            "lentidao",
            "/audio/tiros/tiro2.mp3",
            zumbi -> zumbi.aplicarLentidao(REDUCAO_VELOCIDADE, DURACAO_LENTIDAO)
        );
    }

    public double getTempoDeRecarga() {
        return this.TEMPO_DE_RECARGA;
    }
}
