package com.poo2026_2.model.tiros;

import com.poo2026_2.model.zumbis.Zumbi;

public class javaScript extends tiro {

    public javaScript(double x, double y, double dano, double velocidade) {

        super(x, y, dano, velocidade);
    }
  
    @Override
    public String getCaminhoImagem() {

        // fonte da imagem (acesso em 14/10/2026): https://pt.wikipedia.org/wiki/Ficheiro:JavaScript-logo.png
        return "/images/tiros/javaScript.png";
    }
}