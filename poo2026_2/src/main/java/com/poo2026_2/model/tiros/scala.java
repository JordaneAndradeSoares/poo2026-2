package com.poo2026_2.model.tiros;

import com.poo2026_2.model.zumbis.Zumbi;

public class scala extends tiro {

    public scala(double x, double y, double dano, double velocidade) {

        super(x, y, dano, velocidade);
    }

    @Override
    public String getCaminhoImagem() {

        // fonte da imagem (acesso em 14/10/2026): https://pt.wikipedia.org/wiki/Ficheiro:Scala-full-color.svg
        return "/images/tiros/scala.png";
    }
}