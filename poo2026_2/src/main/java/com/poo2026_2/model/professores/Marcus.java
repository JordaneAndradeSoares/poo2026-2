package com.poo2026_2.model.professores;

public class Marcus extends Professor {

    // Construtor
    public Marcus(int linhaNoTabuleiroDoProfessor, int colunaNoTabuleiroDoProfessor ) {

        super(
            150, 
            1_500_000_000, 
            4000, 
            linhaNoTabuleiroDoProfessor, 
            colunaNoTabuleiroDoProfessor
        );
            
        //setDanoDoProfessor(40); // Definindo o dano do professor Marcus
    }
}