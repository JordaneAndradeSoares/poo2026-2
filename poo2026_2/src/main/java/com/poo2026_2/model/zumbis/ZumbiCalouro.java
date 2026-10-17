package com.poo2026_2.model.zumbis;

public class ZumbiCalouro extends Zumbi {

    // construtor para quando zumbi calouro for chamado diretamente
    public ZumbiCalouro() {

        super(
            0.6,
            40,
            200,
            1_000_000_000L
        );
    }

    // construtor para quando for chamado dos seus filhos
    public ZumbiCalouro(
            double velocidadeDoZumbi,
            double danoDoZumbi,
            double vidaDoZumbi,
            long cadenciaDoZumbi
        ) {

        super(
            velocidadeDoZumbi,
            danoDoZumbi,
            vidaDoZumbi,
            cadenciaDoZumbi
        );
    }
}