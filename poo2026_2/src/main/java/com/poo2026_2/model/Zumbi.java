package com.poo2026_2.model;

//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;

public class Zumbi {

    private double velocidadeDoZumbi;
    private int danoDoZumbi;

    private double posicaoXDoZumbi;
    private double posicaoYDoZumbi;

    private boolean zumbiAtacando;

    public Zumbi(double velocidadeDoZumbi, int danoDoZumbi) {
        this.velocidadeDoZumbi = velocidadeDoZumbi;
        this.danoDoZumbi = danoDoZumbi;

        this.posicaoXDoZumbi = 0;
        this.posicaoYDoZumbi = 0;

        this.zumbiAtacando = false;
    }

    public void moverZumbi() {

        // zumbi anda enquanto não estiver atacando
        if (!zumbiAtacando) {
            posicaoXDoZumbi -= velocidadeDoZumbi;
        }
    }

    public void ataqueZumbi() {

        // zumbi ataca
        zumbiAtacando = true;
    }

    public void pararAtaqueZumbi() {

        // zumbi para de atacar
        zumbiAtacando = false;
    }

    // gets
    public double getPosicaoX() {
        return posicaoXDoZumbi;
    }

    public double getPosicaoY() {
        return posicaoYDoZumbi;
    }

    public double getVelocidadeDoZumbi() {
        return velocidadeDoZumbi;
    }

    public int getDanoDoZumbi() {
        return danoDoZumbi;
    }

    // sets
    public void setPosicaoX(double posicaoX) {
        this.posicaoXDoZumbi = posicaoX;
    }

    public void setPosicaoY(double posicaoY) {
        this.posicaoYDoZumbi = posicaoY;
    }

    public boolean zumbiEstaAtacando() {
        return zumbiAtacando;
    }
}