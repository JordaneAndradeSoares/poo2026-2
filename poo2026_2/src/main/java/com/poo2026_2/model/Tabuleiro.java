/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.poo2026_2.model;
import com.poo2026_2.model.utilizaveis.Cafeteira;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa o tabuleiro do jogo.
 *
 * @author Rafael
 */
public class Tabuleiro {

    private int linhas;
    private int colunas;
    private Celula[][] celulas;

    public Tabuleiro(int linhas, int colunas) {
        if (linhas <= 0 || colunas <= 0) {
            throw new IllegalArgumentException("Linhas e colunas devem ser positivas.");
        }

        this.linhas = linhas;
        this.colunas = colunas;
        this.celulas = new Celula[linhas][colunas];

        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                celulas[i][j] = new Celula(i, j, null);
            }
        }
    }

    public int getLinhas() {
        return linhas;
    }

    public int getColunas() {
        return colunas;
    }

    public Entidade getOcupante(int linha, int coluna) {
        if (!posicaoValida(linha, coluna)) {
            return null;
        }

        return celulas[linha][coluna].getOcupante();
    }

    public boolean instalar(Entidade entidade, int linha, int coluna) {

        if (!posicaoValida(linha, coluna) || entidade == null) {
            return false;
        }

        if (!(entidade instanceof Professor) && !(entidade instanceof Cafeteira)) {
            return false;
        }

        return celulas[linha][coluna].instalar(entidade);
    }

    public Entidade remover(int linha, int coluna) {
        if (!posicaoValida(linha, coluna)) {
            return null;
        }

        return celulas[linha][coluna].remover();
    }

    public boolean instalarProfessor(Professor professor, int linha, int coluna) {
        return instalar(professor, linha, coluna);
    }

    public Professor removerProfessor(int linha, int coluna) {
        if (!posicaoValida(linha, coluna)) {
            return null;
        }

        Celula celula = celulas[linha][coluna];
        Professor professor = celula.getProfessor();

        if (professor != null) {
            celula.removerProfessor();
        }

        return professor;
    }

    public boolean estaOcupada(int linha, int coluna) {
        return posicaoValida(linha, coluna) && celulas[linha][coluna].estaOcupada();
    }

    public List<Professor> getProfessores() {
        List<Professor> professores = new ArrayList<>();

        for (Celula[] linha : celulas) {
            for (Celula celula : linha) {
                if (celula.getOcupante() instanceof Professor) {
                    professores.add((Professor) celula.getOcupante());
                }
            }
        }

        return professores;
    }

    public List<Cafeteira> getCafeteiras() {
        List<Cafeteira> cafeteiras = new ArrayList<>();

        for (Celula[] linha : celulas) {
            for (Celula celula : linha) {
                if (celula.getOcupante() instanceof Cafeteira) {
                    cafeteiras.add((Cafeteira) celula.getOcupante());
                }
            }
        }

        return cafeteiras;
    }

    public void limpar() {
        for (Celula[] linha : celulas) {
            for (Celula celula : linha) {
                celula.remover();
            }
        }
    }

    private boolean posicaoValida(int linha, int coluna) {
        return linha >= 0 && linha < linhas
            && coluna >= 0 && coluna < colunas;
    }

}