package com.poo2026_2.model.utilizaveis;

import com.poo2026_2.model.Professor;
import com.poo2026_2.model.Tiro;

/**
 * Atira Python. (valores provisorios: ajustar no balanceamento)
 *
 * @author thoma
 */
public class Philipe extends Professor {

    private static final int CUSTO_CAFE = 100;

    public Philipe(int linha, int coluna) {
        super(
            100, 
            linha, 
            coluna, 
            CUSTO_CAFE, 
            1.5
        );
    }

    @Override
    public Tiro criarTiro() {
        return novoTiro(
            5.0,
            20,
            "/images/tiros/python.png",
            "nenhum",
            "/audio/tiros/python.mp3"
        );
    }

    public int getCustoCafe() {
        return CUSTO_CAFE;
    }
}
