package com.poo2026_2.model;

/**
 * Representa uma entidade presente no jogo.
 *
 * Uma entidade possui vida e uma posição no tabuleiro,
 * definida por linha e coluna.
 *
 * @author Rafael
 */
public abstract class Entidade {

    private int vida;
    private int linha;
    private int coluna;

    public Entidade(int vida, int linha, int coluna) {
        this.vida = vida;
        this.linha = linha;
        this.coluna = coluna;
    }

    public int getVida() {
        return vida;
    }

    public void setVida(int vida) {
        this.vida = vida;
    }

    public int getLinha() {
        return linha;
    }

    public void setLinha(int linha) {
        this.linha = linha;
    }

    public int getColuna() {
        return coluna;
    }

    public void setColuna(int coluna) {
        this.coluna = coluna;
    }

    /**
     * Verifica se a entidade ainda está viva.
     *
     * @return true caso a vida seja maior que zero
     */
    public boolean estaVivo() {
        return vida > 0;
    }

    /**
     * Aplica dano à entidade.
     *
     * A vida não pode ficar abaixo de zero.
     *
     * @param dano quantidade de dano recebido
     */
    public void receberDano(int dano) {
        vida -= dano;

        if (vida < 0) {
            vida = 0;
        }
    }
}