package com.poo2026_2.model.zumbis;

public class ZumbiCalouro extends Zumbi {

    /*
    public ZumbiCalouro(double velocidadeDoZumbi, double danoDoZumbi, double vidaDoZumbi, long cadenciaDoZumbi) {
        this.velocidadeDoZumbi = velocidadeDoZumbi;
        this.danoDoZumbi = danoDoZumbi;

        this.vidaDoZumbi = vidaDoZumbi; // Valor inicial de vida do zumbi
        this.cadenciaDoZumbi = cadenciaDoZumbi; // Espaço entre os ataques em nanossegundos (lembre de usar o L no final)

        this.posicaoXDoZumbi = 0;
        this.posicaoYDoZumbi = 0;

        this.zumbiAtacando = false;
        this.ZumbiFoiAtingido = false;
    } 
    */

    // construtor para quando zumbi calouro for chamado diretamente
    public ZumbiCalouro() {

        super(
            0.6,
            40,
            100,
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