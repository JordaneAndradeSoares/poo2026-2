package com.poo2026_2.model.zumbis;

public class ZumbiFormando extends Zumbi {
    
    // minitanque (bastante vida, lento)
    public ZumbiFormando() {

        super(
            0.4,
            999999, // estou usando um dano alto para causar hitkill (idealmente deveria usar o valor exata da vida do alvo)
            1670,
            1_000_000_000L
        );
    }
}