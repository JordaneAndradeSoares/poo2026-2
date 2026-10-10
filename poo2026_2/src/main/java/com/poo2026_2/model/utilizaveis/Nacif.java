package com.poo2026_2.model.utilizaveis;

import com.poo2026_2.model.Professor;
import com.poo2026_2.model.Tiro;

/**
 * Atira Verilog. (valores provisorios: ajustar no balanceamento)
 *
 * @author thoma
 */
public class Nacif extends Professor {

    public Nacif(int linha, int coluna) {
        super(100, linha, coluna, 100, 1.5);
    }

    @Override
    public Tiro criarTiro() {
        return novoTiro(5.0, 20,
                "/images/tiros/verilog.png",
                "nenhum",
                "/audio/tiros/verilog.mp3");
    }
}
