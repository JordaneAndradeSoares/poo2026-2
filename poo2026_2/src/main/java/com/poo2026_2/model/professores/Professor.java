package com.poo2026_2.model.professores;

import java.awt.Graphics;

import com.poo2026_2.model.zumbis.Zumbi; // Importação da classe Zumbi
import com.poo2026_2.model.tiros.tiro; // Importação da classe Tiro
import com.poo2026_2.controller.JogoController; // Importação da classe JogoController

public class Professor {

    private int custoCafe;

    private boolean esseProfessorDeveAtacarOsZumbis; // esse atributo define se um professor deve ou não atacar

    private long cadenciaTiroDoProfessor; // de quanto em quanto tempo o professor deve atacar
    private long ultimoTiro; // atributo para armazenar o tempo do último tiro

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
    public int getCustoCafe() {return this.custoCafe;}
    public double getDanoDoProfessor() {return this.danoDoProfessor;}
    public double getCadenciaTiro() {return this.cadenciaTiroDoProfessor;}
    public int getVidaDoProfessor() {return this.vidaDoProfessor;}
    public int getLinhaNoTabuleiroDoProfessor() {return this.linhaNoTabuleiroDoProfessor;}
    public int getColunaNoTabuleiroDoProfessor() {return this.colunaNoTabuleiroDoProfessor;}
    public boolean getEsseProfessorDeveAtacarOsZumbis() {return this.esseProfessorDeveAtacarOsZumbis;}

    public void setEsseProfessorDeveAtacarOsZumbis(boolean esseProfessorDeveAtacarOsZumbis) {this.esseProfessorDeveAtacarOsZumbis = esseProfessorDeveAtacarOsZumbis;}
    public void setCustoCafe(int custoCafe) {this.custoCafe = custoCafe;}
    public void setCadenciaTiroProfessor(long cadenciaTiroDoProfessor) {this.cadenciaTiroDoProfessor = cadenciaTiroDoProfessor;}
    public void setVidaDoProfessor(int vidaDoProfessor) {this.vidaDoProfessor = vidaDoProfessor;}
    public void setColunaNoTabuleiroDoProfessor(int colunaNoTabuleiroDoProfessor) {this.colunaNoTabuleiroDoProfessor = colunaNoTabuleiroDoProfessor;}
    public void setLinhaNoTabuleiroDoProfessor(int linhaNoTabuleiroDoProfessor) {this.linhaNoTabuleiroDoProfessor = linhaNoTabuleiroDoProfessor;}

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
                1 // Velocidade do tiro
            ); 
        } 
        
        // retorna null se o professor não estiver vivo, indicando que ele não pode atacar
        return null; 
    }

    // Verifica se o professor pode atirar com base na cadência de tiro
    public boolean podeAtirar(long agora) {

        if (agora - ultimoTiro >=  cadenciaTiroDoProfessor) {
            
            ultimoTiro = agora; // Atualiza o tempo do último tiro
            return true; // Permite o tiro
        }
        
        return false; // Retorna false se o professor não pode atirar
    }

    public void desenharProfessor(Graphics graphics, int x, int y) {

        // Desenha um retângulo azul representando o professor
        graphics.setColor(java.awt.Color.BLUE);
        graphics.fillRect(x, y, 20, 20); 
    }
}