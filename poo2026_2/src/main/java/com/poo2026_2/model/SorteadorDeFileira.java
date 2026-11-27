package com.poo2026_2.model;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Random;

public final class SorteadorDeFileira {

    private final int fileiras;
    private final int janela;      // quantos spawns recentes contam
    private final double expoente; // quanto penaliza fileira cheia (maior = mais equilibrado)
    private final Random sorteio;
    private final int[] contagem;
    private final Deque<Integer> recentes = new ArrayDeque<>();

    public SorteadorDeFileira(int fileiras, int janela, double expoente, Random sorteio) {
        if (fileiras <= 0 || janela <= 0 || expoente < 0 || sorteio == null) {
            throw new IllegalArgumentException("Parametros invalidos para o sorteador de fileira.");
        }
        this.fileiras = fileiras;
        this.janela = janela;
        this.expoente = expoente;
        this.sorteio = sorteio;
        this.contagem = new int[fileiras];
    }

    /** Sorteia a fileira (0 a fileiras-1) do proximo zumbi. */
    public int proxima() {
        double[] pesos = new double[fileiras];
        double total = 0;
        for (int f = 0; f < fileiras; f++) {
            pesos[f] = 1.0 / Math.pow(1 + contagem[f], expoente);
            total += pesos[f];
        }

        double r = sorteio.nextDouble() * total;
        int escolhida = fileiras - 1;
        for (int f = 0; f < fileiras; f++) {
            r -= pesos[f];
            if (r < 0) {
                escolhida = f;
                break;
            }
        }

        contagem[escolhida]++;
        recentes.addLast(escolhida);
        if (recentes.size() > janela) {
            contagem[recentes.removeFirst()]--; 
        }
        return escolhida;
    }

    public void reiniciar() {
        recentes.clear();
        Arrays.fill(contagem, 0);
    }
}