package com.poo2026_2.model.zumbis;

// Zumbi calouro blindado é uma versão mais resistente do zumbi calouro protegido
public class ZumbiCalouroBlindado extends ZumbiCalouro {
     
    // comum porém balde de ferro
    public ZumbiCalouroBlindado() {

        super(
            0.6,
            40,
            200,
            1_000_000_000L
        );
    }
}