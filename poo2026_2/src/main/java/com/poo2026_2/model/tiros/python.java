package com.poo2026_2.model.tiros;

import com.poo2026_2.model.zumbis.Zumbi;

public class python extends tiro {

    public python(double x, double y, double dano, double velocidade) {

        super(x, y, dano, velocidade);
    }

    // efeito do python
    // o booleano é necessario para o zumbi não ficar com velocidade zero ou menos
    public void aplicarLentidaoNoZumbi(Zumbi zumbi) {

        System.out.println("Zumbi lento");

        if (zumbi.getZumbiLento()) {
            
            double velocidadeAtual = zumbi.getVelocidadeDoZumbi();
            double novaVelocidade = velocidadeAtual * 0.5;

            zumbi.setVelocidadeDoZumbi(novaVelocidade);
        }

        zumbi.setZumbiLento(false);
    }

    
    @Override
    public String getCaminhoImagem() {

        // fonte da imagem (acesso em 14/10/2026): https://upload.wikimedia.org/wikipedia/commons/c/c3/Python-logo-notext.svg?utm_source=commons.wikimedia.org&utm_campaign=index&utm_content=original
        return "/images/tiros/Python.png";
    }
}