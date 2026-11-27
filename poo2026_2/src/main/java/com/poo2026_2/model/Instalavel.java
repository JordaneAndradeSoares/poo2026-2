package com.poo2026_2.model;

/**
 * Tudo que o jogador pode colocar numa casa do tabuleiro pagando café:
 * professores e cafeteiras. Serve para o botão de cada personagem (custo e
 * tempo de recarga) tratar todos do mesmo jeito, sem precisar saber qual é.
 */
public interface Instalavel {

    /** Café necessário para colocar na casa. */
    int getCustoCafe();

    /** Segundos até o botão ficar disponível de novo depois de usar. */
    double getTempoDeRecarga();
}
