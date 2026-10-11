package com.poo2026_2.model.tiros;

import java.security.interfaces.DSAKey;

import com.poo2026_2.model.zumbis.Zumbi;

public class tiro {

    private double posicaoXDoTiro;
    private double posicaoYDoTiro;
    private double velocidadeDoTiro;
    private double danoDoTiro;

    public tiro(double posicaoXDoTiro, double posicaoYDoTiro, double danoDoTiro, double velocidadeDoTiro) {
        this.posicaoXDoTiro = posicaoXDoTiro;
        this.posicaoYDoTiro = posicaoYDoTiro;
        this.danoDoTiro = danoDoTiro;
        this.velocidadeDoTiro = velocidadeDoTiro;
    }

    public double getPosicaoXDoTiro() {return posicaoXDoTiro;}
    public double getPosicaoYDoTiro() {return posicaoYDoTiro;}
    public double getDanoDoTiro() {return danoDoTiro;} 

    public boolean tiroEstaColidindo(Zumbi zumbi) {

        /*
        // 100 é o tamanho do zumbi
        double X = zumbi.getPosicaoXDoZumbi() + 100;
        double Y = zumbi.getPosicaoYDoZumbi() + 100;

        // Verifica se o tiro está na mesma linha e coluna do zumbi
        if ((this.posicaoXDoTiro >= zumbi.getPosicaoXDoZumbi() && this.posicaoXDoTiro <= X) &&
            (this.posicaoYDoTiro >= zumbi.getPosicaoYDoZumbi() && this.posicaoYDoTiro <= Y)) {
        */

        double xEsquerda = zumbi.getPosicaoXDoZumbi() + 50; // Distância horizontal entre a origem do zumbi e o início da hitbox
        double xDireita = zumbi.getPosicaoXDoZumbi() + 250; // Distância horizontal entre a origem do zumbi e o fim da hitbox

        double yTopo = zumbi.getPosicaoYDoZumbi() - 100; // Limite superior da hitbox fica 100 pixels acima da posição Y do modelo
        double yBase = zumbi.getPosicaoYDoZumbi() + 170; // Limite inferior fica 170 pixels abaixo da posição Y do modelo

        return (this.posicaoXDoTiro >= xEsquerda && this.posicaoXDoTiro <= xDireita && this.posicaoYDoTiro >= yTopo && this.posicaoYDoTiro <= yBase);
    }

    public void moverTiro() {

        // O tiro se move APENAS para a direita no tabuleiro, então só precisamos atualizar a coluna do tabuleiro
        posicaoXDoTiro += velocidadeDoTiro;
    }
}