package com.poo2026_2.model.professores;

import java.awt.Graphics;

import com.poo2026_2.model.zumbis.Zumbi; // Importação da classe Zumbi
import com.poo2026_2.model.tiros.tiro; // Importação da classe Tiro
import com.poo2026_2.controller.JogoController; // Importação da classe JogoController

public class Professor {

    // Atributos do diagrama
    private int custoCafe;

    private long cadenciaTiroDoProfessor;
    private long ultimoTiro; // Atributo para armazenar o tempo do último tiro

    private double danoDoProfessor; // os professores podem atacar os zumbis, então eles têm que ter dano (sei que esse valor não estava no diagrama)
    private int vidaDoProfessor; // os professores podem ser atacados pelos zumbis, então eles têm que ter vida (sei que esse valor não estava no diagrama)
    
    // Localização do professor no tabuleiro
    private int linhaNoTabuleiroDoProfessor;
    private int colunaNoTabuleiroDoProfessor;

    // Construtor
    public Professor(int custoCafe, long cadenciaTiroDoProfessor, int vidaDoProfessor, int linhaNoTabuleiroDoProfessor, int colunaNoTabuleiroDoProfessor) {
        this.custoCafe = custoCafe;
        this.cadenciaTiroDoProfessor = cadenciaTiroDoProfessor;
        this.vidaDoProfessor = vidaDoProfessor;
        this.linhaNoTabuleiroDoProfessor = linhaNoTabuleiroDoProfessor;
        this.colunaNoTabuleiroDoProfessor = colunaNoTabuleiroDoProfessor;
    }

    // Getters e Setters
    public int getCustoCafe() {return custoCafe;}
    public double getDanoDoProfessor() {return danoDoProfessor;}
    public double getCadenciaTiro() {return cadenciaTiroDoProfessor;}
    public int getVidaDoProfessor() {return vidaDoProfessor;}
    public int getLinhaNoTabuleiroDoProfessor() {return linhaNoTabuleiroDoProfessor;}
    public int getColunaNoTabuleiroDoProfessor() {return colunaNoTabuleiroDoProfessor;}

    public void setCustoCafe(int custoCafe) {this.custoCafe = custoCafe;}
    public void setCadenciaTiroProfessor(long cadenciaTiroDoProfessor) {this.cadenciaTiroDoProfessor = cadenciaTiroDoProfessor;}
    public void setVidaDoProfessor(int vidaDoProfessor) {this.vidaDoProfessor = vidaDoProfessor;}
    public void setColunaNoTabuleiroDoProfessor(int colunaNoTabuleiroDoProfessor) {this.colunaNoTabuleiroDoProfessor = colunaNoTabuleiroDoProfessor;}
    public void setLinhaNoTabuleiroDoProfessor(int linhaNoTabuleiroDoProfessor) {this.linhaNoTabuleiroDoProfessor = linhaNoTabuleiroDoProfessor;}
    public void setDanoDoProfessor(double danoDoProfessor) {this.danoDoProfessor = danoDoProfessor;}

    public void professorRecebeDano(double dano) {
        this.vidaDoProfessor -= dano;

        if (this.vidaDoProfessor < 0) {
            this.vidaDoProfessor = 0; // Evita que a vida fique negativa
        }
    }

    public boolean professorEstaVivo() {
        return this.vidaDoProfessor > 0;
    }

    // fonte (acesso em 01/10/2026): https://www.youtube.com/watch?v=qUp85ujb_sE
    public tiro professorAtacaZumbi(Zumbi zumbi, double posicaoXDoTiro, double posicaoYDoTiro) {

        if (professorEstaVivo() == true) {
                return new tiro(
                    posicaoXDoTiro, 
                    posicaoYDoTiro,
                    this.danoDoProfessor, 
                    1 // Velocidade do tiro é 1
            ); 
        } 
        
        return null; // retorna null se o professor não estiver vivo, indicando que ele não pode atacar
    }

    public boolean podeAtirar(long agora) {

        // Verifica se o professor pode atirar com base na cadência de tiro
        if (agora - ultimoTiro >=  cadenciaTiroDoProfessor) {
            ultimoTiro = agora; // Atualiza o tempo do último tiro
            return true; // Permite o tiro
        }
        
        return false; // Retorna false se o professor não pode atirar
    }

    /*
    public void desenharAtacarZumbi(Graphics graphics, int x, int y) {

        // Desenha o ataque do professor
        graphics.setColor(java.awt.Color.RED);
        graphics.fillRect(x, y, 20, 20); 
    }
    
    public java.awt.Rectangle getBounds(double larguraDaCelula, double alturaDaCelula) {
        return new java.awt.Rectangle(
            this.linhaNoTabuleiroDoProfessor * alturaDaCelula,
            this.colunaNoTabuleiroDoProfessor * larguraDaCelula,
            larguraDaCelula, 
            alturaDaCelula
        );
    }
    */

    public void desenharProfessor(Graphics graphics, int x, int y) {

        // Desenha um retângulo azul representando o professor
        graphics.setColor(java.awt.Color.BLUE);
        graphics.fillRect(x, y, 20, 20); 
    }
}