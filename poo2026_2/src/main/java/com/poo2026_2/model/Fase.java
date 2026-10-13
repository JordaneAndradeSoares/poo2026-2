package com.poo2026_2.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Fase {

    private int numero;
    private String nome;
    private int cafeInicial;
    private List<Onda> ondas;
    private int indiceOndaAtual;

    private List<Class<? extends Professor>> professorDisponiveis;

    public Fase(int numero, String nome, int cafeInicial) {
        if (numero <= 0) {
            throw new IllegalArgumentException("O numero tem que ser maior que zero.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome nao pode ser nulo.");
        }
        if (cafeInicial < 0) {
            throw new IllegalArgumentException("O cafe inicial nao pode ser negativo.");
        }

        this.numero = numero;
        this.nome = nome;
        this.cafeInicial = cafeInicial;
        
        ondas = new ArrayList<>();
        professorDisponiveis = new ArrayList<>();
        indiceOndaAtual = 0;
    }

    public int getNumero() {
        return numero;
    }

    public String getNome() {
        return nome;
    }

    public int getCafeInicial() {
        return cafeInicial;
    }

    public List<Onda> getOndas() {
        return Collections.unmodifiableList(new ArrayList<>(ondas));
    }

    public void adicionarOnda(Onda onda) {
        if (onda == null) {
            throw new IllegalArgumentException("A onda nao pode ser nula.");
        }
        ondas.add(onda);
    }

    public void removerOnda(Onda onda) {
        ondas.remove(onda);
    }

    public List<Class<? extends Professor>> getProfessorDisponiveis() {
        return Collections.unmodifiableList(new ArrayList<>(professorDisponiveis));
    }


    public void adicionarProfessorDisponivel(
            Class<? extends Professor> professor) {
        if (professor == null) {
            throw new IllegalArgumentException("A classe do professor nao pode ser nula.");
        }
        professorDisponiveis.add(professor);
    }


    public void removerProfessorDisponivel(
            Class<? extends Professor> professor) {

        professorDisponiveis.remove(professor);
    }

    public Onda getOndaAtual() {
        while (indiceOndaAtual < ondas.size() && ondas.get(indiceOndaAtual).estaConcluida()) {
            indiceOndaAtual++;
        }

        if (indiceOndaAtual >= ondas.size()) {
            return null;
        }

        return ondas.get(indiceOndaAtual);
    }

    public boolean estaConcluida() {

        if (ondas.isEmpty()) {
            return false;
        }

        return getOndaAtual() == null;
    }

    public void reiniciar() {
        indiceOndaAtual = 0;

        for (Onda onda : ondas) {
            onda.reiniciar();
        }
    }
}