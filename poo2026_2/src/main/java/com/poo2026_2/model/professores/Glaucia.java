package com.poo2026_2.model.professores;

public class Glaucia extends Professor {

    // Construtor
    public Glaucia(int linhaNoTabuleiroDoProfessor, int colunaNoTabuleiroDoProfessor) {
        super(50, 
            3_000_000_000L, 
            100, 
            linhaNoTabuleiroDoProfessor, 
            colunaNoTabuleiroDoProfessor);

        setDanoDoProfessor(40); // Definindo o dano do professor Glaucia
    }
}