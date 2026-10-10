package com.poo2026_2.model;

import com.poo2026_2.model.Entidade;

/**
 * Zumbi generico. A velocidade é dada em casas por "tick" de atualizacao.
 *
 * @author thoma
 */
public abstract class Zumbi extends Entidade {
    protected static final double INTERVALO_ATAQUE = 1.0;
    private double tempoDesdeMordida;
    private double velocidade;
    private int dano;
    private double posicaoX;
    private boolean atacando;

    public Zumbi(int vida, int linha, double velocidade, int dano) {
        super(vida, linha, 0);
        this.velocidade = velocidade;
        this.dano = dano;
        this.posicaoX = 0;
        this.atacando = false;
        this.tempoDesdeMordida = INTERVALO_ATAQUE;   // a primeira mordida é imediata
    }

    public double getVelocidade() {
        return velocidade;
    }

    protected void setVelocidade(double velocidade) {
        this.velocidade = velocidade;
    }

    public int getDano() {
        return dano;
    }

    protected void setDano(int dano) {
        this.dano = dano;
    }

    public double getPosicaoX() {
        return posicaoX;
    }

    public void setPosicaoX(double posicaoX) {
        this.posicaoX = posicaoX;
    }

    public boolean estaAtacando() {
        return atacando;
    }

    /** O zumbi anda para a esquerda enquanto nao estiver atacando. */
    public void mover(double deltaTime) {
        if (!atacando) {
            posicaoX -= velocidade * deltaTime;
        }
    }

    public void iniciarAtaque() {
        atacando = true;
    }

    public void pararAtaque() {
        atacando = false;
    }

    public boolean atacar(Entidade alvo, double deltaTime) {
    tempoDesdeMordida += deltaTime;
    if (tempoDesdeMordida >= INTERVALO_ATAQUE) {
        tempoDesdeMordida = 0;
        alvo.receberDano(dano);
        return true;
    }
    return false;
}
}
