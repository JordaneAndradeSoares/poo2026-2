package com.poo2026_2.model;

public class EventoExplosao {

    private final int linha;
    private final int coluna;

    public EventoExplosao(int linha, int coluna) {
        this.linha = linha;
        this.coluna = coluna;
    }

    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }
}