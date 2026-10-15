package com.poo2026_2.model.zumbis;

// Zumbi calouro protegido é uma versão mais resistente do zumbi calouro 
public class ZumbiCalouroProtegido extends ZumbiCalouro {
    
    // comum porém cone
    public ZumbiCalouroProtegido() {

        super(
            0.6,
            40,
            650,
            1_000_000_000L
        );
    }
}