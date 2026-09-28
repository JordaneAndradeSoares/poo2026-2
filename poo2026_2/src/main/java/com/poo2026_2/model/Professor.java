package com.poo2026_2.model;

import java.util.List;

public class Professor {

    // Atributos do diagrama
    private int custoCafe;
    private double cadenciaTiro;
    
    // Atributos essenciais para a lógica de posição no grid
    private int linhaNoTabuleiro;
    private int colunaNoTabuleiro;

    // Construtor
    public Professor(int custoCafe, double cadenciaTiro, int linhaNoTabuleiro, int colunaNoTabuleiro) {
        this.custoCafe = custoCafe;
        this.cadenciaTiro = cadenciaTiro;
        this.linhaNoTabuleiro = linhaNoTabuleiro;
        this.colunaNoTabuleiro = colunaNoTabuleiro;
    }

    // TODO: tipo Inimigo
    public boolean podeAtacar(List<Inimigo> inimigosNoTabuleiro) {
        
        for (Inimigo inimigo : inimigosNoTabuleiro) {
            
            // Verifica se o inimigo está na MESMA LINHA que o professor
            boolean estaNaMesmaLinha = (inimigo.getLinhaNoTabuleiro() == this.linhaNoTabuleiro);
            
            // Verifica se o inimigo está NA FRENTE do professor 
            // (como o professor atira para a direita, a coluna do inimigo deve ser maior)
            boolean estaNaFrente = (inimigo.getColunaNoTabuleiro() > this.colunaNoTabuleiro);
            
            // Se encontrar qualquer inimigo que atenda às duas condições, o professor pode atacar!
            if (estaNaMesmaLinha && estaNaFrente) {
                return true;
            }
        }
        
        // Se percorreu toda a lista e não achou ninguém na frente e na mesma linha
        return false;
    }

    // Executa o ataque
    public void Atacar(List<Inimigo> inimigosNoTabuleiro) {
        if (podeAtacar(inimigosNoTabuleiro)) {
            System.out.println("Professor na linha " + linhaNoTabuleiro + " atacou!");

            // TODO: Lógica para instanciar o projétil passando a linhaNoTabuleiro
        }
    }

    // Getters e Setters
    public int getCustoCafe() {
        return custoCafe;
    }

    public void setCustoCafe(int custoCafe) {
        this.custoCafe = custoCafe;
    }

    public double getCadenciaTiro() {
        return cadenciaTiro;
    }

    public void setCadenciaTiro(double cadenciaTiro) {
        this.cadenciaTiro = cadenciaTiro;
    }
}