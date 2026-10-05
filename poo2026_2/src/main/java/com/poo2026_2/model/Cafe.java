package com.poo2026_2.model;

/**
 * Representa um café produzido durante o jogo.
 *
 * O café pode ser produzido por uma Cafeteira ou
 * aparecer aleatoriamente no tabuleiro.
 *
 * @author Rafael
 */
public class Cafe {

    private int quantidade;
    private double posicaoX;
    private double posicaoY;
    private double tempoDeVida; //old temperatura

    public Cafe(int quantidade, double posicaoX, double posicaoY, double tempoDeVida) {
        this.quantidade = quantidade;
        this.posicaoX = posicaoX;
        this.posicaoY = posicaoY;
        this.tempoDeVida = tempoDeVida;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getPosicaoX() {
        return posicaoX;
    }

    public void setPosicaoX(double posicaoX) {
        this.posicaoX = posicaoX;
    }

    public double getPosicaoY() {
        return posicaoY;
    }

    public void setPosicaoY(double posicaoY) {
        this.posicaoY = posicaoY;
    }

    public double getTempoDeVida() {
        return tempoDeVida;
    }

    public void setTempoDeVida(double tempoDeVida) {
        this.tempoDeVida = tempoDeVida;
    }

    /**
     * Atualiza o tempo de vida do café.
     *
     * @param deltaTime tempo passado desde a última atualização
     * @return true se o café ainda estiver disponível,
     *         false se seu tempo de vida tiver terminado
     */
    public boolean atualizar(double deltaTime) {
        tempoDeVida -= deltaTime;

        return tempoDeVida > 0;
    }
}