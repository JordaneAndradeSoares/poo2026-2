/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.poo2026_2.model;

import java.util.function.IntFunction;

/**
 *
 * @author Mateus
 */
public class Onda {

    private int numero;
    private int quantidadeZumbis;
    private double intervaloSpawn;   // segundos entre cada zumbi
    private double tempoDecorrido;   // tempo acumulado desde o último spawn
    private int zumbisGerados;
    private boolean concluida;

    private IntFunction<? extends Zumbi> criadorZumbi;

    public Onda(int numero, int quantidadeZumbis, double intervaloSpawn, IntFunction<? extends Zumbi> criadorZumbi) {
        
        if (numero <= 0) {
            throw new IllegalArgumentException("O numero de ondas tem que ser maior que zero");
        }

        if (quantidadeZumbis < 0) {
            throw new IllegalArgumentException("A quantidade de zumbis nao pode ser negativa.");
        }

        if (intervaloSpawn <= 0 || !Double.isFinite(intervaloSpawn)) {
            throw new IllegalArgumentException("O intervalo de spawn tem que ser positivo e finito.");
        }

        if (criadorZumbi == null) {
            throw new IllegalArgumentException("O tipo de zumbi deve ser informado.");
        }
        
        this.numero = numero;
        this.quantidadeZumbis = quantidadeZumbis;
        this.intervaloSpawn = intervaloSpawn;
        this.criadorZumbi = criadorZumbi;
        this.tempoDecorrido = 0;
        this.zumbisGerados = 0;
        this.concluida = quantidadeZumbis == 0;
    }

    public int getNumero() {
        return numero;
    }

    public int getQuantidadeZumbis() {
        return quantidadeZumbis;
    }

    public double getIntervaloSpawn() {
        return intervaloSpawn;
    }

    public int getZumbisGerados(){
        return zumbisGerados;
    }

    /** Avança o relógio da onda em segundos. */
    public void atualizar(double deltaTime) {
        if (!concluida && deltaTime > 0) {
            tempoDecorrido += deltaTime;
        }
    }

    /**
     * Registra a geração de um zumbi, reinicia o contador de tempo
     * e marca a onda como concluída quando todos foram gerados.
     */
    public boolean gerarZumbi() {
        if (!podeGerarZumbi()) {
            return false;
        }
        zumbisGerados++;
        tempoDecorrido -= intervaloSpawn;
        if (zumbisGerados >= quantidadeZumbis) {
            concluida = true;
        }
        return true;
    }

    public boolean podeGerarZumbi() {
        return !concluida
                && zumbisGerados < quantidadeZumbis
                && tempoDecorrido >= intervaloSpawn;
    }

    public Zumbi criarZumbi(int linha) {
        return criadorZumbi.apply(linha);
    }

    public boolean estaConcluida() {
        return concluida;
    }

    public void reiniciar() {
        tempoDecorrido = 0;
        zumbisGerados = 0;
        concluida = quantidadeZumbis == 0;
    }
}
