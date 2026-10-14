package com.poo2026_2.model.tiros;

import com.poo2026_2.model.zumbis.Zumbi;

public class c extends tiro {

    public c(double x, double y, double dano, double velocidade) {

        super(x, y, dano, velocidade);
    }

    @Override
    public String getCaminhoImagem() {

        // fonte da imagem (acesso em 14/10/2026): https://upload.wikimedia.org/wikipedia/commons/1/19/C_Logo.png?utm_source=pt.wikipedia.org&utm_campaign=index&utm_content=original
        return "/images/tiros/c.png";
    }
}