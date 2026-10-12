package com.poo2026_2.model;

import java.util.ArrayList;
import java.util.List;

public class Fase {

    private int numero;
    private String nome;
    private int cafeInicial;
    private List<Onda> ondas;

    public List<Class<? extends Professor>> professorDisponiveis;


    public Fase() {
        ondas = new ArrayList<>();
        professorDisponiveis = new ArrayList<>();
    }


    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }


    public int getCafeInicial() {
        return cafeInicial;
    }


    public List<Onda> getOndas() {
        return ondas;
    }


    public void adicionarOnda(Onda onda) {
        ondas.add(onda);
    }

    public void removerOnda(Onda onda) {
        ondas.remove(onda);
    }


    public List<Class<? extends Professor>> getProfessorDisponiveis() {
        return professorDisponiveis;
    }


    public void adicionarProfessorDisponivel(
            Class<? extends Professor> professor) {

        professorDisponiveis.add(professor);
    }


    public void removerProfessorDisponivel(
            Class<? extends Professor> professor) {

        professorDisponiveis.remove(professor);
    }


    public boolean estaConcluida() {

        if (ondas.isEmpty()) {
            return false;
        }

        for (Onda onda : ondas) {
            if (!onda.estaConcluida()) {
                return false;
            }
        }

        return true;
    }
}