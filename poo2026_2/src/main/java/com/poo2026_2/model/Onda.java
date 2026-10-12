/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.poo2026_2.model;

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

    public Onda(int numero, int quantidadeZumbis, double intervaloSpawn) {
        this.numero = numero;
        this.quantidadeZumbis = quantidadeZumbis;
        this.intervaloSpawn = intervaloSpawn;
        this.tempoDecorrido = 0;
        this.zumbisGerados = 0;
        this.concluida = quantidadeZumbis <= 0;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getQuantidadeZumbis() {
        return quantidadeZumbis;
    }

    public void setQuantidadeZumbis(int quantidadeZumbis) {
        this.quantidadeZumbis = quantidadeZumbis;
    }

    public double getIntervaloSpawn() {
        return intervaloSpawn;
    }

    public void setIntervaloSpawn(double intervaloSpawn) {
        this.intervaloSpawn = intervaloSpawn;
    }

    /** Avança o relógio da onda em segundos. */
    public void atualizar(double delta) {
        if (concluida) {
            return;
        }
        tempoDecorrido += delta;
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
        tempoDecorrido = 0;
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

    public boolean estaConcluida() {
        return concluida;
    }
}
