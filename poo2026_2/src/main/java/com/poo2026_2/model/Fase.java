package com.poo2026_2.model;

import java.util.ArrayList;
import java.util.List;

import com.poo2026_2.controller.SelecaoNivelController;
import com.poo2026_2.model.zumbis.Calouro;
import com.poo2026_2.model.zumbis.CalouroBlindado;
import com.poo2026_2.model.zumbis.CalouroProtegido;
import com.poo2026_2.model.zumbis.Doutorando;
import com.poo2026_2.model.zumbis.Formando;
import com.poo2026_2.model.zumbis.Trote;
import com.poo2026_2.model.zumbis.VibeCoder;
import com.poo2026_2.model.zumbis.Viciado;

import java.util.Collections;

public class Fase {

    private int numero;
    private String nome;
    private int cafeInicial;
    private List<Onda> ondas;
    private int indiceOndaAtual;

    private List<Class<? extends Professor>> professorDisponiveis;
    
    // =================================================================

    private static Fase criarFase1() {

        Fase fase = new Fase(1, "Início do semestre", 150);

        fase.adicionarOnda(new Onda(1, 4, 6.0, Calouro::new));
        fase.adicionarOnda(new Onda(2, 5, 6.0, Calouro::new));
        fase.adicionarOnda(new Onda(3, 3, 7.0, CalouroBlindado::new));
        fase.adicionarOnda(new Onda(4, 4, 6.0, CalouroProtegido::new));

        return fase;
    }

    private static Fase criarFase2() {

        Fase fase = new Fase(2, "Semana de provas", 200);

        fase.adicionarOnda(new Onda(1, 5, 5.0, CalouroBlindado::new));
        fase.adicionarOnda(new Onda(2, 4, 5.0, Trote::new));
        fase.adicionarOnda(new Onda(3, 4, 5.0, CalouroProtegido::new));
        fase.adicionarOnda(new Onda(4, 3, 5.0, Doutorando::new));
        fase.adicionarOnda(new Onda(5, 4, 4.0, Formando::new));
        fase.adicionarOnda(new Onda(6, 3, 4.0, VibeCoder::new));

        return fase;
    }

    private static Fase criarFase3() {

        Fase fase = new Fase(3, "Fim do semestre", 250);

        fase.adicionarOnda(new Onda(1, 5, 4.0, Calouro::new));
        fase.adicionarOnda(new Onda(2, 4, 4.0, CalouroBlindado::new));
        fase.adicionarOnda(new Onda(3, 4, 4.0, CalouroProtegido::new));
        fase.adicionarOnda(new Onda(4, 4, 3.5, Trote::new));
        fase.adicionarOnda(new Onda(5, 3, 3.5, Doutorando::new));
        fase.adicionarOnda(new Onda(6, 4, 3.0, Formando::new));
        fase.adicionarOnda(new Onda(7, 4, 3.0, VibeCoder::new));
        fase.adicionarOnda(new Onda(8, 3, 3.0, Viciado::new));

        return fase;
    }
        
    public static Fase criarFaseSelecionada() {
        int numero = SelecaoNivelController.faseSelecionada;

        switch (numero) {
            case 1:
                return criarFase1();

            case 2:
                return criarFase2();

            case 3:
                return criarFase3();

            default:
                return criarFase1();
        }
    }

    // =================================================================

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