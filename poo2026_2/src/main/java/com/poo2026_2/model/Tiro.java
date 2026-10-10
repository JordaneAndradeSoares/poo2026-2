/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.poo2026_2.model;

/**
 *
 * @author thoma
 * Tiro disparado por um professor. Todos os tipos de tiro (Python, C, ...)
 * usam esta mesma classe; o que muda sao os valores passados ao construtor.
 */
public class Tiro {

    private double x;
    private double y;
    private double velocidade;
    private int dano;
    private boolean ativo;
    private String sprite;
    private String efeitoVisual;
    private String som;

    public Tiro(double x, double y, double velocidade, int dano,
                String sprite, String efeitoVisual, String som) {
        this.x = x;
        this.y = y;
        this.velocidade = velocidade;
        this.dano = dano;
        this.sprite = sprite;
        this.efeitoVisual = efeitoVisual;
        this.som = som;
        this.ativo = true;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getVelocidade() {
        return velocidade;
    }

    public void setVelocidade(double velocidade) {
        this.velocidade = velocidade;
    }

    public int getDano() {
        return dano;
    }

    public void setDano(int dano) {
        this.dano = dano;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getSprite() {
        return sprite;
    }

    public String getEfeitoVisual() {
        return efeitoVisual;
    }

    public String getSom() {
        return som;
    }

    /** Anda para a direita (os zumbis vem da direita). */
    public void mover(double deltaTime) {
        if (ativo) {
            x += velocidade * deltaTime;
        }
    }
    /** Causa dano no alvo e some. */
    public void atingir(Entidade alvo) {
        alvo.receberDano(dano);
        desativar();
    }

    public void desativar() {
        ativo = false;
    }
}
