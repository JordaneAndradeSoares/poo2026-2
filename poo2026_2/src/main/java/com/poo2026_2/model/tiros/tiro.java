package com.poo2026_2.model.tiros;

import com.poo2026_2.model.zumbis.Zumbi;

public class tiro {

    private double posicaoXDoTiro;
    private double posicaoYDoTiro;
    private double velocidadeDoTiro;
    private double danoDoTiro;

    public tiro(double posicaoXDoTiro, double posicaoYDoTiro, double danoDoTiro, double velocidadeDoTiro) {
        this.posicaoXDoTiro = posicaoXDoTiro;
        this.posicaoYDoTiro = posicaoYDoTiro;
        this.danoDoTiro = 200; //this.danoDoTiro = danoDoTiro;
        this.velocidadeDoTiro = velocidadeDoTiro;
    }

    public double getPosicaoXDoTiro() {return posicaoXDoTiro;}
    public double getPosicaoYDoTiro() {return posicaoYDoTiro;}
    public double getDanoDoTiro() {return danoDoTiro;} 

    public boolean tiroEstaColidindo(Zumbi zumbi) {

        double xEsquerda = zumbi.getPosicaoXDoZumbi() + 50; // Distância horizontal entre a origem do zumbi e o início da hitbox
        double xDireita = zumbi.getPosicaoXDoZumbi() + 250; // Distância horizontal entre a origem do zumbi e o fim da hitbox

        double yTopo = zumbi.getPosicaoYDoZumbi(); // Limite superior da hitbox
        double yBase = zumbi.getPosicaoYDoZumbi() + 130; // Limite inferior fica

        return (this.posicaoXDoTiro >= xEsquerda && this.posicaoXDoTiro <= xDireita && this.posicaoYDoTiro >= yTopo && this.posicaoYDoTiro <= yBase);
    }

    public void moverTiro() {

        // O tiro se move APENAS para a direita no tabuleiro, então só precisamos atualizar a coluna do tabuleiro
        posicaoXDoTiro += velocidadeDoTiro;
    }

    public String getCaminhoImagem() {

        return "/images/tiros/tiro.png";
    }
}