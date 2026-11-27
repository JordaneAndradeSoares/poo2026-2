package com.poo2026_2.model;

import java.util.ArrayList;
import java.util.List;

import com.poo2026_2.controller.SelecaoNivelController;

import java.util.Collections;

public class Fase {

    private int numero;
    private String nome;
    private int cafeInicial;
    private Cronograma cronograma;

    private List<Class<? extends Professor>> professorDisponiveis;
    
    // =================================================================

    /** Monta a fase escolhida na tela de selecao, lendo o niveis.json. */
    public static Fase criarFaseSelecionada() {
        return CatalogoNiveis.obter(SelecaoNivelController.faseSelecionada).criarFase();
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
        
        professorDisponiveis = new ArrayList<>();
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

    /** A linha do tempo da fase: quando nasce cada zumbi. */
    public Cronograma getCronograma() {
        if (cronograma == null) {
            throw new IllegalStateException("A fase " + numero + " ainda nao tem cronograma.");
        }
        return cronograma;
    }

    public void setCronograma(Cronograma cronograma) {
        if (cronograma == null) {
            throw new IllegalArgumentException("O cronograma nao pode ser nulo.");
        }
        this.cronograma = cronograma;
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

    /** A fase acaba quando o ultimo zumbi ja nasceu (e o jogador derrotou todos). */
    public boolean estaConcluida() {
        return cronograma != null && cronograma.estaConcluido();
    }

    public void reiniciar() {
        if (cronograma != null) {
            cronograma.reiniciar();
        }
    }
}
