package com.poo2026_2.model.professores;

public class Nacif extends Professor {

    // Construtor
    public Nacif(int linhaNoTabuleiroDoProfessor, int colunaNoTabuleiroDoProfessor) {
        super(50, 
            1_500_000_000L, 
            100, 
            linhaNoTabuleiroDoProfessor, 
            colunaNoTabuleiroDoProfessor);
            
        setDanoDoProfessor(40); // Definindo o dano do professor Nacif
    }
}