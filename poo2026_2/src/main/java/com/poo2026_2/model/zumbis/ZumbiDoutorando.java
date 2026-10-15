package com.poo2026_2.model.zumbis;

public class ZumbiDoutorando extends Zumbi {
    
    // tanque (extrema vida, extremamente lento)
    public ZumbiDoutorando() {

        super(
            0.2,
            999999, // estou usando um dano alto para causar hitkill (idealmente devia usar o valor exata da vida do alvo)
            300,
            1_000_000_000L
        );
    }
}