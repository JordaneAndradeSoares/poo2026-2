/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.poo2026_2.model;

/**
 *
 * @author thoma Professor generico. Cada professor concreto define o seu tiro.
 */
public abstract class Professor extends Entidade {

    private double tempoDesdeUltimoTiro = 0.0;

    private int custoCafe;
    private double cadenciaTiro; // segundos entre um tiro e outro

    public Professor(int vida, int linha, int coluna, int custoCafe, double cadenciaTiro) {
        super(vida, linha, coluna);
        this.custoCafe = custoCafe;
        this.cadenciaTiro = cadenciaTiro;
    }

    public int getCustoCafe() {
        return custoCafe;
    }

    public double getCadenciaTiro() {
        return cadenciaTiro;
    }

    /**
     * Cada professor diz qual tiro dispara.
     */
    public abstract Tiro criarTiro();

    public boolean podeAtacar(double deltaTime) {
        tempoDesdeUltimoTiro += deltaTime;
        if (estaVivo() && tempoDesdeUltimoTiro >= cadenciaTiro) {
            tempoDesdeUltimoTiro = 0;
            return true;
        }
        return false;
    }

    public Tiro atacar() {
        tempoDesdeUltimoTiro = 0.0;
        return criarTiro();
    }

    /* Cria um tiro na posicao do professor com os valores informados.*/
    protected Tiro novoTiro(double velocidade, int dano, String sprite, String efeitoVisual, String som) {
        return new Tiro(getColuna(), getLinha(), velocidade, dano, sprite, efeitoVisual, som);
    }

    public void atualizarTempo(double deltaTime) {
        tempoDesdeUltimoTiro += deltaTime;
    }

    public boolean podeAtacar() {
        return estaVivo() && tempoDesdeUltimoTiro >= cadenciaTiro;
    }
}
